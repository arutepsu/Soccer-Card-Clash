package de.htwg.se.soccercardclash.model.gameComponent.factory

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.factory.IGameInitializer
import de.htwg.se.soccercardclash.model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.base.PlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{ActionManager, IActionManager}
import de.htwg.se.soccercardclash.util.UndoManager
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
