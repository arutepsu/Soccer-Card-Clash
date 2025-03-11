package model.playingFiledComponent.factory

import com.google.inject.{Inject, Singleton}
import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.factory.IPlayingFieldFactory
import util.Deserializer
import play.api.libs.json.*

import scala.xml.*

@Singleton
class PlayingFieldDeserializer @Inject()() (
                                           playingFieldFactory: IPlayingFieldFactory,
                                           playerDeserializer: PlayerDeserializer
                                         ) extends Deserializer[IPlayingField] {

  override def fromXml(xml: Elem): IPlayingField = {
    println("DEBUG: Entering PlayingFieldDeserializer.fromXml")
    println(s"DEBUG: Full XML received:\n${xml.toString()}") // ✅ Print full XML before parsing

    // ✅ Use `\\ "playingField"` to search globally
    val playingFieldXml = (xml \\ "playingField").headOption
      .map(_.asInstanceOf[Elem])
      .getOrElse {
        println("❌ ERROR: <playingField> not found in XML!")
        throw new IllegalArgumentException("ERROR: Missing 'playingField' in XML.")
      }

    println(s"✅ DEBUG: Extracted playingFieldXml:\n$playingFieldXml")

    val attackerXml = (playingFieldXml \ "Attacker").headOption
      .map(_.asInstanceOf[Elem])
      .getOrElse {
        println("❌ ERROR: <Attacker> not found inside <playingField>!")
        throw new IllegalArgumentException("ERROR: Missing 'Attacker' in XML.")
      }

    val defenderXml = (playingFieldXml \ "Defender").headOption
      .map(_.asInstanceOf[Elem])
      .getOrElse {
        println("❌ ERROR: <Defender> not found inside <playingField>!")
        throw new IllegalArgumentException("ERROR: Missing 'Defender' in XML.")
      }

    // ✅ Ensure <Player> is extracted correctly from <Attacker> and <Defender>
    val player1Xml = (playingFieldXml \ "Attacker" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      println("❌ ERROR: Missing <Player> inside <Attacker> in XML!")
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Attacker> in XML.")
    }

    val player2Xml = (playingFieldXml \ "Defender" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      println("❌ ERROR: Missing <Player> inside <Defender> in XML!")
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Defender> in XML.")
    }

    println(s"✅ DEBUG: Extracted Player1 XML:\n$player1Xml")
    println(s"✅ DEBUG: Extracted Player2 XML:\n$player2Xml")

    val player1 = playerDeserializer.fromXml(player1Xml)
    val player2 = playerDeserializer.fromXml(player2Xml)


    println(s"✅ DEBUG: Extracted players - Player1: $player1, Player2: $player2")

    val playingField = playingFieldFactory.createPlayingField(player1, player2)
    println(s"✅ DEBUG: Created playingField: $playingField")

    playingField
  }


  override def fromJson(json: JsObject): IPlayingField = {
    println("DEBUG: Entering PlayingFieldDeserializer.fromJson")

    val player1Json = (json \ "attacker").as[JsObject]
    val player2Json = (json \ "defender").as[JsObject]

    println(s"DEBUG: Extracted attacker JSON: $player1Json")
    println(s"DEBUG: Extracted defender JSON: $player2Json")

    val player1 = playerDeserializer.fromJson(player1Json)
    println(s"DEBUG: Deserialized player1: $player1")

    val player2 = playerDeserializer.fromJson(player2Json)
    println(s"DEBUG: Deserialized player2: $player2")

    val playingField = playingFieldFactory.createPlayingField(player1, player2)
    println(s"DEBUG: Created playingField: $playingField")

    playingField
  }
}
