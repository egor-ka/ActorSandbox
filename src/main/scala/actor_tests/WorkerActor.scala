package actor_tests

import actor_tests.LocalActorMessages._
import akka.actor.Actor
import akka.event.Logging

class WorkerActor extends Actor {
  val log = Logging(context.system, this)

  def receive: Receive = {
    case InitWorker =>
      log.info(s"Initialized worker: ${self.path.name}")
      sender ! TagRequest
    case LoadAndSaveMessagesForTag(tag) =>
      val storedMessages = FakeStorage.getMessages(tag)
      val messageIds = storedMessages.map(_.id)
      log.info(s"For tag [${tag.name}] Loaded messages with ids: $messageIds")

      //ToDo: Parallelize this part
      storedMessages.foreach {
        message =>
          FakeFileSystem.saveBytesToFile(message)
          log.info(s"Saved message with id: ${message.id}")
      }
      sender ! ProcessingAcknowledgement(tag)
      sender ! TagRequest
    case unknownRequest => sender ! UnknownRequest(unknownRequest.toString)
  }
}