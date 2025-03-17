package model.playingFiledComponent.dataStructure

import util.Deserializer
import scala.xml.*
import play.api.libs.json.*
import javax.inject.{Singleton, Inject}
import scala.collection.mutable
import model.cardComponent.ICard
import model.cardComponent.factory.CardDeserializer

trait IHandCardsQueueFactory {
  def create(cards: List[ICard]): IHandCardsQueue
}

@Singleton
class HandCardsQueueFactory @Inject()() extends IHandCardsQueueFactory {
  override def create(cards: List[ICard]): IHandCardsQueue = {
    new HandCardsQueue(cards)
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
