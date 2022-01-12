package actorStop

import actorStop.stopActors.Parent.{StartChild, StopChild}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}
import akkaChildActors.childActors.Parent.CreateChild

object stopActors extends App {
  class Parent extends Actor with ActorLogging {

    import Parent._

    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) => {
        log.info(s"Starting child with name $name")
        context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))
      }
      case StopChild(name)
      => {
        log.info("Stop this child")
        val optionChild = children.get(name)
        // context.stop is used to stop an actor
        optionChild.foreach(childRef => context.stop(childRef))
      }
      case StopSelf => {
        log.info("Stop the self")
        // can also stop self actor using the same
        context.stop(self)
      }
    }
  }


  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => {
        log.info(s"the message receive is $message")
      }
    }
  }

  object Parent {
    case class StartChild(name: String)

    case class StopChild(name: String)

    case object StopSelf
  }

  val actorSystem = ActorSystem("actorSystem")
  val parentCaller = actorSystem.actorOf(Props[Parent], "parent")
  parentCaller ! StartChild("child1")
  val childCaller = actorSystem.actorSelection("user/parent/child1")
  childCaller ! "Hi kid"
  parentCaller ! StopChild("child1")
  Thread.sleep(500)
  childCaller ! "Hi"

  /*
  method 2 to stop an actor
   */
  val looseActor = actorSystem.actorOf(Props[Child], "looseActor")
  looseActor ! "hello, loose actor"
  // Use to kill an actors
  looseActor ! PoisonPill // or Kill
  looseActor ! "HI"

  /*
  Method to keep a watch on the actor who is going to die:
   */
  class Watcher extends Actor with ActorLogging {

    override def receive: Receive = {
      case CreateChild(childName) => {
        log.info(s"Creating a new child with name $childName")
        val newChild = context.actorOf(Props[Child], childName)
        log.info(s"Watching $childName child ")
        // context.watch is used to watch the activity on the actor if it terminates or not.
        context.watch(newChild)

      }
      // Terminated is an akka method which is used to detect the watcher if any actor got terminated
      case Terminated(ref) => {
        log.info(s"Watching this $ref child to get terminate")
      }
      case message => {
        log.info(message.toString)
      }
    }
  }

  object Watcher {
    case class CreateChild(childName: String)
  }

  val watcherCaller = actorSystem.actorOf(Props[Watcher], "watcherCaller")
  watcherCaller ! CreateChild("RAMA")
  val newChild = actorSystem.actorSelection("user/watcherCaller/RAMA")
  Thread.sleep(200)
  newChild ! Kill


}
