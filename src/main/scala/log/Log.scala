package log

import java.io.{File, PrintWriter}

import scala.io.Source
//import scalax.io._

object Log {

  val file = new File("textFile.txt")
  var fileText = "filebegin"
//  val writer = new PrintWriter(file)

//  val writer = new PrintWriter(new File(filePath))

  def writeLog(text:String) = {
    val writer = new PrintWriter(file)
    val fSource = Source.fromFile(file)
    while (fSource.hasNext){
      fileText = fileText + "\n" + fSource.next().toString
      println("                             File iteration: " + fileText)
    }
    fSource.close()
//    fileText = fileText + fSource.mkString
    writer.write(fileText + "\n" + text)
    writer.close()
  }

}
