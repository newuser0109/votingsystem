import sqlite3
from contextlib import contextmanager
from datetime import datetime
from typing import Optional

from .config import DB_PATH
from .models import Application, ApplicationStatus


class ApplicationTracker:
    def __init__(self, db_path: str = DB_PATH):
        self.db_path = db_path
        self._init_db()

    @contextmanager
    def _conn(self):
        conn = sqlite3.connect(self.db_path)
        conn.row_factory = sqlite3.Row
        try:
            yield conn
            conn.commit()
        finally:
            conn.close()

    def _init_db(self):
        with self._conn() as conn:
            conn.execute("""
                CREATE TABLE IF NOT EXISTS seen_jobs (
                    job_id TEXT PRIMARY KEY,
                    seen_at TEXT NOT NULL
                )
            """)
            conn.execute("""
                CREATE TABLE IF NOT EXISTS applications (
                    job_id          TEXT PRIMARY KEY,
                    title           TEXT NOT NULL,
                    company         TEXT NOT NULL,
                    url             TEXT NOT NULL,
                    status          TEXT NOT NULL DEFAULT 'new',
                    fit_score       INTEGER DEFAULT 0,
                    applied_date    TEXT DEFAULT '',
                    draft_id        TEXT DEFAULT '',
                    calendar_event_id TEXT DEFAULT '',
                    notes           TEXT DEFAULT '',
                    created_at      TEXT NOT NULL
                )
            """)

    def is_seen(self, job_id: str) -> bool:
        with self._conn() as conn:
            row = conn.execute(
                "SELECT 1 FROM seen_jobs WHERE job_id = ?", (job_id,)
            ).fetchone()
            return row is not None

    def mark_seen(self, job_id: str):
        with self._conn() as conn:
            conn.execute(
                "INSERT OR IGNORE INTO seen_jobs (job_id, seen_at) VALUES (?, ?)",
                (job_id, datetime.now().isoformat()),
            )

    def save_application(self, app: Application):
        with self._conn() as conn:
            conn.execute("""
                INSERT OR REPLACE INTO applications
                    (job_id, title, company, url, status, fit_score,
                     applied_date, draft_id, calendar_event_id, notes, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                app.job_id, app.title, app.company, app.url,
                app.status.value, app.fit_score, app.applied_date,
                app.draft_id, app.calendar_event_id, app.notes, app.created_at,
            ))

    def update_status(self, job_id: str, status: ApplicationStatus, **kwargs):
        fields = {"status": status.value}
        fields.update(kwargs)
        set_clause = ", ".join(f"{k} = ?" for k in fields)
        values = list(fields.values()) + [job_id]
        with self._conn() as conn:
            conn.execute(
                f"UPDATE applications SET {set_clause} WHERE job_id = ?", values
            )

    def get_all_applications(self) -> list[Application]:
        with self._conn() as conn:
            rows = conn.execute(
                "SELECT * FROM applications ORDER BY created_at DESC"
            ).fetchall()
        return [self._row_to_app(r) for r in rows]

    def get_by_status(self, status: ApplicationStatus) -> list[Application]:
        with self._conn() as conn:
            rows = conn.execute(
                "SELECT * FROM applications WHERE status = ? ORDER BY created_at DESC",
                (status.value,),
            ).fetchall()
        return [self._row_to_app(r) for r in rows]

    def get_application(self, job_id: str) -> Optional[Application]:
        with self._conn() as conn:
            row = conn.execute(
                "SELECT * FROM applications WHERE job_id = ?", (job_id,)
            ).fetchone()
        return self._row_to_app(row) if row else None

    @staticmethod
    def _row_to_app(row) -> Application:
        return Application(
            job_id=row["job_id"],
            title=row["title"],
            company=row["company"],
            url=row["url"],
            status=ApplicationStatus(row["status"]),
            fit_score=row["fit_score"],
            applied_date=row["applied_date"],
            draft_id=row["draft_id"],
            calendar_event_id=row["calendar_event_id"],
            notes=row["notes"],
            created_at=row["created_at"],
        )
