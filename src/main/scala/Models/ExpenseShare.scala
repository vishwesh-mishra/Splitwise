package Models

case class ExpenseShare(
                         eid: Int,
                         payerId: String,
                         receiverId: String,
                         share: Double
                       )
