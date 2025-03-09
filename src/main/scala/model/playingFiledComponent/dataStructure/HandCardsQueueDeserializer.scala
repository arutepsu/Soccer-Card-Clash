package model.playingFiledComponent.dataStructure

import util.Deserializer
import model.cardComponent.CardDeserializer
import scala.xml.*
import play.api.libs.json.*
import com.google.inject.Singleton
import scala.collection.mutable


@Singleton
object HandCardsQueueDeserializer extends Deserializer[IHandCardsQueue] {

  override def fromXml(xml: Elem): IHandCardsQueue = {
    val cards = (xml \ "Card").map(node => CardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    new HandCardsQueue(cards)
  }

  override def fromJson(json: JsObject): IHandCardsQueue = {
    val cards = (json \ "cards").as[List[JsObject]].map(CardDeserializer.fromJson)
    new HandCardsQueue(cards)
  }
}

