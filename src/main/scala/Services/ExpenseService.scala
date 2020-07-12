package Services

import Models.{Expense, ExpenseShare, User}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ExpenseService {
  private var eid = 1

  var expenseArray: ArrayBuffer[Expense] = ArrayBuffer[Expense]()
  var expenseShareArray: ArrayBuffer[ExpenseShare] = ArrayBuffer[ExpenseShare]()

  def storeExpenseShares(payerId: String, expense: Double, receivers: Array[String], shareType: String, shares: Option[Array[Double]]): Unit = {
    try {
      var userShareMap: Map[String, Double] = Map()

      shareType.toLowerCase match {
        case "equal" =>
          val share = expense / receivers.length
          receivers.foreach(receiver => userShareMap += (receiver -> share))
        case "exact" =>
          val exactShares = shares.getOrElse(Array[Double]())
          var totalShare = 0D
          for ((receiver, share) <- (receivers zip exactShares)) {
            userShareMap += (receiver -> share)
            totalShare += share
          }
          if(totalShare != expense) throw new Exception("Total share is not equal to total expense!")
        case "percent" =>
          val percentShares = shares.getOrElse(Array[Double]())
          for ((receiver, share) <- (receivers zip percentShares)) userShareMap += (receiver -> (share / 100) * expense)
      }
      for ((receiverId, share) <- userShareMap)
        if (payerId != receiverId) expenseShareArray += ExpenseShare(eid, payerId, receiverId, share)
    } catch {
      case ex: Exception => throw ex;
    }
  }

  def createExpense(payerId: String, expense: Double, receivers: Array[String], shareType: String, shares: Option[Array[Double]]=None): Expense = {
    val newExpense = Expense(eid, payerId, expense)
    expenseArray += newExpense
    storeExpenseShares(payerId, expense, receivers, shareType, shares)
    eid += 1
    newExpense
  }

  def calculateOweAmounts(oweMap: mutable.Map[(String,String),Double], uId: String): mutable.Map[(String,String),Double] = {
    val asPayerArray: ArrayBuffer[ExpenseShare] = expenseShareArray.filter(e => e.payerId==uId)
    asPayerArray.foreach(e => {
      if(!oweMap.exists(_._1 == (e.receiverId,e.payerId))) {
        val key = (e.payerId, e.receiverId)
        oweMap += (key -> (oweMap.getOrElse(key, 0D) + e.share))
      }
    })

    val asReceiverArray: ArrayBuffer[ExpenseShare] = expenseShareArray.filter(e => e.receiverId==uId)
    asReceiverArray.foreach(e => {
      if(!oweMap.exists(_._1 == (e.payerId,e.receiverId))) {
        val key = (e.receiverId, e.payerId)
        oweMap += (key -> (oweMap.getOrElse(key, 0D) - e.share))
      }
    })

    oweMap
  }

  def showExpenses(users: ArrayBuffer[User], userService: UserService): Unit = {
    var oweMap: mutable.Map[(String,String),Double] = mutable.Map()
    val userIds = users.map(user => user.uId)
    userIds.foreach(uId => oweMap ++= calculateOweAmounts(oweMap, uId))
//    userId match {
//      case Some(uId) =>
//        oweMap = calculateOweAmounts(oweMap, uId)
//      case _ =>
//        val relevantUserIds = expenseShareArray.map(es => es.payerId).toList ::: expenseShareArray.map(es => es.receiverId).toList
//        val relevantIds = relevantUserIds.toSet
//        relevantIds.foreach(uId => oweMap ++= calculateOweAmounts(oweMap, uId))
//    }
    println("SHOW BALANCE")
    if(oweMap.nonEmpty) {
      oweMap.foreach(kv => {
        val user1 = userService.getUserById(kv._1._1)(0).name
        val user2 = userService.getUserById(kv._1._2)(0).name
        val amount = kv._2
        if (amount > 0) println(s"$user2 owes $user1: $amount")
        else println(s"$user1 owes $user2: ${-amount}")
      })
      println()
    } else println("No balances \n")
  }
}
