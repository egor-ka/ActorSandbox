package actor_tests

import akka.actor.{ActorSystem, Props}
import LocalActorMessages._

object LoadingApp extends App {

  val system = ActorSystem("loader")

  val hub = system.actorOf(Props[HubActor], "hub")

  val messageTagsForLoading = Seq(
    MessageTag("tag1"),
    MessageTag("tag2"),
    MessageTag("tag3"),
    MessageTag("tag4"),
    MessageTag("tag5")
  )

  val workerPoolSize = 2

  hub ! StartProcessing(messageTagsForLoading, workerPoolSize)
}