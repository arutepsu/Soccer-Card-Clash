package model.gameComponent.factory

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

class GameState(
                 override val playingField: IPlayingField,
                 override val player1: IPlayer,
                 override val player2: IPlayer,
                 override val player1Hand: IHandCardsQueue,
                 override val player2Hand: IHandCardsQueue,
                 override val player1Defenders: List[ICard],
                 override val player2Defenders: List[ICard],
                 override val player1Goalkeeper: Option[ICard],
                 override val player2Goalkeeper: Option[ICard],
                 override val player1Score: Int,
                 override val player2Score: Int
               ) extends IGameState {


  override def toXml: Elem = {
    <root>
      {playingField.toXml}<player1Hand>
      {player1Hand.getCards.map(_.toXml)}
    </player1Hand>
      <player2Hand>
        {player2Hand.getCards.map(_.toXml)}
      </player2Hand>
      <player1Field>
        {player1Defenders.map(_.toXml)}
      </player1Field>
      <player2Field>
        {player2Defenders.map(_.toXml)}
      </player2Field>
      <player1Goalkeeper>
        {player1Goalkeeper.map(_.toXml).getOrElse(<empty/>)}
      </player1Goalkeeper>
      <player2Goalkeeper>
        {player2Goalkeeper.map(_.toXml).getOrElse(<empty/>)}
      </player2Goalkeeper>
      <player1Score>
        {player1Score}
      </player1Score>
      <player2Score>
        {player2Score}
      </player2Score>
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

@Singleton
class GameStateFactory extends IGameStateFactory {

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

    new GameState(
      playingField,
      player1,
      player2,
      player1Hand,
      player2Hand,
      player1Field,
      player2Field,
      player1Goalkeeper,
      player2Goalkeeper,
      player1Score,
      player2Score
    )
  }
}

trait IMementoFactory {
  def createMemento(gameState: IGameState): Memento

  def restoreFromMemento(memento: Memento, playingField: IPlayingField): IGameState
}

@Singleton
class MementoFactory @Inject()(handCardsQueueFactory: IHandCardsQueueFactory) extends IMementoFactory {

  override def createMemento(gameState: IGameState): Memento = {
    Memento(
      gameState.player1,
      gameState.player2,
      gameState.player1Defenders,
      gameState.player2Defenders,
      gameState.player1Goalkeeper,
      gameState.player2Goalkeeper,
      gameState.player1Hand.toList,
      gameState.player2Hand.toList,
      gameState.player1Score,
      gameState.player2Score,
      gameState.player1.getActionStates.map {
        case (policy, CanPerformAction(remainingUses)) => policy -> remainingUses
        case (policy, OutOfActions) => policy -> 0
      },
      gameState.player2.getActionStates.map {
        case (policy, CanPerformAction(remainingUses)) => policy -> remainingUses
        case (policy, OutOfActions) => policy -> 0
      }
    )
  }

  override def restoreFromMemento(memento: Memento, playingField: IPlayingField): IGameState = {
    new GameState(
      playingField,
      memento.attacker,
      memento.defender,
      handCardsQueueFactory.create(memento.player1Hand),
      handCardsQueueFactory.create(memento.player2Hand),
      memento.player1Defenders,
      memento.player2Defenders,
      memento.player1Goalkeeper,
      memento.player2Goalkeeper,
      memento.player1Score,
      memento.player2Score
    )
  }
}

