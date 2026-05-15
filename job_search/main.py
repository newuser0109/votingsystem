#!/usr/bin/env python3
"""
Job Search Automation System
Usage:
  python -m job_search run       # Full pipeline: search → analyze → draft → schedule
  python -m job_search run --dry-run            # Simulate without creating drafts
  python -m job_search run --no-email           # Skip Gmail drafts
  python -m job_search run --no-calendar        # Skip calendar events
  python -m job_search status                   # Show application status dashboard
  python -m job_search followup                 # List jobs needing follow-up
"""

import argparse
import sys

from .config import ANTHROPIC_API_KEY, PROFILE, RAPIDAPI_KEY, MIN_FIT_SCORE
from .models import ApplicationStatus
from .pipeline import JobSearchPipeline
from .tracker import ApplicationTracker


def cmd_run(args):
    _check_env()
    pipeline = JobSearchPipeline()
    result = pipeline.run(
        dry_run=args.dry_run,
        skip_email=args.no_email,
        skip_calendar=args.no_calendar,
    )

    print("\n" + "=" * 60)
    print("  PIPELINE COMPLETE")
    print("=" * 60)
    print(f"  Jobs searched:       {result.searched}")
    print(f"  New (unseen) jobs:   {result.new_jobs}")
    print(f"  Analyzed by AI:      {result.analyzed}")
    print(f"  Passed threshold:    {result.drafted}  (score >= {MIN_FIT_SCORE})")
    print(f"  Gmail drafts:        {result.drafted}")
    print(f"  Calendar reminders:  {result.scheduled}")
    if result.errors:
        print(f"\n  Errors ({len(result.errors)}):")
        for e in result.errors:
            print(f"    ! {e}")

    if result.top_matches:
        print("\n  TOP MATCHES")
        print("  " + "-" * 56)
        for job, analysis in result.top_matches:
            print(f"  [{analysis.fit_score:3d}] {job.title} @ {job.company}")
            print(f"        {job.compensation or 'Salary TBD'} | {job.location}")
            print(f"        {analysis.summary}")
            print()
    print("=" * 60)


def cmd_status(args):
    tracker = ApplicationTracker()
    print(tracker.get_all_applications().__len__(), "applications found")
    pipeline = JobSearchPipeline(tracker=tracker)
    print(pipeline.status_report())


def cmd_followup(args):
    tracker = ApplicationTracker()
    apps = tracker.get_by_status(ApplicationStatus.APPLIED)
    drafted = tracker.get_by_status(ApplicationStatus.DRAFTED)
    pending = apps + drafted

    if not pending:
        print("No applications pending follow-up.")
        return

    print(f"\nApplications to follow up ({len(pending)}):\n")
    for app in pending:
        print(f"  • {app.title} @ {app.company}  [score: {app.fit_score}]")
        print(f"    Status: {app.status.value}  |  Applied: {app.applied_date or 'not recorded'}")
        print(f"    {app.url}\n")


def _check_env():
    missing = []
    if not ANTHROPIC_API_KEY:
        missing.append("ANTHROPIC_API_KEY  — get at https://console.anthropic.com")
    if not RAPIDAPI_KEY:
        missing.append("RAPIDAPI_KEY       — get at https://rapidapi.com/letscrape-6bRBa3QguO5/api/indeed12")
    if not PROFILE["email"] or PROFILE["email"] == "your.email@example.com":
        missing.append("CANDIDATE_EMAIL    — your email address")
    if missing:
        print("\nMissing required configuration in your .env file:\n")
        for m in missing:
            print(f"  • {m}")
        print("\nCopy .env.example to .env and fill in the values.")
        sys.exit(1)


def main():
    parser = argparse.ArgumentParser(
        prog="job_search",
        description="Automated job search — finds, scores, and drafts applications for you.",
    )
    sub = parser.add_subparsers(dest="command")

    run_parser = sub.add_parser("run", help="Run the full job search pipeline")
    run_parser.add_argument("--dry-run", action="store_true", help="Simulate without creating drafts or events")
    run_parser.add_argument("--no-email", action="store_true", help="Skip Gmail draft creation")
    run_parser.add_argument("--no-calendar", action="store_true", help="Skip calendar event creation")

    sub.add_parser("status", help="Show application status dashboard")
    sub.add_parser("followup", help="List applications needing follow-up")

    args = parser.parse_args()
    if args.command == "run":
        cmd_run(args)
    elif args.command == "status":
        cmd_status(args)
    elif args.command == "followup":
        cmd_followup(args)
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
