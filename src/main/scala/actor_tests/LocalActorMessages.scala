package actor_tests

object LocalActorMessages {

  sealed trait InternalMessage

  object CheckWorkers extends InternalMessage

  object InitWorker extends InternalMessage

  case class LoadMessagesForTags(tags: Seq[MessageTag]) extends InternalMessage

  case class LoadAndSaveMessagesForTag(tag: MessageTag) extends InternalMessage

  // I'm done, give me new tag
  case class LoadAcknowledgement(tag: MessageTag) extends InternalMessage

  object TagRequest extends InternalMessage

  case class UnknownRequest(message: String) extends InternalMessage

}