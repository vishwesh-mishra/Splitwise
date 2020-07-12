import TestCases._

object Main extends App {
  try {
    SampleTest
  } catch {
    case exception: Exception => println(exception.getMessage)
  }
}