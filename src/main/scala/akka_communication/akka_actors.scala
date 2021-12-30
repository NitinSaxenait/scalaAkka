package akka_communication

import akka.actor.{Actor, ActorSystem, Props}

object akka_actors extends App {
  // Step 1: create an actor System which will lead the communication.
  val actorSystem = ActorSystem("actorSystem")
  println(actorSystem.name)

  // Step 2: Create Actors:
  class myActor extends Actor {
    def receive: PartialFunction[Any, Unit] = {
      // {context.self.path} this is used to identify the actor uniquely.
      case message: String => println(s"${context.self.path}--The message received successfully, The message is [$message]")
      case integer: Int => println(s"The message is [$integer]")
      case anyType(message) => println(s"The message is [$message]")
      case _ => println("Wrong call!!")
    }

  }

  // Step 3: Invoking the Actors and communicate
  val actorCall = actorSystem.actorOf(Props[myActor], "actorCall")
  val anotherActorCall = actorSystem.actorOf(Props[myActor], "anotherActorCall")
  val specialActor = actorSystem.actorOf(Props[myActor], "specialActor")
  // can send any type in the "Message"
  actorCall ! "hello Aka"
  actorCall ! 2
  anotherActorCall ! "Hi i am another actor call"

  case class anyType(message: String)

  specialActor ! anyType("HelloActors i can be of any type")

}

