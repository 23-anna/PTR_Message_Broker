package messages

trait Acknowledgement {
  def id: Int
  def actorName: String
  var status: Int
}
