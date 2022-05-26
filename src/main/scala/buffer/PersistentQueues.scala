package buffer

import messages.Message

import scala.collection.mutable.ArrayBuffer

object PersistentQueues {

  val buffer = new ArrayBuffer[Queue]()

  def createQueue(topic: String, message: Message) = {
    buffer.addOne(new Queue {
      override def name: String = topic

      override def messages: ArrayBuffer[Message] = ArrayBuffer(message)
    })
  }

  def getQueue(name: String): Any = {
    for (a <- buffer.indices){
      if (buffer(a).name.equals(name)){
        return buffer(a)
      }
    }
  }

}
