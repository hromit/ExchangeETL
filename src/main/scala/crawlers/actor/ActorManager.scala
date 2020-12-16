package crawlers.actor

import akka.actor.{ActorRef, ActorSelection, ActorSystem, Props}
import akka.dispatch.MessageDispatcher

import scala.concurrent.ExecutionContext

object ActorManager {
  lazy val system: ActorSystem = ActorSystem("crawlers")

  def getNewSystemActor(dispatcher: String, actorName: String, props: Props): ActorRef =
    system.actorOf(props.withDispatcher(s"crawlers.${dispatcher}"), actorName)

  def getExistedActor(path: String): ActorSelection = system.actorSelection(path)
}

object ExecutionContexts {

  implicit lazy val downloadDispatcher: ExecutionContext = dispatcher("crawlers.downloader-dispatcher")
  implicit lazy val processorDispatcher: ExecutionContext = dispatcher("crawlers.processor-dispatcher")
  implicit lazy val pipelineDispatcher: ExecutionContext  = dispatcher("crawlers.pipeline-dispatcher")


  def dispatcher(id: String): MessageDispatcher = {
    val dispatchers = ActorManager.system.dispatchers
    if(dispatchers.hasDispatcher(id)) {
      dispatchers.lookup(id)
    } else {
      dispatchers.defaultGlobalDispatcher
    }
  }


}