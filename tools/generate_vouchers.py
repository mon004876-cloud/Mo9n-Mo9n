#!/usr/bin/env python3
"""توليد أكواد تفعيل لتطبيق موسوعة السادس.

يُضيف بصمات الأكواد (SHA-256) إلى app/src/main/assets/vouchers/voucher_registry.txt
ويكتب الأكواد نفسها في ملف CSV داخل tools/generated_vouchers/ (هذا المجلد مُستثنى من git).

⚠️ لا ترفع ملف الـ CSV إلى المستودع أبداً، فهو يحتوي على الأكواد الصالحة.
بعد توليد دفعة جديدة يجب إصدار نسخة جديدة من التطبيق حتى تتعرف على الأكواد.

أمثلة:
  # 500 كود للباقة الشاملة صالحة حتى 2027-09-30
  python3 tools/generate_vouchers.py --plan ALL_7_SUBJECTS_VIP_ANNUAL --count 500 --expires 2027-09-30

  # 200 كود لكتاب الفيزياء فقط
  python3 tools/generate_vouchers.py --plan SINGLE_SUBJECT_ANNUAL --subject physics --count 200 --expires 2027-09-30
"""
import argparse
import csv
import datetime
import hashlib
import pathlib
import secrets

ROOT = pathlib.Path(__file__).resolve().parent.parent
REGISTRY = ROOT / "app/src/main/assets/vouchers/voucher_registry.txt"
OUT_DIR = ROOT / "tools/generated_vouchers"

# يجب أن تطابق VoucherVerifier.kt
HASH_PREFIX = "sadis-voucher-v1:"
ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"  # Crockford Base32 (بدون I L O U)
CODE_LENGTH = 16  # 80 بت عشوائية

PLANS = {"SINGLE_SUBJECT_ANNUAL", "ALL_7_SUBJECTS_VIP_ANNUAL"}
SUBJECTS = {"islamic", "arabic", "english", "math", "biology", "physics", "chemistry"}


def new_code() -> str:
    return "".join(secrets.choice(ALPHABET) for _ in range(CODE_LENGTH))


def pretty(code: str) -> str:
    return "-".join(code[i:i + 4] for i in range(0, CODE_LENGTH, 4))


def hash_code(code: str) -> str:
    return hashlib.sha256((HASH_PREFIX + code).encode("utf-8")).hexdigest()


def main() -> None:
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("--plan", required=True, choices=sorted(PLANS))
    p.add_argument("--subject", choices=sorted(SUBJECTS), help="مطلوب مع SINGLE_SUBJECT_ANNUAL")
    p.add_argument("--count", type=int, required=True)
    p.add_argument("--expires", required=True, help="YYYY-MM-DD (آخر يوم صلاحية)")
    a = p.parse_args()

    if a.plan == "SINGLE_SUBJECT_ANNUAL" and not a.subject:
        p.error("--subject مطلوب لاشتراك الكتاب الواحد")
    if a.plan == "ALL_7_SUBJECTS_VIP_ANNUAL" and a.subject:
        p.error("--subject لا يُستخدم مع الباقة الشاملة")
    if not 1 <= a.count <= 100_000:
        p.error("--count يجب أن يكون بين 1 و 100000")
    datetime.date.fromisoformat(a.expires)

    subject = a.subject or "*"
    codes = {new_code() for _ in range(a.count)}
    while len(codes) < a.count:
        codes.add(new_code())

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    stamp = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
    out_csv = OUT_DIR / f"{a.plan}_{subject}_{stamp}.csv"
    with out_csv.open("w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(["code", "plan", "subject", "expires"])
        for c in sorted(codes):
            w.writerow([pretty(c), a.plan, subject, a.expires])

    with REGISTRY.open("a", encoding="utf-8") as f:
        f.write(f"# batch {stamp}: {a.count} x {a.plan}/{subject} until {a.expires}\n")
        for c in sorted(codes, key=hash_code):
            f.write(f"{a.plan}|{subject}|{a.expires}|{hash_code(c)}\n")

    print(f"✓ {a.count} كود → {out_csv.relative_to(ROOT)}")
    print(f"✓ تمت إضافة البصمات إلى {REGISTRY.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
