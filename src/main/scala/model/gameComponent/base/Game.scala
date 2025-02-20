package model.gameComponent.base

import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.HandSwapCommand
import model.cardComponent.cardFactory.DeckFactory
import model.gameComponent.IGame
import model.playerComponent.IPlayer
import model.playerComponent.playerFactory.PlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.manager.ActionManager
import util.UndoManager
import play.api.libs.json._
import scala.xml._

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.util.Try

import com.google.inject.{Inject, Singleton}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import play.api.libs.json.{JsObject, Json}
import model.playerComponent.factories.IPlayerFactory
import model.playingFiledComponent.factories._
@Singleton
class Game @Inject() (
                       playerFactory: IPlayerFactory,
                       playingFieldFactory: IPlayingFieldFactory,
                       actionManagerFactory: IActionManagerFactory
                     ) extends IGame {

  private var player1: IPlayer = _
  private var player2: IPlayer = _
  private var playingField: IPlayingField = _
  private var actionManager: ActionManager = _

  override def getPlayingField: IPlayingField = playingField
  override def getPlayer1: IPlayer = player1
  override def getPlayer2: IPlayer = player2
  override def getActionManager: ActionManager = actionManager

  /** ✅ Creates Players using Injected Factory */
  private def createPlayers(playerName1: String, playerName2: String): (IPlayer, IPlayer) = {
    val deck = DeckFactory.createDeck()
    DeckFactory.shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    val p1 = playerFactory.createPlayer(playerName1, hand1)
    val p2 = playerFactory.createPlayer(playerName2, hand2)

    (p1, p2)
  }

  /** ✅ Starts the Game with Injected Dependencies */
  override def startGame(playerName1: String, playerName2: String): Unit = {
    val (p1, p2) = createPlayers(playerName1, playerName2)
    player1 = p1
    player2 = p2

    playingField = playingFieldFactory.createPlayingField(player1, player2)
    playingField.getDataManager.initializePlayerHands(player1.getCards, player2.getCards)
    playingField.setPlayingField()

    actionManager = actionManagerFactory.createActionManager(playingField)
  }

  override def selectDefenderPosition(): Int = {
    if (playingField.getDataManager.allDefendersBeaten(playingField.getDefender)) -1 else -2
  }

  override def saveGame(): Unit = {

  }

  override def loadGame(): Unit = {
    val jsonString = new String(Files.readAllBytes(Paths.get("game_save.json")), StandardCharsets.UTF_8)
    val gameJson = Json.parse(jsonString)
//
//    player1 = playerFactory.loadPlayerFromJson((gameJson \ "player1").get)
//    player2 = playerFactory.loadPlayerFromJson((gameJson \ "player2").get)
//    playingField = playingFieldFactory.loadFromJson((gameJson \ "playingField").get)
//    actionManager = actionManagerFactory.loadFromJson((gameJson \ "actions").get)
  }
  
}
