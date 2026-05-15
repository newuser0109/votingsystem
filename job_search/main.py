#!/usr/bin/env python3
"""
Job Search Automation — simplified digest mode.

Usage:
  python -m job_search          # Search all boards → email digest

The system searches Indeed and Dice for all manager/director title variants,
deduplicates across boards, and emails you a single HTML list with:
  Job Title | Company | Location | Posted Date | Job Board | Link
"""

import argparse
import sys

from .config import CANDIDATE_EMAIL, RAPIDAPI_KEY
from .search import Job, JobSearchManager


def _check_env():
    missing = []
    if not RAPIDAPI_KEY:
        missing.append("RAPIDAPI_KEY  — free key at https://rapidapi.com/letscrape-6bRBa3QguO5/api/indeed12")
    if not CANDIDATE_EMAIL or CANDIDATE_EMAIL == "you@gmail.com":
        missing.append("CANDIDATE_EMAIL — your Gmail address")
    if missing:
        print("\nMissing required .env settings:\n")
        for m in missing:
            print(f"  • {m}")
        print("\nCopy .env.example → .env and fill in the values.")
        sys.exit(1)


def _is_atlanta(job: Job) -> bool:
    loc = job.location.lower()
    ga_cities = {"atlanta", "alpharetta", "marietta", "norcross", "duluth",
                 "roswell", "johns creek", "sandy springs", "decatur",
                 "smyrna", "kennesaw", "gwinnett", "peachtree", "stone mountain",
                 "lawrenceville", "suwanee", "cumming", "canton", "cartersville",
                 "stockbridge", "mcdonough", "newnan", "fayetteville", "griffin",
                 ", ga", ", georgia"}
    return any(c in loc for c in ga_cities)


def _is_remote(job: Job) -> bool:
    return "remote" in job.location.lower() or job.location.strip() == ""


def run(args):
    _check_env()

    print("\n[1/3] Searching job boards...")
    manager = JobSearchManager()
    all_jobs = manager.search_all()

    # Split into remote vs Atlanta-area buckets
    remote_jobs = [j for j in all_jobs if _is_remote(j)]
    local_jobs  = [j for j in all_jobs if _is_atlanta(j) and not _is_remote(j)]
    other_jobs  = [j for j in all_jobs if not _is_remote(j) and not _is_atlanta(j)]

    total = len(remote_jobs) + len(local_jobs)
    print(f"[1/3] {len(all_jobs)} jobs found, {total} match Remote/Atlanta ({len(other_jobs)} other locations filtered out)")

    print(f"\n[2/3] Building email digest ({len(remote_jobs)} remote + {len(local_jobs)} Atlanta)...")

    if args.dry_run:
        print("[2/3] DRY RUN — skipping email. Top results:")
        for j in (remote_jobs + local_jobs)[:10]:
            print(f"       {j.title} @ {j.company} | {j.location} | {j.posted_date} | {j.board}")
        return

    from .emailer import send_digest
    draft_id = send_digest(remote_jobs, local_jobs)

    print(f"[3/3] Email draft created in Gmail (id: {draft_id})")
    print(f"\n  → Open Gmail Drafts, review, then send to {CANDIDATE_EMAIL}")
    print(f"  → {len(remote_jobs)} remote jobs  |  {len(local_jobs)} Atlanta area jobs")


def main():
    parser = argparse.ArgumentParser(
        prog="job_search",
        description="Search all job boards and email a deduplicated list of matching roles.",
    )
    parser.add_argument(
        "--dry-run", action="store_true",
        help="Print results without creating the email draft",
    )
    args = parser.parse_args()
    run(args)


if __name__ == "__main__":
    main()
