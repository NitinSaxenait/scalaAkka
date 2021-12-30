package akkaChildActors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akkaChildActors.childActors.Parent.{CreateChild, TellChild}

// This application practice on making an Child actor from Parent actor.
// For this we used content.actorOf()
// We have also included Logging in the Application for better explanation.
object childActors extends App {
  object Parent {
    case class CreateChild(childName: String)

    case class TellChild(message: String)
  }

  class Parent extends Actor with ActorLogging {

    import Parent._

    override def receive: Receive = {
      case CreateChild(childName) => {
        log.info("Creating Child")
        val childRef = context.actorOf(Props[Child], childName)
        context.become(withChild(childRef))
      }
    }

    def withChild(childReference: ActorRef): Receive = {
      case TellChild(message) => {
        childReference ! message
      }
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(s"$self I got [$message]")
    }
  }

  val actorSystem = ActorSystem("actorSystem")
  val parent = actorSystem.actorOf(Props[Parent], "parent")
  parent ! CreateChild("myChild")
  parent ! TellChild("Hi new Born Child")
}
