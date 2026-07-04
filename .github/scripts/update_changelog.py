#!/usr/bin/env python3
"""Insert a new version section into docs/changelog.md from a GitHub release body.

Reads the release notes body from /tmp/release_body.md and the version from the
RELEASE_VERSION env var, then prepends a Keep-a-Changelog style section right
after the "## [Unreleased]" heading.
"""
import os
import pathlib
from datetime import datetime, timezone

RELEASE_BODY_PATH = pathlib.Path("/tmp/release_body.md")
CHANGELOG_PATH = pathlib.Path("docs/changelog.md")
UNRELEASED_MARKER = "## [Unreleased]\n"


def main():
    version = os.environ["RELEASE_VERSION"]
    date = datetime.now(timezone.utc).strftime("%Y-%m-%d")

    body_lines = RELEASE_BODY_PATH.read_text().splitlines()
    if body_lines and body_lines[0].strip().lower() == "## what's changed":
        body_lines = body_lines[1:]
    body = "\n".join(body_lines).strip()

    text = CHANGELOG_PATH.read_text()
    idx = text.index(UNRELEASED_MARKER) + len(UNRELEASED_MARKER)
    entry = f"\n## [{version}] — {date}\n\n{body}\n"
    CHANGELOG_PATH.write_text(text[:idx] + entry + text[idx:])


if __name__ == "__main__":
    main()
