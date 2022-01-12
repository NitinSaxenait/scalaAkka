package routers

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.{Broadcast, RoundRobinPool}

object routing extends App {
  val systemActor = ActorSystem("actorSystem")

  class Route extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => {
        log.info(message.toString)
      }
    }
  }

  // this is the method to create multiple slave(actor) which follows round-robin approach.
  // routing is done to send same set of message to the multiple actors.
  val poolMaster = systemActor.actorOf(RoundRobinPool(5).props(Props[Route]), "poolMaster")
  for (i <- 1 to (10)) {
    poolMaster ! s"${i} Hi slave"

  }
  // This will send the message to each of the actor with the other messages.
  poolMaster ! Broadcast("Hi")
}
