package cn.thinkjoy.scala4fun.file

import better.files.File

/**
 * Hello world!
 *
 */
object FileApp{
  def main(args: Array[String]) {
    val file=File("test.txt").createIfNotExists()
    file
  }
}
