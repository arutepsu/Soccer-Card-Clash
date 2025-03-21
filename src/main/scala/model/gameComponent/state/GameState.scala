package model.gameComponent.state

import controller.command.memento.base.Memento
import model.cardComponent.ICard
import model.cardComponent.factory.CardDeserializer
import model.gameComponent.IGame
import model.playerComponent.IPlayer
import model.playerComponent.factory.PlayerDeserializer
import model.playerComponent.playerAction.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure.*
import model.playingFiledComponent.factory.PlayingFieldDeserializer
import util.{Deserializer, Serializable}
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.xml.*

trait IGameState extends Serializable {
  def playingField: IPlayingField

  def player1: IPlayer

  def player2: IPlayer

  def player1Hand: IHandCardsQueue

  def player2Hand: IHandCardsQueue

  def player1Defenders: List[ICard]

  def player2Defenders: List[ICard]

  def player1Goalkeeper: Option[ICard]

  def player2Goalkeeper: Option[ICard]

  def player1Score: Int

  def player2Score: Int

  def toXml: Elem

  def toJson: JsObject
}

trait IGameStateFactory {
  def create(
              playingField: IPlayingField,
              player1: IPlayer,
              player2: IPlayer,
              player1Hand: IHandCardsQueue,
              player2Hand: IHandCardsQueue,
              player1Field: List[ICard],
              player2Field: List[ICard],
              player1Goalkeeper: Option[ICard],
              player2Goalkeeper: Option[ICard],
              player1Score: Int,
              player2Score: Int
            ): IGameState
}

class GameState(
                 override val playingField: IPlayingField,
                 private val memento: Memento,
                 handCardsQueueFactory: IHandCardsQueueFactory
               ) extends IGameState {

  override val player1: IPlayer = memento.attacker
  override val player2: IPlayer = memento.defender

  override val player1Hand: IHandCardsQueue = handCardsQueueFactory.create(memento.player1Hand)
  override val player2Hand: IHandCardsQueue = handCardsQueueFactory.create(memento.player2Hand)

  override val player1Defenders: List[ICard] = memento.player1Defenders
  override val player2Defenders: List[ICard] = memento.player2Defenders

  override val player1Goalkeeper: Option[ICard] = memento.player1Goalkeeper
  override val player2Goalkeeper: Option[ICard] = memento.player2Goalkeeper

  override val player1Score: Int = memento.player1Score
  override val player2Score: Int = memento.player2Score

  override def toXml: Elem = {
    <root>
      {playingField.toXml}
      <player1Hand>{player1Hand.getCards.map(_.toXml)}</player1Hand>
      <player2Hand>{player2Hand.getCards.map(_.toXml)}</player2Hand>
      <player1Field>{player1Defenders.map(_.toXml)}</player1Field>
      <player2Field>{player2Defenders.map(_.toXml)}</player2Field>
      <player1Goalkeeper>{player1Goalkeeper.map(_.toXml).getOrElse(<empty/>)}</player1Goalkeeper>
      <player2Goalkeeper>{player2Goalkeeper.map(_.toXml).getOrElse(<empty/>)}</player2Goalkeeper>
      <player1Score>{player1Score}</player1Score>
      <player2Score>{player2Score}</player2Score>
    </root>
  }

  override def toJson: JsObject = Json.obj(
    "playingField" -> playingField.toJson,
    "player1Hand" -> player1Hand.getCards.map(_.toJson),
    "player2Hand" -> player2Hand.getCards.map(_.toJson),
    "player1Field" -> player1Defenders.map(_.toJson),
    "player2Field" -> player2Defenders.map(_.toJson),
    "player1Goalkeeper" -> player1Goalkeeper.map(_.toJson).getOrElse(Json.obj()),
    "player2Goalkeeper" -> player2Goalkeeper.map(_.toJson).getOrElse(Json.obj()),
    "player1Score" -> player1Score,
    "player2Score" -> player2Score
  )
}
class GameStateFactory @Inject()(handCardsQueueFactory: IHandCardsQueueFactory) extends IGameStateFactory {

  override def create(
                       playingField: IPlayingField,
                       player1: IPlayer,
                       player2: IPlayer,
                       player1Hand: IHandCardsQueue,
                       player2Hand: IHandCardsQueue,
                       player1Field: List[ICard],
                       player2Field: List[ICard],
                       player1Goalkeeper: Option[ICard],
                       player2Goalkeeper: Option[ICard],
                       player1Score: Int,
                       player2Score: Int
                     ): IGameState = {

    val memento = Memento(
      player1,
      player2,
      player1Field,
      player2Field,
      player1Goalkeeper,
      player2Goalkeeper,
      player1Hand.toList,
      player2Hand.toList,
      player1Score,
      player2Score,
      player1.getActionStates.map {
        case (p, CanPerformAction(u)) => p -> u
        case (p, OutOfActions)        => p -> 0
      },
      player2.getActionStates.map {
        case (p, CanPerformAction(u)) => p -> u
        case (p, OutOfActions)        => p -> 0
      }
    )

    new GameState(playingField, memento, handCardsQueueFactory)
  }
}
