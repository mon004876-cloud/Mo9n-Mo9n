"""Turn a Gemini line-art image (JPG/PNG) into a crisp SVG for print.

    python tools/vectorize.py art/src/06_enzymes.jpg art/06_enzymes.svg

The SVG keeps the original image's pixel coordinates, so callout positions
measured on the original image still line up. Needs: potrace, Pillow.
"""
import re
import subprocess
import sys
import tempfile
from pathlib import Path

from PIL import Image, ImageFilter

UPSCALE = 4          # trace at 4x for smooth curves
THRESHOLD = 150      # 0-255: darker than this becomes black line


def vectorize(src, dst):
    img = Image.open(src).convert("L")
    w, h = img.size
    big = img.resize((w * UPSCALE, h * UPSCALE), Image.LANCZOS).filter(ImageFilter.GaussianBlur(1.2))
    bw = big.point(lambda v: 0 if v < THRESHOLD else 255, "1")
    with tempfile.TemporaryDirectory() as tmp:
        pbm = Path(tmp) / "in.pbm"
        bw.save(pbm)
        # -r 72*UPSCALE makes 1 SVG unit = 1 original pixel; -t drops specks; -O smooths curves
        subprocess.run(["potrace", str(pbm), "-s", "-r", str(72 * UPSCALE), "-t", "6",
                        "-a", "1.1", "-O", "0.4", "-o", str(Path(tmp) / "out.svg")], check=True)
        svg = (Path(tmp) / "out.svg").read_text()
    svg = svg[svg.index("<svg"):]                              # drop XML prolog
    svg = re.sub(r"<metadata>.*?</metadata>", "", svg, flags=re.S)
    svg = re.sub(r'width="[^"]+" height="[^"]+"', "", svg, count=1)  # size comes from the page
    Path(dst).write_text(svg)
    print(f"{dst}: viewBox 0 0 {w} {h}")


if __name__ == "__main__":
    vectorize(sys.argv[1], sys.argv[2])
