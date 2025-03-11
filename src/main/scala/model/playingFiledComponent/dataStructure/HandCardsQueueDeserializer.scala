package model.playingFiledComponent.dataStructure

import util.Deserializer
import model.cardComponent.CardDeserializer
import scala.xml.*
import play.api.libs.json.*
import javax.inject.{Singleton, Inject}
import scala.collection.mutable
import model.cardComponent.ICard

@Singleton
class HandCardsQueueDeserializer @Inject() (cardDeserializer: CardDeserializer) extends Deserializer[IHandCardsQueue] {

  override def fromXml(xml: Elem): IHandCardsQueue = {

    val cards = (xml \ "cards" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList

    val handCardsQueue = HandCardsQueueFactory.create(cards)

    handCardsQueue
  }


  override def fromJson(json: JsObject): IHandCardsQueue = {

    val cards = (json \ "cards").as[List[JsObject]].map(cardDeserializer.fromJson)

    val handCardsQueue = HandCardsQueueFactory.create(cards)

    handCardsQueue
  }
}

object HandCardsQueueFactory {
  def create(cards: List[ICard]): IHandCardsQueue = {
    new HandCardsQueue(cards)
  }
}
