package model.playingFieldComponent.factory

import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.factory.IPlayingFieldFactory
import model.playerComponent.IPlayer
import model.playerComponent.factory.PlayerDeserializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import play.api.libs.json._
import model.playingFiledComponent.factory.PlayingFieldDeserializer
import scala.xml.Elem
import org.mockito.ArgumentMatchers.any

class PlayingFieldDeserializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  class TestableDeserializer(
                              playingFieldFactory: IPlayingFieldFactory,
                              playerDeserializer: PlayerDeserializer
                            ) extends PlayingFieldDeserializer()(playingFieldFactory, playerDeserializer)

  "PlayingFieldDeserializer" should {

    "deserialize from XML and return an IPlayingField using PlayerDeserializer and Factory" in {
      val xml: Elem =
        <root>
          <playingField>
            <Attacker>
              <Player name="Alice">
                <Cards></Cards>
                <ActionStates></ActionStates>
              </Player>
            </Attacker>
            <Defender>
              <Player name="Bob">
                <Cards></Cards>
                <ActionStates></ActionStates>
              </Player>
            </Defender>
          </playingField>
        </root>

      val mockPlayer1 = mock[IPlayer]
      val mockPlayer2 = mock[IPlayer]
      val mockField = mock[IPlayingField]

      val mockPlayerDeserializer = mock[PlayerDeserializer]
      val mockFieldFactory = mock[IPlayingFieldFactory]

      when(mockPlayerDeserializer.fromXml(any())).thenReturn(mockPlayer1, mockPlayer2)
      when(mockFieldFactory.createPlayingField(mockPlayer1, mockPlayer2)).thenReturn(mockField)

      val deserializer = new TestableDeserializer(mockFieldFactory, mockPlayerDeserializer)
      val result = deserializer.fromXml(xml)

      result shouldBe mockField
      verify(mockPlayerDeserializer, times(2)).fromXml(any())
      verify(mockFieldFactory).createPlayingField(mockPlayer1, mockPlayer2)
    }

    "deserialize from JSON and return an IPlayingField using PlayerDeserializer and Factory" in {
      val json = Json.obj(
        "attacker" -> Json.obj("name" -> "Alice", "cards" -> Json.arr(), "actionStates" -> Json.obj()),
        "defender" -> Json.obj("name" -> "Bob", "cards" -> Json.arr(), "actionStates" -> Json.obj())
      )

      val mockPlayer1 = mock[IPlayer]
      val mockPlayer2 = mock[IPlayer]
      val mockField = mock[IPlayingField]

      val mockPlayerDeserializer = mock[PlayerDeserializer]
      val mockFieldFactory = mock[IPlayingFieldFactory]

      when(mockPlayerDeserializer.fromJson(any())).thenReturn(mockPlayer1, mockPlayer2)
      when(mockFieldFactory.createPlayingField(mockPlayer1, mockPlayer2)).thenReturn(mockField)

      val deserializer = new TestableDeserializer(mockFieldFactory, mockPlayerDeserializer)
      val result = deserializer.fromJson(json)

      result shouldBe mockField
      verify(mockPlayerDeserializer, times(2)).fromJson(any())
      verify(mockFieldFactory).createPlayingField(mockPlayer1, mockPlayer2)
    }

    "throw a RuntimeException if XML is missing attacker or defender" in {
      val invalidXml: Elem =
        <root>
          <playingField>
            <Attacker></Attacker>
          </playingField>
        </root>

      val mockPlayerDeserializer = mock[PlayerDeserializer]
      val mockFieldFactory = mock[IPlayingFieldFactory]

      val deserializer = new TestableDeserializer(mockFieldFactory, mockPlayerDeserializer)

      val exception = intercept[RuntimeException] {
        deserializer.fromXml(invalidXml)
      }

      exception.getMessage should include("Missing 'Defender' in XML.")
    }

    "throw a RuntimeException if JSON is missing attacker or defender" in {
      val invalidJson = Json.obj("attacker" -> Json.obj("name" -> "Alice"))

      val mockPlayerDeserializer = mock[PlayerDeserializer]
      val mockFieldFactory = mock[IPlayingFieldFactory]

      val deserializer = new TestableDeserializer(mockFieldFactory, mockPlayerDeserializer)

      val exception = intercept[RuntimeException] {
        deserializer.fromJson(invalidJson)
      }

      exception.getMessage should include("❌ Error parsing PlayingField JSON")
    }
  }
}
