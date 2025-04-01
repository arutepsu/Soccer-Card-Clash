package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.LoadSelectedGameTuiCommand
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.anyString
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import java.io.{ByteArrayOutputStream, File, FileWriter, PrintStream}

class LoadSelectedGameTuiCommandTest extends AnyFlatSpec with Matchers with MockitoSugar {

  def captureOutput(block: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) { block }
    out.toString.trim
  }

  private def createTempGamesDirectoryWith(files: List[String]): File = {
    val tempDir = new File("games")
    tempDir.mkdirs()
    files.foreach { name =>
      val f = new File(tempDir, name)
      val writer = new FileWriter(f)
      writer.write("dummy content")
      writer.close()
    }
    tempDir
  }

  private def cleanUpTempGamesDir(): Unit = {
    val dir = new File("games")
    if (dir.exists()) {
      dir.listFiles().foreach(_.delete())
      dir.delete()
    }
  }

  "LoadSelectedGameTuiCommand" should "load the selected game when index is valid" in {
    createTempGamesDirectoryWith(List("a_save.json", "b_save.xml"))

    val mockController = mock[IController]
    val mockFactory = mock[ITuiCommandFactory]
    val mockLoadCommand = mock[ITuiCommand]

    when(mockFactory.createLoadGameTuiCommand("a_save.json")).thenReturn(mockLoadCommand)

    val command = new LoadSelectedGameTuiCommand(mockController, index = 0, mockFactory)

    val output = captureOutput {
      command.execute()
    }

    verify(mockFactory).createLoadGameTuiCommand("a_save.json")
    verify(mockLoadCommand).execute()
    output should not include "❗" // ❗ (error symbol)

    cleanUpTempGamesDir()
  }

  it should "print an error if the index is invalid" in {
    createTempGamesDirectoryWith(List("only_one.xml"))

    val mockController = mock[IController]
    val mockFactory = mock[ITuiCommandFactory]

    val command = new LoadSelectedGameTuiCommand(mockController, index = 10, mockFactory)

    val output = captureOutput {
      command.execute()
    }

    output should include("❌ Invalid selection.")
    verify(mockFactory, never()).createLoadGameTuiCommand(anyString())

    cleanUpTempGamesDir()
  }
}