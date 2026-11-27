"""Build a workbook page (activity page + answer key) in English and Arabic.

    python build_page.py pages/01_animal_cell.json
    node render.js            # turns build/*.html into samples/*.pdf and *.png

The drawing has no text. Numbers, leader lines and all wording are added here,
so the same art serves the US (English, Letter) and Iraqi (Arabic, A4) editions.
"""

import html
import json
import re
import sys
from pathlib import Path

HERE = Path(__file__).parent
BUILD = HERE / "build"

UI = {
    "en": {
        "dir": "ltr", "page": "Letter", "book": "Biology Coloring Workbook",
        "name": "Name", "class": "Class", "date": "Date",
        "instr": "Color each structure in the diagram, then color the matching box in the key.",
        "key": "Color Key", "structure": "Structure", "suggested": "Suggested color", "yours": "Your color",
        "check": "Check Your Understanding", "answers_at": "Answers are at the back of the book.",
        "answer_key": "Answer Key", "answers": "Answers",
    },
    "ar": {
        "dir": "rtl", "page": "A4", "book": "دفتر تلوين الأحياء",
        "name": "الاسم", "class": "الصف", "date": "التاريخ",
        "instr": "لوّن كل تركيب في الرسمة، ثم لوّن المربع المقابل له في مفتاح الألوان.",
        "key": "مفتاح الألوان", "structure": "التركيب", "suggested": "اللون المقترح", "yours": "لونك",
        "check": "اختبر فهمك", "answers_at": "الأجوبة في نهاية الدفتر.",
        "answer_key": "مفتاح الإجابات", "answers": "الأجوبة",
    },
}

AR_DIGITS = str.maketrans("0123456789", "٠١٢٣٤٥٦٧٨٩")


def num(n, lang):
    return str(n).translate(AR_DIGITS) if lang == "ar" else str(n)


def figure(page, lang, colored=False):
    """Drawing + numbered callouts. Art can be our own SVG or a raster image from Gemini
    (PNG/JPG, with "art_size": [width, height] in the page file)."""
    if page["art"].lower().endswith(".svg"):
        art = (HERE / page["art"]).read_text(encoding="utf-8")
        _, _, w, h = re.search(r'viewBox="([^"]+)"', art).group(1).split()
        art = art.replace("<svg ", f'<svg x="0" y="0" width="{w}" height="{h}" ', 1)
    else:
        w, h = page["art_size"]
        art = f'<image href="../{page["art"]}" x="0" y="0" width="{w}" height="{h}"/>'
        colored = False  # a raster drawing cannot be auto-colored

    k = page.get("callout_scale", 1)  # bigger badges for big raster images
    callouts = []
    for p in page["parts"]:
        if not p.get("at"):
            continue  # positions not set yet
        (x0, y0), (x1, y1) = p["at"], p["label"]
        dx, dy = x0 - x1, y0 - y1
        d = (dx * dx + dy * dy) ** 0.5 or 1
        ex, ey = x1 + dx / d * 15 * k, y1 + dy / d * 15 * k  # stop the line at the circle edge
        callouts.append(
            f'<g style="--k:{k}"><line x1="{x0}" y1="{y0}" x2="{ex:.1f}" y2="{ey:.1f}" class="lead" stroke-width="{1.3 * k}"/>'
            f'<circle cx="{x0}" cy="{y0}" r="{3 * k}" class="dot"/>'
            f'<circle cx="{x1}" cy="{y1}" r="{15 * k}" class="badge" stroke-width="{1.8 * k}"/>'
            f'<text x="{x1}" y="{y1}" class="badge-n" font-size="{15 * k}">{num(p["n"], lang)}</text></g>'
        )

    fills = ""
    if colored:
        for p in page["parts"]:
            c, n = p["color"], p["n"]
            fills += (f".colored .p{n}{{fill:{c}!important}}"
                      f".colored .p{n}.tube{{fill:none!important;stroke:{c}!important}}")
    vb = page.get("view_box") or f"0 0 {w} {h}"
    return (f'<svg class="fig{" colored" if colored else ""}" viewBox="{vb}" '
            f'xmlns="http://www.w3.org/2000/svg"><style>{fills}</style>{art}{"".join(callouts)}</svg>')


