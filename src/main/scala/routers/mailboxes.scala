package routers

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

object mailboxes extends App {
  val actorSystem = ActorSystem("mailboxes")

  class MailBoxes extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => {
        log.info(message.toString)
      }
    }
  }

  class PriorityMailBox(setting: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
    PriorityGenerator {
      case message: String if message.startsWith("[P0]") => 0
      case message: String if message.startsWith("[P1]") => 1
      case message: String if message.startsWith("[P2]") => 2
      case message: String if message.startsWith("[P3]") => 3
      case _ => 4

    })

  val caller = actorSystem.actorOf(Props[MailBoxes].withDispatcher("ticket-dispatcher"))
  caller! PoisonPill
  // Thread.sleep(1000)
  caller ! "[P3] Hi RUST"
  caller ! "[P0] Hi RUST"
  caller ! "[P1] Hi RUST"

}
