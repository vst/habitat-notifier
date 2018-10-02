package com.vsthost.rnd.habitat.notifier

import cats.tagless.finalAlg

import scala.language.higherKinds


/**
  * Defines notifier capability.
  *
  * @tparam F Context type parameter.
  */
@finalAlg
trait Notifier[F[_]] {
  /**
    * Notifies the target with the given note.
    *
    * @param note       The note to notify target with.
    * @param formatter  Implicit note formatter for the given note type.
    * @tparam A         Type of the note.
    */
  def notify[A](note: A)(implicit formatter: NoteFormatter[A]): F[Unit]
}
