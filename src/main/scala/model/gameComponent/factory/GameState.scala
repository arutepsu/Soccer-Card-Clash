package model.gameComponent.factory
//import model.playingFiledComponent.{IPlayingField, PlayingFieldDeserializer}
//import model.cardComponent.{CardDeserializer, ICard}
import controller.command.memento.base.Memento
import model.cardComponent.CardDeserializer
import model.gameComponent.IGame
import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.PlayingFieldDeserializer
//import model.playerComponent.factory.PlayerDeserializer
import util.{Deserializer, Serializable}
import model.playingFiledComponent.dataStructure.{HandCardsQueueDeserializer, IHandCardsQueue}
import play.api.libs.json.*
import model.playerComponent.IPlayer
import scala.xml.*
import model.playingFiledComponent.dataStructure.*
import model.cardComponent.ICard
import model.playingFiledComponent.IPlayingField
import javax.inject.Singleton
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
  def saveState(): Memento
  def restoreState(memento: Memento): IGameState

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

  /** Save current game state into a Memento */
  override def saveState(): Memento = Memento(
    attacker = player1,
    defender = player2,
    player1Defenders = player1Defenders,
    player2Defenders = player2Defenders,
    player1Goalkeeper = player1Goalkeeper,
    player2Goalkeeper = player2Goalkeeper,
    player1Hand = player1Hand.getCards.toList,
    player2Hand = player2Hand.getCards.toList,
    player1Score = player1Score,
    player2Score = player2Score,
    player1Actions = Map.empty,
    player2Actions = Map.empty
  )

  /** Restore game state from a Memento (returns a new GameState) */
  override def restoreState(memento: Memento): IGameState =
    new GameState(
      playingField,
      memento.attacker,
      memento.defender,
      new HandCardsQueue(memento.player1Hand),
      new HandCardsQueue(memento.player2Hand),
      memento.player1Defenders,
      memento.player2Defenders,
      memento.player1Goalkeeper,
      memento.player2Goalkeeper,
      memento.player1Score,
      memento.player2Score
    )

  override def toXml: Elem = {
    <GameState player1Score={player1Score.toString} player2Score={player2Score.toString}>
      <PlayingField>{playingField.toXml}</PlayingField>
      <Player1>{player1.toXml}</Player1>
      <Player2>{player2.toXml}</Player2>
      <Player1Hand>{player1Hand.getCards.map(_.toXml)}</Player1Hand>
      <Player2Hand>{player2Hand.getCards.map(_.toXml)}</Player2Hand>
      <Player1Field>{player1Defenders.map(_.toXml)}</Player1Field>
      <Player2Field>{player2Defenders.map(_.toXml)}</Player2Field>
      {player1Goalkeeper.map(gk => <Player1Goalkeeper>{gk.toXml}</Player1Goalkeeper>).getOrElse(Nil)}
      {player2Goalkeeper.map(gk => <Player2Goalkeeper>{gk.toXml}</Player2Goalkeeper>).getOrElse(Nil)}
    </GameState>
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

  def createFromMemento(memento: Memento, playingField: IPlayingField): IGameState
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
                     ): IGameState =
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

  override def createFromMemento(memento: Memento, playingField: IPlayingField): IGameState =
    new GameState(
      playingField,
      memento.attacker,
      memento.defender,
      new HandCardsQueue(memento.player1Hand),
      new HandCardsQueue(memento.player2Hand),
      memento.player1Defenders,
      memento.player2Defenders,
      memento.player1Goalkeeper,
      memento.player2Goalkeeper,
      memento.player1Score,
      memento.player2Score
    )
}

