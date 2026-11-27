"""Correct wrong labels in the Grandma pages without changing their look.

    python tools/fix_text.py grandma/fixes.json            # all pages
    python tools/fix_text.py grandma/fixes.json 05          # one page
    python tools/fix_text.py grandma/fixes.json 05 --grid   # also save a coordinate grid

Each page in fixes.json has a list of operations on the original image:
    {"erase": [x0, y0, x1, y1]}                       paint over with the local background
    {"text": "NEW LABEL", "box": [x0, y0, x1, y1],    erase the box, then write text that
     "weight": 500, "align": "center", "rot": 0}       fits inside it (use \\n for lines;
                                                       "font": "comic" for rounded lettering)
    {"line": [x0, y0, x1, y1, width]}                 draw a straight ink line
Results go to grandma/fixed/.
"""
import json
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent.parent
FONT = ROOT / "fonts" / "fira-sans-condensed-latin-{w}-normal.woff"
INK = (28, 28, 28)


def background(img, box, pad=6):
    """Median color of the light pixels in a ring around the box."""
    x0, y0, x1, y1 = box
    ring = []
    px = img.load()
    W, H = img.size
    for x in range(max(0, x0 - pad), min(W, x1 + pad)):
        for y in (max(0, y0 - pad), min(H - 1, y1 + pad)):
            ring.append(px[x, y])
    for y in range(max(0, y0 - pad), min(H, y1 + pad)):
        for x in (max(0, x0 - pad), min(W - 1, x1 + pad)):
            ring.append(px[x, y])
    light = sorted((p for p in ring if sum(p) > 600), key=sum) or [(255, 255, 255)]
    return light[len(light) // 2]


def erase(img, box, pad=5):
    """Paint the box with a smooth fill interpolated from the paper around it, so the
    patch blends into the off-white page instead of showing a flat rectangle."""
    import numpy as np
    x0, y0, x1, y1 = box
    W, H = img.size
    x0, y0, x1, y1 = max(pad, x0), max(pad, y0), min(W - pad - 1, x1), min(H - pad - 1, y1)
    a = np.asarray(img).astype(float)
    flat = np.array(background(img, (x0, y0, x1, y1), pad), float)

    def paper(strip):  # strip: (n, k, 3) -> per-row/col light color, dark ink replaced by flat
        out = []
        for line in strip:
            light = line[line.sum(1) > 600]
            out.append(np.median(light, 0) if len(light) else flat)
        return np.array(out)

    top = paper(a[y0 - pad:y0, x0:x1 + 1].transpose(1, 0, 2))
    bot = paper(a[y1 + 1:y1 + 1 + pad, x0:x1 + 1].transpose(1, 0, 2))
    lef = paper(a[y0:y1 + 1, x0 - pad:x0])
    rig = paper(a[y0:y1 + 1, x1 + 1:x1 + 1 + pad])
    h, w = y1 - y0 + 1, x1 - x0 + 1
    ty = np.linspace(0, 1, h)[:, None, None]
    tx = np.linspace(0, 1, w)[None, :, None]
    vert = top[None] * (1 - ty) + bot[None] * ty
    horz = lef[:, None] * (1 - tx) + rig[:, None] * tx
    fill = (vert + horz) / 2
    a[y0:y1 + 1, x0:x1 + 1] = fill
    img.paste(Image.fromarray(a.clip(0, 255).astype("uint8")))


def write(img, op):
    box = op["box"]
    if op.get("erase", True):
        erase(img, op.get("erase_box", box))
    x0, y0, x1, y1 = box
    bw, bh = x1 - x0, y1 - y0
    font_path = (str(ROOT / "fonts" / "comic-neue-latin-700-normal.woff") if op.get("font") == "comic"
                 else str(FONT).format(w=op.get("weight", 500)))
    text, align = op["text"], op.get("align", "center")
    # largest size that fits the box (measured before rotation)
    size = op.get("size") or 80
    while size > 6:
        font = ImageFont.truetype(font_path, size)
        l, t, r, b = ImageDraw.Draw(img).multiline_textbbox((0, 0), text, font=font, align=align, spacing=size * 0.12)
        if r - l <= bw and b - t <= bh or op.get("size"):
            break
        size -= 1
    layer = Image.new("RGBA", (int(r - l) + 8, int(b - t) + 8), (0, 0, 0, 0))
    ImageDraw.Draw(layer).multiline_text((4 - l, 4 - t), text, font=font, fill=INK + (255,), align=align, spacing=size * 0.12)
    if op.get("rot"):
        layer = layer.rotate(op["rot"], expand=True, resample=Image.BICUBIC)
    lw, lh = layer.size
    if align == "left":
        px = x0
    elif align == "right":
        px = x1 - lw
    else:
        px = x0 + (bw - lw) // 2
    img.paste(layer, (px, y0 + (bh - lh) // 2), layer)


def grid(img, out):
    g = img.copy()
    d = ImageDraw.Draw(g)
    for x in range(0, g.width, 25):
        d.line([(x, 0), (x, g.height)], fill=(255, 0, 0) if x % 100 == 0 else (255, 200, 200))
        if x % 100 == 0:
            d.text((x + 2, 2), str(x), fill=(255, 0, 0))
    for y in range(0, g.height, 25):
        d.line([(0, y), (g.width, y)], fill=(0, 0, 255) if y % 100 == 0 else (200, 200, 255))
        if y % 100 == 0:
            d.text((2, y + 2), str(y), fill=(0, 0, 255))
    g.save(out)


def main():
    cfg_path = Path(sys.argv[1])
    only = [a for a in sys.argv[2:] if not a.startswith("--")]
    cfg = json.loads(cfg_path.read_text(encoding="utf-8"))
    base = cfg_path.parent
    (base / "fixed").mkdir(exist_ok=True)
    for name, ops in cfg.items():
        if only and not any(name.startswith(o) for o in only):
            continue
        img = Image.open(base / "originals" / f"{name}.jpg").convert("RGB")
        for op in ops:
            if "text" in op:
                write(img, op)
            elif "erase" in op:
                erase(img, op["erase"])
            elif "line" in op:  # [x0, y0, x1, y1, width]
                x0, y0, x1, y1, w = op["line"]
                ImageDraw.Draw(img).line([(x0, y0), (x1, y1)], fill=INK, width=w)
        out = base / "fixed" / f"{name}.png"
        img.save(out)
        if "--grid" in sys.argv:
            grid(img, base / "fixed" / f"{name}_grid.png")
        print("fixed", out.name, f"({len(ops)} changes)")


if __name__ == "__main__":
    main()
