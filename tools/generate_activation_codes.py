#!/usr/bin/env python3
"""Generate activation codes for the app (Python 3.8+, standard library only).

Each run:
  * writes the printable codes to a private CSV file (sell or print these; never commit it),
  * prints ActivationCodeRecord(...) lines to paste into
    app/src/main/java/com/example/data/ActivationCodeRegistry.kt.

The app only stores a salted PBKDF2 hash of every code, so the codes cannot be read out of the APK.
Keep this file in sync with ActivationCodes.kt (alphabet, lengths, iterations).

Examples:
  python3 tools/generate_activation_codes.py --plan all --count 50
  python3 tools/generate_activation_codes.py --plan subject --subject physics --count 50
"""
import argparse
import csv
import datetime
import hashlib
import pathlib
import re
import secrets
import sys

ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"  # Crockford base32: no I, L, O, U
ID_LENGTH = 4
SECRET_LENGTH = 12
PBKDF2_ITERATIONS = 10_000
HASH_BYTES = 32
SALT_BYTES = 16

PLAN_IDS = {"all": "ALL_7_SUBJECTS_VIP_ANNUAL", "subject": "SINGLE_SUBJECT_ANNUAL"}
SUBJECT_IDS = ["islamic", "arabic", "english", "math", "biology", "physics", "chemistry"]
REGISTRY = pathlib.Path(__file__).resolve().parent.parent / (
    "app/src/main/java/com/example/data/ActivationCodeRegistry.kt"
)


def random_chars(length):
    return "".join(secrets.choice(ALPHABET) for _ in range(length))


def hash_secret(secret, salt):
    return hashlib.pbkdf2_hmac("sha1", secret.encode("ascii"), salt, PBKDF2_ITERATIONS, HASH_BYTES)


def existing_ids():
    if not REGISTRY.exists():
        return set()
    return set(re.findall(r'ActivationCodeRecord\(\s*id\s*=\s*"([0-9A-Z]{4})"', REGISTRY.read_text("utf-8")))


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--plan", choices=sorted(PLAN_IDS), required=True,
                        help="'all' unlocks all 7 books, 'subject' unlocks one book")
    parser.add_argument("--subject", choices=SUBJECT_IDS, help="required with --plan subject")
    parser.add_argument("--count", type=int, default=10)
    parser.add_argument("--out", help="CSV file for the printable codes (default: activation-codes-<time>.csv)")
    args = parser.parse_args()

    if args.plan == "subject" and not args.subject:
        parser.error("--plan subject needs --subject")
    if args.plan == "all" and args.subject:
        parser.error("--subject only goes with --plan subject")
    if not 1 <= args.count <= 100_000:
        parser.error("--count must be between 1 and 100000")

    used = existing_ids()
    plan_id = PLAN_IDS[args.plan]
    subject_kotlin = f'"{args.subject}"' if args.subject else "null"
    out = pathlib.Path(args.out or f"activation-codes-{datetime.datetime.now():%Y%m%d-%H%M%S}.csv")

    rows, kotlin = [], []
    for _ in range(args.count):
        code_id = random_chars(ID_LENGTH)
        while code_id in used:
            code_id = random_chars(ID_LENGTH)
        used.add(code_id)
        secret = random_chars(SECRET_LENGTH)
        salt = secrets.token_bytes(SALT_BYTES)
        printable = "-".join((code_id, secret[0:4], secret[4:8], secret[8:12]))
        rows.append((printable, plan_id, args.subject or "all"))
        kotlin.append(
            f'        ActivationCodeRecord(id = "{code_id}", saltHex = "{salt.hex()}", '
            f'hashHex = "{hash_secret(secret, salt).hex()}", planId = "{plan_id}", subjectId = {subject_kotlin}),'
        )

    with out.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle)
        writer.writerow(["code", "plan", "subject"])
        writer.writerows(rows)

    print(f"# {len(rows)} codes written to {out} (private: do not commit or share this file).", file=sys.stderr)
    print(f"# Paste these lines inside ActivationCodeRegistry.records:", file=sys.stderr)
    print("\n".join(kotlin))


if __name__ == "__main__":
    main()
