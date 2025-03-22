package de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent

import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.factory.GameDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Using
import scala.xml.{Elem, XML}

class XmlComponentSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val mockDeserializer: GameDeserializer = mock[GameDeserializer]
  val mockGameState: IGameState = mock[IGameState]

  val xmlComponent = new XmlComponent(mockDeserializer)

  "XmlComponent" should {

    "save game state to XML file" in {
      val dummyXml: Elem = <game><player>Alice</player></game>
      when(mockGameState.toXml).thenReturn(dummyXml)

      xmlComponent.save(mockGameState)

      val saved = Source.fromFile("games/game.xml").mkString
      saved should include("Alice")
    }

    "load game state from XML file if file exists" in {
      val testFile = "games/test.xml"
      val dummyXml = <game><player>Bob</player></game>

      // Write dummy XML to file
      Using(new PrintWriter(new File(testFile))) { pw =>
        pw.write(dummyXml.toString())
      }

      when(mockDeserializer.fromXml(any())).thenReturn(mockGameState)

      val result = xmlComponent.load("test.xml")
      result shouldBe Some(mockGameState)
    }

    "return None if XML file does not exist" in {
      val result = xmlComponent.load("non_existing_file.xml")
      result shouldBe None
    }

    "return None if deserialization fails" in {
      val testFile = "games/broken.xml"
      val brokenXml = "<broken><not-elem></broken>" // Malformed

      Using(new PrintWriter(new File(testFile))) { pw =>
        pw.write(brokenXml)
      }

      when(mockDeserializer.fromXml(any())).thenThrow(new RuntimeException("boom"))

      val result = xmlComponent.load("broken.xml")
      result shouldBe None
    }
    "not throw if saving fails due to file write error" in {
      val gameState = mock[IGameState]
      when(gameState.toXml).thenReturn(<game>
        <player>Alice</player>
      </game>)

      // Break the `games` folder by making it a file
      val gamesPath = new File("games")
      gamesPath.delete()
      Using(new PrintWriter(gamesPath))(_.write("not_a_directory"))

      noException should be thrownBy {
        xmlComponent.save(gameState)
      }

      gamesPath.delete()
    }
    "create games folder if it does not exist" in {
      val folder = new File("games")
      if (folder.exists()) folder.delete()

      val dummyXml = <game><player>Alice</player></game>
      when(mockGameState.toXml).thenReturn(dummyXml)

      xmlComponent.save(mockGameState)

      folder.exists() shouldBe true
      folder.isDirectory shouldBe true
    }
    "return None if XML file is not valid XML" in {
      val testFile = "games/invalid.xml"
      val invalidContent = "<<<not xml>>>"

      Using(new PrintWriter(new File(testFile))) { pw =>
        pw.write(invalidContent)
      }

      val result = xmlComponent.load("invalid.xml")
      result shouldBe None
    }

  }
}
