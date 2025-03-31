package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.IPlayingFieldFactory
import de.htwg.se.soccercardclash.util.Deserializer
import play.api.libs.json.*
import scala.xml.*

import scala.util.{Failure, Success, Try}


@Singleton
class PlayingFieldDeserializer @Inject()() (
  playingFieldFactory: IPlayingFieldFactory,
  playerDeserializer: PlayerDeserializer
) extends Deserializer[IPlayingField] {

  override def fromXml(xml: Elem): IPlayingField = {
    Try {
      val playingFieldXml = (xml \\ "playingField").headOption
        .map(_.asInstanceOf[Elem])
        .getOrElse {
          throw new IllegalArgumentException("ERROR: Missing 'playingField' in XML.")
        }


      val attackerXml = (playingFieldXml \ "Attacker").headOption
        .map(_.asInstanceOf[Elem])
        .getOrElse {
          throw new IllegalArgumentException("ERROR: Missing 'Attacker' in XML.")
        }

      val defenderXml = (playingFieldXml \ "Defender").headOption
        .map(_.asInstanceOf[Elem])
        .getOrElse {
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
          throw new IllegalArgumentException("ERROR: Missing 'Player' inside <Defender> in XML.")
        }


      val player1 = playerDeserializer.fromXml(player1Xml)
      val player2 = playerDeserializer.fromXml(player2Xml)


      playingFieldFactory.createPlayingField(player1, player2)
    } match {
      case Success(playingField) => playingField
      case Failure(exception) =>
        throw new RuntimeException(s"❌ Error parsing PlayingField XML: ${exception.getMessage}", exception)
    }
  }


  override def fromJson(json: JsObject): IPlayingField = {
    Try {
      val player1Json = (json \ "attacker").as[JsObject]
      val player2Json = (json \ "defender").as[JsObject]

      val player1 = playerDeserializer.fromJson(player1Json)

      val player2 = playerDeserializer.fromJson(player2Json)

      playingFieldFactory.createPlayingField(player1, player2)

    } match {
      case Success(playingField) => playingField
      case Failure(exception) =>
        throw new RuntimeException(s"❌ Error parsing PlayingField JSON: ${exception.getMessage}", exception)
    }
  }
}