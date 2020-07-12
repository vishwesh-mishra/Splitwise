package Services

import Models.User
import scala.collection.mutable.ArrayBuffer

class UserService {
  var usersArray: ArrayBuffer[User] = ArrayBuffer[User]()

  def addUser(uId: String, name: String, email: String, mobile: Int) = usersArray += User(uId,name,email,mobile)

  def getUserById(uId: String): ArrayBuffer[User] = usersArray.filter(_.uId == uId)
  def getAllUsers(): ArrayBuffer[User] = usersArray
}
