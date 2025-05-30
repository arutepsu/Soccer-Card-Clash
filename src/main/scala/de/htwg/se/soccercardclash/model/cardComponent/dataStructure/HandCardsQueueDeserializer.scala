package de.htwg.se.soccercardclash.model.cardComponent.dataStructure

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.util.Deserializer
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.collection.mutable
import scala.xml.*


trait IHandCardsQueueFactory {
  def create(cards: List[ICard]): IHandCardsQueue
}

@Singleton
class HandCardsQueueFactory @Inject()() extends IHandCardsQueueFactory {
  override def create(cards: List[ICard]): IHandCardsQueue = {
    HandCardsQueue(cards)
  }
}

@Singleton
class HandCardsQueueDeserializer @Inject()(
                                            cardDeserializer: CardDeserializer,
                                            handCardsQueueFactory: IHandCardsQueueFactory
                                          ) extends Deserializer[IHandCardsQueue] {

  override def fromXml(xml: Elem): IHandCardsQueue = {
    val cards = (xml \ "cards" \ "Card").map(node => cardDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    handCardsQueueFactory.create(cards)
  }

  override def fromJson(json: JsObject): IHandCardsQueue = {
    val cards = (json \ "cards").as[List[JsObject]].map(cardDeserializer.fromJson)
    handCardsQueueFactory.create(cards)
  }
}
