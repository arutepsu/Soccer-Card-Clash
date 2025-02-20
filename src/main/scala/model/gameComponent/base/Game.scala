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

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.util.Try

class Game extends IGame {
  private var player1: IPlayer = _
  private var player2: IPlayer = _
  private var playingField: IPlayingField = _
  private var actionManager: ActionManager = _

  override def getPlayingField: IPlayingField = playingField
  override def getPlayer1: IPlayer = player1
  override def getPlayer2: IPlayer = player2
  def getGameManager: ActionManager = actionManager


  private def createPlayers(playerName1: String, playerName2: String): (IPlayer, IPlayer) = {
    val deck = DeckFactory.createDeck()

    DeckFactory.shuffleDeck(deck)

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    val player1 = PlayerFactory.createPlayer(playerName1, hand1.toList)
    val player2 = PlayerFactory.createPlayer(playerName2, hand2.toList)

    (player1, player2)
  }


  override def startGame(playerName1: String, playerName2: String): Unit = {

    val (p1, p2) = createPlayers(playerName1, playerName2)
    player1 = p1
    player2 = p2

    playingField = new PlayingField(player1, player2)

    playingField.getDataManager.initializePlayerHands(player1.getCards, player2.getCards)

    playingField.setPlayingField()

    actionManager = new ActionManager(playingField)
  }


  override def selectDefenderPosition(): Int =
    if (playingField.getDataManager.allDefendersBeaten(playingField.getDefender)) -1 else -2

  override def saveGame(): Unit = {
    val jsonString = Json.prettyPrint(this.toJson)
    Files.write(Paths.get("game_save.json"), jsonString.getBytes(StandardCharsets.UTF_8))
  }

  override def loadGame(): Unit = {
    val jsonString = new String(Files.readAllBytes(Paths.get("game_save.json")), StandardCharsets.UTF_8)
    val gameJson = Json.parse(jsonString)

    player1 = PlayerFactory.loadPlayerFromJson((gameJson \ "player1").get)
    player2 = PlayerFactory.loadPlayerFromJson((gameJson \ "player2").get)
    playingField = PlayingField.loadFromJson((gameJson \ "playingField").get)
    actionManager = ActionManager.loadFromJson((gameJson \ "actions").get)
  }

  override def toJson: JsObject = super.toJson

  override def toXml: Elem = super.toXml
}