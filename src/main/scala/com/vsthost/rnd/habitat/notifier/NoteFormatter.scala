package com.vsthost.rnd.habitat.notifier

/**
  * Defines the note formatter trait.
  *
  * @tparam A The type of the note to be formatted.
  */
trait NoteFormatter[A] {
  /**
    * Consumes a note and produces a [[String]] representation of it.
    *
    * @param note The note to be formatted.
    * @return [[String]] representation of the note.
    */
  def format(note: A): String
}
