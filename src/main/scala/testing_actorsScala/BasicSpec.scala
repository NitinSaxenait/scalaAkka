package testing_actorsScala

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import testing_actorsScala.BasicSpec.{SecondTest, SimpleTest}

class BasicSpec extends TestKit(ActorSystem("BasicSpec")) with
  ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "This SimpleTest " should {
    val simpleActor = system.actorOf(Props[SimpleTest])
    "return the same message it sends" in {

      val message = "Hi first test"
      simpleActor ! message
      expectMsg(message)
    }
    "second test" in {
      val test2 = "Hi test 2"
      simpleActor ! test2
      expectMsg("Hello there")
    }
  }
  "Second test case" should {
    "return whether the receiver received the message or not " in {
      val caller = system.actorOf(Props[SecondTest])
      val message = "Hi second test"
      caller ! message
      expectMsg("Hi second test, have i done the testing successfully?")
    }
  }

}

object BasicSpec {
  class SimpleTest extends Actor {
    override def receive: Receive = {
      case "Hi test 2" => {
        sender() ! "Hello there"
      }
      case message => {
        sender() ! message
      }


    }
  }

  class SecondTest extends Actor {
    override def receive: Receive = {
      case "Hi second test" => {
        sender() ! "Hi second test, have i done the testing successfully?"
      }

    }
  }
}
