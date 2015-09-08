package sbt.compiler.javac

import java.io.File

import sbt.util.Logger
import sbt.internal.util.UnitSpec

class JavaErrorParserSpec extends UnitSpec {

  "The JavaErrorParser" should "be able to parse linux errors" in parseSampleLinux()
  it should "be able to parse windows file names" in parseWindowsFile()
  it should "be able to parse windows errors" in parseSampleWindows()

  def parseSampleLinux() = {
    val parser = new JavaErrorParser()
    val logger = Logger.Null
    val problems = parser.parseProblems(sampleLinuxMessage, logger)

    problems should have size (1)
    problems(0).position.sourcePath.get shouldBe ("/home/me/projects/sample/src/main/Test.java")

  }

  def parseSampleWindows() = {
    val parser = new JavaErrorParser()
    val logger = Logger.Null
    val problems = parser.parseProblems(sampleWindowsMessage, logger)

    problems should have size (1)
    problems(0).position.sourcePath.get shouldBe (windowsFile)

  }

  def parseWindowsFile() = {
    val parser = new JavaErrorParser()
    parser.parse(parser.fileAndLineNo, sampleWindowsMessage) match {
      case parser.Success((file, line), rest) => file shouldBe (windowsFile)
      case parser.Error(msg, next)            => assert(false, s"Error to parse: $msg, ${next.pos.longString}")
      case parser.Failure(msg, next)          => assert(false, s"Failed to parse: $msg, ${next.pos.longString}")
    }
  }

  def sampleLinuxMessage =
    """
      |/home/me/projects/sample/src/main/Test.java:4: cannot find symbol
      |symbol  : method baz()
      |location: class Foo
      |return baz();
    """.stripMargin

  def sampleWindowsMessage =
    s"""
      |$windowsFile:4: cannot find symbol
      |symbol  : method baz()
      |location: class Foo
      |return baz();
    """.stripMargin

  def windowsFile = """C:\Projects\sample\src\main\java\Test.java"""
  def windowsFileAndLine = s"""$windowsFile:4"""
}
