package com.vsthost.rnd.habitat.notifier

import cats.effect._
import cats.syntax.all._
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.Json

import scala.language.higherKinds

/**
  * Provides a simple notifier via Slack.
  *
  * @param url  Slack webhook URL.
  * @param name Optional name for the Slack username.
  * @param F    Evidence for the context type parameter.
  * @tparam F   Context type parameter.
  */
class SimpleSlackNotifier[F[_]](url: Uri, name: Option[String])(implicit F : Sync[F]) extends Notifier[F] {
  /**
    * Defines the STTP backend to be used.
    */
  // TODO: Shall we use another effect type other than [[Id]]?
  implicit private val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  /**
    * Defines default headers which will be send along with each request.
    */
  private val defaultHeaders = Map("Accept" -> "application/json")

  /**
    * Defines the base STTP client.
    */
  private val client = F.pure(sttp.headers(defaultHeaders))

  /**
    * Formats the note and packages into a Slack post request payload.
    *
    * @param note       Note for the payload.
    * @param formatter  Formatter for the note type.
    * @tparam A         Type of the note.
    * @return Slack post request payload.
    */
  private def makePayload[A](note: A, formatter: NoteFormatter[A]): Json = {
    // Get the JSON formatted note:
    val jsonNote = Json.fromString(formatter.format(note))

    // Prepare the JSON payload as per name parameter and return:
    name match {
      case None => Json.obj("text" -> jsonNote)
      case Some(x) => Json.obj("username" -> Json.fromString(x), "text" -> jsonNote)
    }
  }

  /**
    * Sends the given payload to Slack.
    *
    * Note that this method may throw a runtime exception.
    *
    * @param payload Payload to be sent.
    * @return [[Unit]] in context.
    */
  private def send(payload: Json): F[Unit] = client.map { c =>
    c.post(url).body(payload).response(asJson[Unit]).send().body match {
      case Left(error) => throw new RuntimeException(error)
      case _ => ()
    }
  }

  /**
    * Notifies the target with the given note.
    *
    * @param note       The note to notify target with.
    * @param formatter  Implicit note formatter for the given note type.
    * @tparam A         Type of the note.
    */
  override def notify[A](note: A)(implicit formatter: NoteFormatter[A]): F[Unit] =
    send(makePayload(note, formatter))
}

/**
  * Provides a companion object to [[SimpleSlackNotifier]] for convenience definitions and functions.
  */
object SimpleSlackNotifier {
  /**
    * Provides a default instance for `Notifier[IO]`.
    *
    * @param url  Slack URL.
    * @return An instance of [[Notifier]] for the [[IO]] instance.
    */
  def io(url: Uri): SimpleSlackNotifier[IO] = new SimpleSlackNotifier[IO](url, None)

  /**
    * Provides a default instance for `Notifier[IO]`.
    *
    * @param url  Slack URL.
    * @param name Name as the Slack username.
    * @return An instance of [[Notifier]] for the [[IO]] instance.
    */
  def io(url: Uri, name: String): SimpleSlackNotifier[IO] = new SimpleSlackNotifier[IO](url, Some(name))
}
