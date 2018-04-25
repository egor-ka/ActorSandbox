package actor_tests

import akka.actor.{Actor, PoisonPill, Props}
import LocalActorMessages._
import akka.event.Logging

class HubActor extends Actor {
  val log = Logging(context.system, this)

  // ToDo: pass this argument through cmd params
  private val poolSize = 2

  private var workerPool = (for (i <- 1 to poolSize) yield context.actorOf(Props[WorkerActor], s"worker$i")).toSet

  private var allTags: Seq[MessageTag] = _

  // ToDo: think about refactoring this system to to FSM
  def receive: Receive = {
    case LoadMessagesForTags(tags) =>
      allTags = tags
      workerPool.foreach(_ ! InitWorker)
    case TagRequest => processLoadRequest()
    case LoadAcknowledgement(tag) =>
      log.info(s"Finished processing messages for tag: ${tag.name}")
      processLoadRequest()
    case CheckWorkers => if (workerPool.isEmpty) context.system.terminate()
    case unknownRequest => log.info(s"Received unknown request: ${unknownRequest.toString}")
  }

  private def processLoadRequest() = {
    provideTag() match {
      case Some(tag) => sender ! LoadAndSaveMessagesForTag(tag)
      case _ =>
        log.info(s"Sending PoisonPill to worker: ${sender.path.name}")
        sender ! PoisonPill
        workerPool = workerPool - sender
        self ! CheckWorkers
    }
  }

  private def provideTag(): Option[MessageTag] = {
    if (allTags.nonEmpty) {
      val tag = allTags.head
      allTags = allTags.tail
      Some(tag)
    } else {
      None
    }
  }
}
