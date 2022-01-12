package akka_communication

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka_communication.exercises.Counter.{Decrement, Increment, Print}

object exercises extends App {
  // This exercise is going to perform two operations:
  // 1. INCREMENT
  // 2. DECREMENT
  val actorSystem = ActorSystem("actorSystem")

  object Counter {
    case object Increment

    case object Decrement

    case object Print
  }

  class counter extends Actor {
    var counter = 0

    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(s"[counter] The value of counter is $counter")
    }
  }

  val caller = actorSystem.actorOf(Props[counter], "caller")
  for (count <- 1 to 5) {
    caller ! Increment
  }
  for (count <- 1 to 3) {
    caller ! Decrement
  }
  caller ! Print

  // Exercise for implementing a Bank Account:
  var amount = 0

  case class DepositAmount(amountDeposit: Int)

  case class WithdrawAmount(amountWid: Int)

  case class Statement(depositAmount: Int, withdrawAmount: Int)


  class bankAccount extends Actor {
    override def receive: Receive = {
      case DepositAmount(amountDeposit) => {
        if (amountDeposit <= 0) {
          sender() ! "Invalid Deposit."

        }
        else {
          amount += amountDeposit
          println("Money deposit successfully")

        }
      }
      case WithdrawAmount(amountWid) => {
          if (amountWid >= amount) {
            println("Withdraw Failed, Insufficient Balance")
          }
          else {
            amount -= amountWid
            println("Money withdraw successfully")


          }

      }
      case Statement(depositAmount, withdrawAmount) => {
        println(s"The amount Deposited in you account is $depositAmount and the amount Withdraw is $withdrawAmount.")
        println(s"The Total amount in the Account is $amount.")
      }
    }
  }

  val bankCaller = actorSystem.actorOf(Props[bankAccount], "bankCaller")
  val moneyToDeposit = 0
  val moneyToWithdraw = 1
  bankCaller ! DepositAmount(moneyToDeposit)
  bankCaller ! WithdrawAmount(moneyToWithdraw)
  bankCaller ! Statement(moneyToDeposit, moneyToWithdraw)

}
