package model.playingFiledComponent

import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.factory.IPlayingFieldFactory
import util.Deserializer
import scala.xml.*
import play.api.libs.json.*
import com.google.inject.{Singleton, Inject}

@Singleton
class PlayingFieldDeserializer @Inject()() (
                                           playingFieldFactory: IPlayingFieldFactory,
                                           playerDeserializer: PlayerDeserializer
                                         ) extends Deserializer[IPlayingField] {

  override def fromXml(xml: Elem): IPlayingField = {
    println("DEBUG: Entering PlayingFieldDeserializer.fromXml")

    val player1 = playerDeserializer.fromXml((xml \ "attacker").head.asInstanceOf[Elem])
    val player2 = playerDeserializer.fromXml((xml \ "defender").head.asInstanceOf[Elem])

    println(s"DEBUG: Extracted players - Player1: $player1, Player2: $player2")

    val playingField = playingFieldFactory.createPlayingField(player1, player2)
    println(s"DEBUG: Created playingField: $playingField")

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
