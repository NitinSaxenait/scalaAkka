package routers

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}
// This application will use stash in which actor will hold the request and transfer the call to the other Actor.
object stashingState extends App {
  val actorSystem = ActorSystem("actorSystem")

  case object Open

  case object Close

  case object Read

  case class Write(user_content: String)

  class StashActor extends Actor with
    ActorLogging
    with Stash {
    var content: String = ""
// By default we have set the receiving end as close.
    override def receive: Receive = close

    def open: Receive = {
      case Read => {
        log.info(s"I am readings the content [$content]")
      }
      case Write(user_content) => {
        log.info(s"I am writing the content [$user_content]")
        content = user_content
      }
      case Close => {
        log.info("This operation will be handled in close state, changing the state to close")
        context.become(close)
        // Now here unstashAll will throw all the hold request to the new state.
        unstashAll()
      }
      case message => {
        log.info(s"Sorry i can't handle this request [$message]")
        // This is the main method here which will hold all the coming request in stash.
        stash()
      }
    }

    def close: Receive = {
      case Open => {
        log.info("Changing the state to Open")
        context.become(open)
        unstashAll()

      }
      case message => {
        log.info(s"I can't handle this request [$message]")
        stash()
      }

    }

  }

  val caller = actorSystem.actorOf(Props[StashActor], "caller")
  caller ! Read
  caller ! Open
  caller ! Write("Hello There")
  caller ! Read


}