CSS = """
@font-face{font-family:Inter;src:url(../fonts/inter-latin-400-normal.woff2);font-weight:400}
@font-face{font-family:Inter;src:url(../fonts/inter-latin-700-normal.woff2);font-weight:700}
@font-face{font-family:NotoAr;src:url(../fonts/noto-sans-arabic-arabic-400-normal.woff2);font-weight:400}
@font-face{font-family:NotoAr;src:url(../fonts/noto-sans-arabic-arabic-700-normal.woff2);font-weight:700}
@page{size:PAGESIZE;margin:0}
*{box-sizing:border-box}
body{margin:0;font-family:Inter,NotoAr,sans-serif;color:#1d1d1f;font-size:10.5pt;line-height:1.4}
html[lang=ar] body{font-family:NotoAr,Inter,sans-serif;font-size:10.5pt;line-height:1.55}
.page{width:100%;height:100vh;padding:0.5in 0.55in 0.4in;display:flex;flex-direction:column;page-break-after:always;overflow:hidden}
.top{display:flex;justify-content:space-between;align-items:center;font-size:8.5pt;color:#666;
     border-bottom:1.5px solid #1d1d1f;padding-bottom:5px;letter-spacing:.02em}
.top b{color:#1d1d1f}
.pgnum{display:inline-grid;place-items:center;width:22px;height:22px;border-radius:50%;background:#1d1d1f;color:#fff;font-weight:700;font-size:9pt}
.meta{display:flex;gap:18px;margin:12px 0 4px;font-size:9.5pt}
.meta span{flex:1;display:flex;gap:6px;white-space:nowrap}
.meta span i{flex:1;border-bottom:1px solid #999;font-style:normal}
.meta span.short{flex:.6}
h1{font-size:21pt;margin:10px 0 2px;line-height:1.15}
h1 small{font-size:11pt;font-weight:400;color:#666;margin-inline-start:8px}
.intro{margin:2px 0 4px;color:#333;font-size:9.8pt}
.fig{width:100%;flex:1;min-height:0;display:block}
.fig .lead{stroke:#1d1d1f}
.fig .dot{fill:#1d1d1f}
.fig .badge{fill:#fff;stroke:#1d1d1f}
.fig .badge-n{font-family:Inter,NotoAr,sans-serif;font-weight:700;text-anchor:middle;dominant-baseline:central;fill:#1d1d1f}
.instr{font-size:9.3pt;color:#444;margin:4px 0 8px;padding:6px 10px;background:#f3f3f3;border-radius:6px}
.cols{display:grid;grid-template-columns:1.25fr 1fr;gap:20px}
h2{font-size:11.5pt;margin:0 0 6px;text-transform:uppercase;letter-spacing:.05em}
html[lang=ar] h2{text-transform:none;letter-spacing:0;font-size:12pt}
table{width:100%;border-collapse:collapse;font-size:9.3pt}
th{text-align:start;font-size:8pt;color:#666;font-weight:400;border-bottom:1px solid #1d1d1f;padding:2px 4px}
td{padding:3px 4px;border-bottom:1px solid #ddd;vertical-align:middle}
td.n{width:24px;font-weight:700;text-align:center}
td.c{color:#555;white-space:nowrap}
td.box{width:46px}
td.box div{width:38px;height:15px;border:1.3px solid #1d1d1f;border-radius:3px}
ol.q{margin:0;padding-inline-start:18px}
ol.q li{margin-bottom:10px}
ol.q .ln{border-bottom:1px solid #999;height:20px}
.foot{margin-top:auto;padding-top:6px;font-size:8pt;color:#888;text-align:end}
ol.a li{margin-bottom:6px}
.ak .fig{flex:1}
html[lang=ar] ol{list-style-type:arabic-indic}
"""


def activity_page(page, lang, ui):
    rows = "".join(
        f'<tr><td class="n">{num(p["n"], lang)}</td><td>{html.escape(p[lang])}</td>'
        f'<td class="c">{html.escape(p["color_" + lang])}</td><td class="box"><div></div></td></tr>'
        for p in page["parts"])
    qs = "".join(f'<li>{html.escape(q[lang])}<div class="ln"></div><div class="ln"></div></li>'
                 for q in page["questions"])
    return f"""
<section class="page">
  <div class="top"><span><b>{ui['book']}</b> · {page['unit'][lang]}</span><span class="pgnum">{num(page['id'], lang)}</span></div>
  <div class="meta"><span>{ui['name']}:<i></i></span><span class="short">{ui['class']}:<i></i></span><span class="short">{ui['date']}:<i></i></span></div>
  <h1>{page['title'][lang]}{'<small>' + page['title']['en'] + '</small>' if lang == 'ar' else ''}</h1>
  <p class="intro">{html.escape(page['intro'][lang])}</p>
  {figure(page, lang)}
  <div class="instr">✎ {ui['instr']}</div>
  <div class="cols">
    <div><h2>{ui['key']}</h2>
      <table><tr><th>#</th><th>{ui['structure']}</th><th>{ui['suggested']}</th><th>{ui['yours']}</th></tr>{rows}</table></div>
    <div><h2>{ui['check']}</h2><ol class="q">{qs}</ol></div>
  </div>
  <div class="foot">{ui['answers_at']}</div>
</section>"""


def answer_page(page, lang, ui):
    rows = "".join(
        f'<tr><td class="n">{num(p["n"], lang)}</td><td>{html.escape(p[lang])}</td>'
        f'<td class="box"><div style="background:{p["color"]}"></div></td></tr>'
        for p in page["parts"])
    ans = "".join(f'<li><span style="color:#666">{html.escape(q[lang])}</span><br><b>{html.escape(q["answer_" + lang])}</b></li>'
                  for q in page["questions"])
    return f"""
<section class="page ak">
  <div class="top"><span><b>{ui['book']}</b> · {ui['answer_key']}</span><span class="pgnum">{num(page['id'], lang)}</span></div>
  <h1>{ui['answer_key']}: {page['title'][lang]}</h1>
  {figure(page, lang, colored=True)}
  <div class="cols">
    <div><h2>{ui['key']}</h2><table>{rows}</table></div>
    <div><h2>{ui['answers']}</h2><ol class="a">{ans}</ol></div>
  </div>
</section>"""


def build(page_file):
    page = json.loads(Path(page_file).read_text(encoding="utf-8"))
    BUILD.mkdir(exist_ok=True)
    stem = Path(page["art"]).stem
    for lang, ui in UI.items():
        doc = (f'<!DOCTYPE html><html lang="{lang}" dir="{ui["dir"]}"><head><meta charset="utf-8">'
               f'<title>{page["title"][lang]}</title><style>{CSS.replace("PAGESIZE", ui["page"])}</style></head><body>'
               f'{activity_page(page, lang, ui)}{answer_page(page, lang, ui)}</body></html>')
        out = BUILD / f"{stem}_{lang}.html"
        out.write_text(doc, encoding="utf-8")
        print("wrote", out)


if __name__ == "__main__":
    build(sys.argv[1] if len(sys.argv) > 1 else HERE / "pages/01_animal_cell.json")
