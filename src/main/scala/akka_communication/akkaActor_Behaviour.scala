package akka_communication

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka_communication.akkaActor_Behaviour.Mother.MotherStart

object akkaActor_Behaviour extends App {

  val actorSystem = ActorSystem("actorSystem")

  /*
  TASK:
  CREATE MOTHER ACTOR-> 1. SEND OPTIONS FOR FOOD TO CHILD 2. ASK WHETHER HE LIKED IT OR NOT
  CREATE CHILD ACTOR-> 1. REPLY WITH MOOD BASED ON FOOD PROVIDED:

   */
  object Mother {
    case class MotherStart(childReference: ActorRef)

    case class Food(food: String)

    case class AskTo(message: String)

    val VEGETABLE = "loki"
    val ICE_CREAM = "iceCream"
  }

  object Child {
    case object KidAccept

    case object KidReject

  }

  class Child extends Actor {

    import Mother._
    import Child._

    override def receive: Receive = childStateHappy()

    def childStateHappy(): Receive = {
      case Food(VEGETABLE) => context.become(childStateSad())
      case Food(ICE_CREAM) => context.unbecome()
      case AskTo(_) => sender() ! KidAccept
    }

    def childStateSad(): Receive = {
      case Food(VEGETABLE) => context.unbecome()
      case Food(ICE_CREAM) => context.become(childStateHappy())
      case AskTo(_) => sender() ! KidReject
    }
  }

  class Mother extends Actor {

    import Child._
    import Mother._

    override def receive: Receive = {
      case MotherStart(childReference) => {
        childReference ! Food(ICE_CREAM)
        childReference ! Food(VEGETABLE)
        childReference ! Food(ICE_CREAM)


        childReference ! AskTo("Do you like the Food!")
      }
      case KidAccept => println("Yey, it was so yummy!")
      case KidReject => println("No i did not like it at all!")
    }

  }

  val motherCalling = actorSystem.actorOf(Props[Mother])
  val childCalling = actorSystem.actorOf(Props[Child])
  motherCalling ! MotherStart(childCalling)


}
