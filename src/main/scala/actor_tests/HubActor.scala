package actor_tests

import actor_tests.LocalActorMessages._
import akka.actor.{ActorRef, FSM, PoisonPill, Props}
import akka.event.Logging

class HubActor extends FSM[State, Data] {
  override val log = Logging(context.system, this)

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(StartProcessing(tags, workerPoolSize), Uninitialized) =>
      log.info("Received StartProcessing message.")
      if (tags.isEmpty || workerPoolSize <= 0) {
        self ! TerminateSystem(s"Requested: tags size = [${tags.size}], workers size = [$workerPoolSize]")
        stay
      } else {
        goto(Active) using TagsAndWorkers(tags, getWorkers(workerPoolSize))
      }
  }

  onTransition {
    case Idle -> Active =>
      log.info("State change: Idle -> Active")
      nextStateData match {
        case TagsAndWorkers(_, workers) => workers.foreach(_ ! InitWorker)
        case _ =>
      }
    case Active -> Active =>
      nextStateData match {
        case TagsAndWorkers(_, workers) if workers.isEmpty => self ! TerminateSystem("No workers active")
        case _ =>
      }
  }

  when(Active) {
    case Event(TagRequest, data@TagsAndWorkers(Nil, workers)) =>
      log.info(s"No tags left to load, sending PoisonPill to worker: ${sender.path.name}")
      sender ! PoisonPill
      goto(Active) using data.copy(workers = workers - sender)
    case Event(TagRequest, data@TagsAndWorkers(tags, _)) =>
      sender ! LoadAndSaveMessagesForTag(tags.head)
      stay using data.copy(tags = tags.tail)
    case Event(ProcessingAcknowledgement(tag), _) =>
      log.info(s"Finished processing messages for tag: ${tag.name}.")
      stay
  }

  whenUnhandled {
    case Event(TerminateSystem(message), _) =>
      log.info(s"$message. Terminating system.")
      context.system.terminate()
      stay
    case Event(request, state) =>
      log.warning(s"Received unhandled request $request in state $state/$stateName")
      stay
  }

  private def getWorkers(workerPoolSize: Int): Set[ActorRef] =
    (for (i <- 1 to workerPoolSize) yield context.actorOf(Props[WorkerActor], s"worker-$i")).toSet
}

sealed trait State
case object Idle extends State
case object Active extends State

sealed trait Data
case object Uninitialized extends Data
case class TagsAndWorkers(tags: Seq[MessageTag], workers: Set[ActorRef]) extends Data