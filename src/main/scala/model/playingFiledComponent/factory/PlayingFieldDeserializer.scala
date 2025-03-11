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

    val playingFieldXml = (xml \\ "playingField").headOption
      .map(_.asInstanceOf[Elem])
      .getOrElse {
        println("❌ ERROR: <playingField> not found in XML!")
        throw new IllegalArgumentException("ERROR: Missing 'playingField' in XML.")
      }


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

    val player1Xml = (playingFieldXml \ "Attacker" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Attacker> in XML.")
    }

    val player2Xml = (playingFieldXml \ "Defender" \ "Player").headOption
    .map(_.asInstanceOf[Elem])
    .getOrElse {
      println("❌ ERROR: Missing <Player> inside <Defender> in XML!")
      throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Defender> in XML.")
    }


    val player1 = playerDeserializer.fromXml(player1Xml)
    val player2 = playerDeserializer.fromXml(player2Xml)


    val playingField = playingFieldFactory.createPlayingField(player1, player2)

    playingField
  }


  override def fromJson(json: JsObject): IPlayingField = {

    val player1Json = (json \ "attacker").as[JsObject]
    val player2Json = (json \ "defender").as[JsObject]

    val player1 = playerDeserializer.fromJson(player1Json)

    val player2 = playerDeserializer.fromJson(player2Json)

    val playingField = playingFieldFactory.createPlayingField(player1, player2)

    playingField
  }
}
