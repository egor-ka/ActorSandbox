package actor_tests

object LocalActorMessages {

  sealed trait InternalMessage

  case class StartProcessing(tags: Seq[MessageTag], workerPoolSize: Int) extends InternalMessage

  object InitHub extends InternalMessage

  case class TerminateSystem(message: String) extends InternalMessage

  object InitWorker extends InternalMessage

  object TagRequest extends InternalMessage

  case class LoadAndSaveMessagesForTag(tag: MessageTag) extends InternalMessage

  case class ProcessingAcknowledgement(tag: MessageTag) extends InternalMessage

  case class UnknownRequest(message: String) extends InternalMessage
}