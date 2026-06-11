# ADR 0001: Fixed calendar occurrences

**Status**: Accepted

## Decision

Occurrences are computed from a fixed anchor date + interval, not rolled forward from the last completion date.

## Context

Two models were considered:

- **Fixed calendar**: "Bookkeeping" always appears Jan 1, Feb 1, Mar 1 — regardless of when (or whether) it was completed.
- **Rolling from completion**: completing "Bookkeeping" on Jan 15 schedules the next occurrence for Feb 15.

## Reasoning

Fixed calendar was chosen because:

1. Some tasks (tax review) have hard external deadlines tied to specific calendar dates — rolling would drift away from them.
2. The full-year view is only meaningful with fixed occurrences: you can see all 12 bookkeeping slots before the year starts, plan around them, and see gaps.
3. The schedule config stays simple: anchor + interval fully defines the year, no need to persist last-completion dates to compute next-due.

The downside — a skipped household task looks "behind" rather than just delayed — is acceptable because the UI treats past uncompleted occurrences as neutral rather than alarming.
