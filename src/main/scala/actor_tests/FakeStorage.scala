package actor_tests

import java.util.UUID

import scala.util.Random

object FakeStorage {
  def getMessages(tag: MessageTag): Seq[StoredMessage] = {
    Thread.sleep(100)
    generateMessages()
  }

  private def generateMessages() = {
    val messageNumberForTag = Random.nextInt() % 5 + 1
    for {
      _ <- 0 to messageNumberForTag
    } yield {
      val id = UUID.randomUUID().toString
      StoredMessage(id, "message bytes $id")
    }
  }
}