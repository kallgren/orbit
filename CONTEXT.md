# Orbit

A calm full-year surface for recurring maintenance tasks — the monthly-to-yearly complement to OnTop's weekly/fortnightly rhythm.

## Language

**Task**:
A named maintenance chore with a fixed recurring schedule. Defined by a name, category, interval, and anchor date. A task is not an occurrence — it is the template that generates occurrences.
_Avoid_: item, chore, reminder, todo

**Category**:
The kind of a task. Exactly two exist: `:digital` and `:household`. Fixed, not user-configurable.
_Avoid_: section, group, type

**Interval**:
The cadence at which a task recurs. Expressed as a keyword: `:monthly`, `:bimonthly`, `:quarterly`, `:biannual`, `:annual`. No arbitrary cadences.
_Avoid_: frequency, period, recurrence

**Anchor date**:
The date of the first occurrence of a task. All future occurrences are computed by stepping forward from the anchor by the interval. The anchor is a calendar date, not a completion date.
_Avoid_: start date, seed date

**Occurrence**:
A specific dated instance of a task, derived by expanding a task's anchor date and interval across the year. "Bookkeeping on 2026-03-01" is an occurrence. Occurrences are computed — they are never stored.
_Avoid_: instance, entry, slot

**Completion**:
The record that a specific occurrence has been marked done. Keyed by task name and occurrence date. Persists across sessions in localStorage. Independent per occurrence — completing one does not affect others.
_Avoid_: check, status, done-state, progress

**Schedule**:
The full set of task definitions (name, category, interval, anchor date) from which all occurrences are computed. Stored as JSON in localStorage; a placeholder schedule lives in source.
_Avoid_: config, plan, calendar

**Current**:
An occurrence whose date is today or earlier, and which has not been completed. The default view shows only current occurrences.
_Avoid_: active, due, overdue, pending

**Upcoming**:
An occurrence whose date is in the future. Hidden by default; revealed by the "show upcoming" toggle.
_Avoid_: future, scheduled, pending
