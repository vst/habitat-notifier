package com.vsthost.rnd.habitat.notifier

/**
  * Provides a bunch of implicits for convenience.
  */
package object implicits {
  /**
    * Provides default implicit note formatter for common types.
    */
  object formatters {
    /**
      * Defines the default note formatter for given strings.
      */
    implicit val str: NoteFormatter[String] =
      identity[String]
  }
}
