"""Generate the 30 Biology Coloring Workbook illustrations with Gemini.

Usage:
    python generate.py --prompts-only     # write PROMPTS.md (copy/paste into the Gemini app)
    python generate.py                    # generate all 30 images via the Gemini API
    python generate.py --only 1 16 23     # generate specific pages only
    python generate.py --force            # regenerate pages that already exist

Requires (API mode only):
    pip install google-genai
    export GEMINI_API_KEY="..."            # from https://aistudio.google.com/apikey
"""

import argparse
import json
import os
import sys
import time
from pathlib import Path

HERE = Path(__file__).parent
TOPICS = HERE / "topics.json"
OUT_DIR = HERE / "output"

# Change this if Google renames / releases a newer image model.
MODEL = os.environ.get("GEMINI_IMAGE_MODEL", "gemini-2.5-flash-image")
ASPECT_RATIO = "3:4"  # portrait page

PROMPT_TEMPLATE = """\
Create a black-and-white coloring book page for a high school biology workbook (grades 9-12).

Subject: {subject}.

The illustration must clearly show these parts, each as its own separate closed shape that a student can color:
{parts}

Style rules:
- Scientifically accurate, like a clean textbook diagram redrawn as line art.
- Bold, smooth, uniform black outlines on a pure white background.
- No shading, no gray tones, no hatching, no color fills, no gradients.
- Large, closed regions that are easy to color with colored pencils.
- Centered on the page, portrait orientation, generous white margins on all sides.
- Leave empty white space around the drawing so numbered labels can be added later.

Very important: NO text of any kind. No words, no letters, no numbers, no labels,
no arrows with text, no title, no watermark, no signature.
"""


def load_topics():
    with TOPICS.open(encoding="utf-8") as f:
        return json.load(f)


def build_prompt(topic):
    parts = "\n".join(f"- {en}" for en, _ar in topic["parts"])
    return PROMPT_TEMPLATE.format(subject=topic["subject"], parts=parts)


def write_prompts_md(topics):
    lines = [
        "# برومبتات Gemini — Biology Coloring Workbook",
        "",
        "انسخ كل برومبت والصقه بتطبيق Gemini (أو AI Studio) لتوليد الصورة.",
        "الأسماء العربية والإنجليزية تحت كل برومبت هي مفتاح الترقيم اللي نضيفه بعدين فوق الرسمة.",
        "",
    ]
    for t in topics:
        lines += [
            f"## {t['id']:02d}. {t['title_en']} — {t['title_ar']}",
            "",
            "```text",
            build_prompt(t).rstrip(),
            "```",
            "",
            "| # | English | العربي |",
            "|---|---|---|",
        ]
        lines += [f"| {i} | {en} | {ar} |" for i, (en, ar) in enumerate(t["parts"], 1)]
        lines.append("")
    path = HERE / "PROMPTS.md"
    path.write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote {path}")


def make_config(types):
    try:
        return types.GenerateContentConfig(
            response_modalities=["IMAGE"],
            image_config=types.ImageConfig(aspect_ratio=ASPECT_RATIO),
        )
    except (AttributeError, TypeError, ValueError):
        # Older SDK without ImageConfig: aspect ratio is requested in the prompt instead.
        return types.GenerateContentConfig(response_modalities=["IMAGE"])


def generate_images(topics, force=False, retries=3):
    try:
        from google import genai
        from google.genai import types
    except ImportError:
        sys.exit("Missing SDK. Run: pip install google-genai")

    if not os.environ.get("GEMINI_API_KEY"):
        sys.exit("Set GEMINI_API_KEY first (get one at https://aistudio.google.com/apikey)")

    client = genai.Client()
    config = make_config(types)
    OUT_DIR.mkdir(exist_ok=True)

    failed = []
    for t in topics:
        out = OUT_DIR / f"{t['id']:02d}_{t['slug']}.png"
        if out.exists() and not force:
            print(f"[skip] {out.name} already exists")
            continue

        print(f"[{t['id']:02d}/30] {t['title_en']} ...", flush=True)
        for attempt in range(1, retries + 1):
            try:
                resp = client.models.generate_content(
                    model=MODEL, contents=[build_prompt(t)], config=config
                )
                image_bytes = next(
                    (p.inline_data.data
                     for c in (resp.candidates or [])
                     for p in (c.content.parts if c.content else [])
                     if getattr(p, "inline_data", None) and p.inline_data.data),
                    None,
                )
                if not image_bytes:
                    raise RuntimeError("no image in response (may have been blocked)")
                out.write_bytes(image_bytes)
                print(f"   saved {out.name}")
                break
            except Exception as e:  # noqa: BLE001 - report and retry any API error
                print(f"   attempt {attempt} failed: {e}")
                if attempt == retries:
                    failed.append(t["id"])
                else:
                    time.sleep(10 * attempt)
        time.sleep(2)  # be gentle with rate limits

    if failed:
        print(f"\nFailed pages: {failed}  -> rerun with: python generate.py --only {' '.join(map(str, failed))}")
    else:
        print("\nDone. Images are in", OUT_DIR)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--prompts-only", action="store_true", help="only write PROMPTS.md")
    ap.add_argument("--only", type=int, nargs="+", metavar="ID", help="page numbers to generate")
    ap.add_argument("--force", action="store_true", help="overwrite existing images")
    args = ap.parse_args()

    topics = load_topics()
    if args.only:
        topics = [t for t in topics if t["id"] in set(args.only)]

    if args.prompts_only:
        write_prompts_md(load_topics())
    else:
        generate_images(topics, force=args.force)


if __name__ == "__main__":
    main()
