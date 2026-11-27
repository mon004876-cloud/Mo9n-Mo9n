"""Draws art/01_animal_cell.svg (line art only, no text). Each part has class p<N>
matching the part number in pages/01_animal_cell.json so the answer key can color it."""
import math
from pathlib import Path

W, H = 640, 560
out = []
add = out.append

def sausage(cx, cy, r_in, r_out, a0, a1, cls):
    """Closed curved band (ER cisterna / Golgi sac) around (cx,cy) between angles a0..a1 (deg)."""
    pts_o, pts_i = [], []
    n = 24
    for k in range(n + 1):
        a = math.radians(a0 + (a1 - a0) * k / n)
        pts_o.append((cx + r_out * math.cos(a), cy + r_out * math.sin(a)))
        pts_i.append((cx + r_in * math.cos(a), cy + r_in * math.sin(a)))
    rc = (r_out - r_in) / 2
    d = f"M{pts_o[0][0]:.1f},{pts_o[0][1]:.1f} "
    d += " ".join(f"L{x:.1f},{y:.1f}" for x, y in pts_o[1:])
    d += f" A{rc:.1f},{rc:.1f} 0 0 1 {pts_i[-1][0]:.1f},{pts_i[-1][1]:.1f} "
    d += " ".join(f"L{x:.1f},{y:.1f}" for x, y in reversed(pts_i[:-1]))
    d += f" A{rc:.1f},{rc:.1f} 0 0 1 {pts_o[0][0]:.1f},{pts_o[0][1]:.1f} Z"
    add(f'<path class="{cls}" d="{d}"/>')
    return pts_o

def mito(cx, cy, rot, s=1.0):
    add(f'<g transform="translate({cx},{cy}) rotate({rot}) scale({s})">')
    add('<ellipse class="p5" cx="0" cy="0" rx="44" ry="20"/>')
    add('<ellipse class="p5 inner" cx="0" cy="0" rx="38" ry="14"/>')
    zz = "M-34,0 " + " ".join(
        f"L{x},{-11 if i % 2 == 0 else 11}" for i, x in enumerate(range(-28, 33, 8))) + " L34,0"
    add(f'<path class="line" d="{zz}"/>')
    add('</g>')

add(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {W} {H}" class="art">')
add('<style>.art path,.art ellipse,.art circle,.art rect{fill:#fff;stroke:#111;stroke-width:2.6;stroke-linejoin:round}'
    '.art .line{fill:none;stroke-width:2}.art .rib{fill:#111;stroke:none}</style>')

# 3 + 4: cell membrane (band) and cytoplasm
blob = ("M320,78 C430,72 530,128 545,230 C560,330 515,430 420,472 "
        "C335,508 220,498 150,446 C85,396 75,290 102,206 C128,128 215,84 320,78 Z")
add(f'<path class="p3" d="{blob}"/>')
add(f'<path class="p4" d="{blob}" transform="translate(323,288) scale(0.955) translate(-323,-288)"/>')

# 6: rough ER — curved cisternae around the nucleus, ribosomes on their surface
ncx, ncy = 300, 270
for r_in, r_out in [(92, 104), (114, 126), (136, 148)]:
    pts = sausage(ncx, ncy, r_in, r_out, -48, 48, "p6")
    for x, y in pts[2:-2:3]:
        add(f'<circle class="rib p9" cx="{x:.1f}" cy="{y:.1f}" r="3.2"/>')

# 1 + 2: nucleus (double envelope with pores) and nucleolus
add(f'<ellipse class="p1" cx="{ncx}" cy="{ncy}" rx="78" ry="68"/>')
add(f'<ellipse class="p1 inner" cx="{ncx}" cy="{ncy}" rx="70" ry="60"/>')
for a in range(0, 360, 45):
    t = math.radians(a + 20)
    x1, y1 = ncx + 70 * math.cos(t), ncy + 60 * math.sin(t)
    x2, y2 = ncx + 78 * math.cos(t), ncy + 68 * math.sin(t)
    add(f'<path class="line" d="M{x1:.1f},{y1:.1f} L{x2:.1f},{y2:.1f}" style="stroke:#fff;stroke-width:5"/>')
add(f'<ellipse class="p2" cx="{ncx + 14}" cy="{ncy - 8}" rx="24" ry="19"/>')
add(f'<path class="line" d="M262,300 q14,-10 26,4 t28,-2 M250,250 q10,8 20,-2" style="stroke-dasharray:2 6;stroke-linecap:round;stroke-width:3"/>')

# 8: Golgi apparatus — stacked curved sacs + vesicles
for r_in, r_out in [(58, 68), (74, 84), (90, 100), (106, 116)]:
    sausage(168, 478, r_in, r_out, -82, -26, "p8")
for vx, vy, vr in [(280, 418, 7), (284, 438, 5.5), (170, 345, 6), (190, 340, 5)]:
    add(f'<circle class="p8" cx="{vx}" cy="{vy}" r="{vr}"/>')

# 7: smooth ER — branching tubes (outline drawn as black stroke + white core)
ser = "M398,372 C420,350 445,392 470,368 C488,350 500,372 488,392 M430,382 C440,410 470,404 482,420 M404,392 C398,412 420,428 440,424"
add(f'<path d="{ser}" style="fill:none;stroke:#111;stroke-width:17;stroke-linecap:round"/>')
add(f'<path class="p7 tube" d="{ser}" style="fill:none;stroke:#fff;stroke-width:11.5;stroke-linecap:round"/>')

# 5: mitochondria
mito(228, 150, 22)
mito(495, 300, 78, 0.9)
mito(338, 440, 6, 0.95)

# 10: lysosomes
for lx, ly in [(402, 128), (128, 330)]:
    add(f'<circle class="p10" cx="{lx}" cy="{ly}" r="14"/>')
    for dx, dy in [(-5, -3), (4, -5), (0, 5), (6, 3)]:
        add(f'<circle class="rib" cx="{lx + dx}" cy="{ly + dy}" r="1.8"/>')

# 11: centrioles — two perpendicular cylinders
add('<g transform="translate(176,262)">')
add('<rect class="p11" x="-20" y="-7" width="40" height="14" rx="3"/>')
for x in range(-12, 13, 8):
    add(f'<path class="line" d="M{x},-7 L{x},7"/>')
add('<rect class="p11" x="14" y="2" width="14" height="40" rx="3"/>')
for y in range(10, 37, 8):
    add(f'<path class="line" d="M14,{y} L28,{y}"/>')
add('</g>')

# 9: free ribosomes scattered in the cytoplasm
for x, y in [(330, 130), (345, 145), (318, 150), (282, 118), (420, 175), (172, 300), (210, 330),
             (256, 380), (300, 375), (390, 450), (410, 440), (150, 230), (470, 200), (462, 325)]:
    add(f'<circle class="rib p9" cx="{x}" cy="{y}" r="3.4"/>')

add('</svg>')
Path(__file__).resolve().parent.parent.joinpath("art/01_animal_cell.svg").write_text("\n".join(out), encoding="utf-8")
print("ok")
