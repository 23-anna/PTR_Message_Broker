package buffer

import java.io.{File, FileWriter}

import messages.Message

import scala.collection.mutable.ArrayBuffer

object PersistentQueues {

  val buffer = new ArrayBuffer[Queue]()

  def createQueue(topic: String, message: Message) = {
    buffer += new Queue {
      override def name: String = topic

      override def messages: ArrayBuffer[Message] = ArrayBuffer(message)
    }

    val filename = topic + ".txt"
    val fileWriter = new FileWriter(new File(filename))
//    fileWriter.write("hello there")
    fileWriter.close()
  }

  def getQueue(name: String): Any = {
    for (a <- buffer.indices){
      if (buffer(a).name.equals(name)){
        return buffer(a)
      }
    }
  }

}
