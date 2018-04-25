package actor_tests

import java.util.UUID

import scala.util.Random

object Storage {
  def getMessages(tag: MessageTag): Seq[StoredMessage] = generateMessages()

  private def generateMessages() = {
    val messageNumberForTag = Random.nextInt() % 5 + 5
    for {
      _ <- 0 until messageNumberForTag
    } yield {
      val id = UUID.randomUUID().toString
      StoredMessage(id, "message bytes $id")
    }
  }
}