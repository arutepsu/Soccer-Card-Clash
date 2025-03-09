package model.playingFiledComponent

import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.factory.IPlayingFieldFactory
import util.Deserializer
import scala.xml.*
import play.api.libs.json.*
import com.google.inject.Singleton

@Singleton
object PlayingFieldDeserializer extends Deserializer[IPlayingField] {

  // Summon the IPlayingFieldFactory instance (ensuring it exists in the given scope)
  private given playingFieldFactory: IPlayingFieldFactory = summon[IPlayingFieldFactory]

  override def fromXml(xml: Elem): IPlayingField = {
    val player1 = PlayerDeserializer.fromXml((xml \ "Player1").head.asInstanceOf[Elem])
    val player2 = PlayerDeserializer.fromXml((xml \ "Player2").head.asInstanceOf[Elem])

    playingFieldFactory.createPlayingField(player1, player2) // ✅ Uses summoned factory
  }

  override def fromJson(json: JsObject): IPlayingField = {
    val player1 = PlayerDeserializer.fromJson((json \ "player1").as[JsObject])
    val player2 = PlayerDeserializer.fromJson((json \ "player2").as[JsObject])

    playingFieldFactory.createPlayingField(player1, player2) // ✅ Uses summoned factory
  }
}
