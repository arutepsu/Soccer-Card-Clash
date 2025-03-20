package model.gameComponent.factory

import com.google.inject.{Inject, Singleton}
import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapActionCommands.RegularActionCommand
import model.cardComponent.ICard
import model.cardComponent.factory.{DeckFactory, IDeckFactory}
import model.fileIOComponent.IFileIO
import model.gameComponent.IGame
import model.gameComponent.factory.IGameInitializer
import model.gameComponent.factory.{GameState, GameStateFactory, IGameState, IGameStateFactory}
import model.playerComponent.IPlayer
import model.playerComponent.factory.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.dataStructure.{HandCardsQueue, IHandCardsQueueFactory}
import model.playingFiledComponent.factory.*
import model.playingFiledComponent.manager.{ActionManager, IActionManager}
import util.UndoManager
import play.api.libs.json.*

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}
import scala.xml.*

trait IGameInitializer {
  def getPlayingField: IPlayingField
  def getPlayer1: IPlayer
  def getPlayer2: IPlayer
  def getActionManager: IActionManager

  def createGame(playerName1: String, playerName2: String): Unit
  def initializeFromState(state: IGameState): Unit
  def selectDefenderPosition(): Int
}

class GameInitializer @Inject()(
                                 playerFactory: IPlayerFactory,
                                 playingFieldFactory: IPlayingFieldFactory,
                                 deckFactory: IDeckFactory
                               ) extends IGameInitializer {

  private var player1: IPlayer = _
  private var player2: IPlayer = _
  private var playingField: IPlayingField = _

  override def getPlayingField: IPlayingField = playingField
  override def getPlayer1: IPlayer = player1
  override def getPlayer2: IPlayer = player2
  override def getActionManager: IActionManager = playingField.getActionManager

  override def createGame(playerName1: String, playerName2: String): Unit = {

    val (p1, p2) = createPlayers(playerName1, playerName2)
    player1 = p1
    player2 = p2

    playingField = playingFieldFactory.createPlayingField(player1, player2)
    playingField.getRoles.setRoles(player1, player2)

    val dataManager = playingField.getDataManager
    dataManager.initializePlayerHands(player1.getCards.toList, player2.getCards.toList)

    playingField.setPlayingField()
  }

  private def createPlayers(playerName1: String, playerName2: String): (IPlayer, IPlayer) = {
    val deck = deckFactory.createDeck()
    deckFactory.shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    val p1 = playerFactory.createPlayer(playerName1, hand1)
    val p2 = playerFactory.createPlayer(playerName2, hand2)

    (p1, p2)
  }

  override def initializeFromState(state: IGameState): Unit = {

    player1 = state.player1
    player2 = state.player2

    playingField = playingFieldFactory.createPlayingField(player1, player2)

    val dataManager = playingField.getDataManager
    dataManager.initializePlayerHands(state.player1Hand.getCards.toList, state.player2Hand.getCards.toList)
    dataManager.setPlayerDefenders(player1, state.player1Defenders)
    dataManager.setPlayerDefenders(player2, state.player2Defenders)

    dataManager.setPlayerGoalkeeper(player1, state.player1Goalkeeper)
    dataManager.setPlayerGoalkeeper(player2, state.player2Goalkeeper)

    playingField.setPlayingField()
  }

  override def selectDefenderPosition(): Int = {
    if (playingField.getDataManager.allDefendersBeaten(playingField.getDefender)) -1 else -2
  }
  
}
