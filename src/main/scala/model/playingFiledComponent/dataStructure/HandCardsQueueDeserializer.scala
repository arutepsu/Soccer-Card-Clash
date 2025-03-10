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
    println("DEBUG: Entering HandCardsQueueDeserializer.fromXml")

    val cards = (xml \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    println(s"DEBUG: Extracted hand cards: $cards")

    val handCardsQueue = HandCardsQueueFactory.create(cards)
    println(s"DEBUG: Created HandCardsQueue: $handCardsQueue")

    handCardsQueue
  }

  override def fromJson(json: JsObject): IHandCardsQueue = {
    println("DEBUG: Entering HandCardsQueueDeserializer.fromJson")

    val cards = (json \ "cards").as[List[JsObject]].map(cardDeserializer.fromJson)
    println(s"DEBUG: Extracted hand cards: $cards")

    val handCardsQueue = HandCardsQueueFactory.create(cards)
    println(s"DEBUG: Created HandCardsQueue: $handCardsQueue")

    handCardsQueue
  }
}

object HandCardsQueueFactory {
  def create(cards: List[ICard]): IHandCardsQueue = {
    new HandCardsQueue(cards)  // Adjust this based on your class structure
  }
}
