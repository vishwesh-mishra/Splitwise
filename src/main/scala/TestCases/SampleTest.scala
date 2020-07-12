package TestCases

import Models.{Expense, User}
import Services._

object SampleTest {
  val UserService = new UserService()
  UserService.addUser("u1","User1","user1@fake.com",1)
  UserService.addUser("u2","User2","user2@fake.com",2)
  UserService.addUser("u3","User3","user3@fake.com",3)
  UserService.addUser("u4","User4","user4@fake.com",4)

  val ExpenseService = new ExpenseService()
  ExpenseService.showExpenses(UserService.getAllUsers(), UserService)
  ExpenseService.showExpenses(UserService.getUserById("u1"), UserService)
  var newExpense: Expense = ExpenseService.createExpense("u1",1000D, Array[String]("u1","u2","u3","u4"),"EQUAL")
  ExpenseService.showExpenses(UserService.getUserById("u4"), UserService)
  ExpenseService.showExpenses(UserService.getUserById("u1"), UserService)
  newExpense = ExpenseService.createExpense("u1",1250D, Array[String]("u2","u3"),"EXACT",Some(Array[Double](370D,880D)))
  ExpenseService.showExpenses(UserService.getAllUsers(), UserService)
  newExpense = ExpenseService.createExpense("u4",1200D, Array[String]("u1","u2","u3","u4"),"PERCENT",Some(Array[Double](40D,20D,20D,20D)))
  ExpenseService.showExpenses(UserService.getUserById("u1"), UserService)
  ExpenseService.showExpenses(UserService.getAllUsers(), UserService)
}
