package akka_communication

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

// This application is to set a communication between two actors:
// actor: samar
// actor: bob
// this is also going to implement a forward type communication.
object communication_usingActors extends App {
  val actorSystem = ActorSystem("actorSystem")

  class communication extends Actor {
    // sender() : This works as a replying to a message where receiver will reply to sender()
    // ref !: This work as a send msg where sender sends the message to the reference e.g ref! "Hi" sender is saying
    // "Hi" to the receiver(ref)
    override def receive: Receive = {
      // If the case receive "Hi" then the sender()! means replying to the sender
      // sender()! == reply this to the sender with msg "Hi there"
      case "Hi" => sender() ! "Hi there"
      case "Hi there" => sender() ! s"Hello ${sender()}"
      case message: String => println(s"${self}--I received message $message")
      // ref! "Hi" this means sender sends its reference a "Hi" msg
      case messageTo(ref) => ref ! "Hi"


    }
  }

  case class messageTo(ref: ActorRef)

  val samar = actorSystem.actorOf(Props[communication], "samar")
  val bob = actorSystem.actorOf(Props[communication], "bob")
  // (bob) is a reference here.So samar is sending msg to "bob" in this case
  samar ! messageTo(bob)
}
