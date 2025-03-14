package controller.base
import controller.command.ICommand
import controller.{Events, IController}
import model.cardComponent.factory.DeckFactory
import model.gameComponent.IGame
import model.gameComponent.base.Game
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import util.{Observer, UndoManager}

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable
import scala.util.Try
import com.google.inject.Inject
import controller.command.factory.ICommandFactory
import model.playingFiledComponent.manager.base.ActionManager

class Controller @Inject() (private val game: IGame, private val commandFactory: ICommandFactory) extends IController{
  private val undoManager = new UndoManager

  def getPlayingField: IPlayingField = {
    val field = game.getPlayingField
//    println(s"!!!!!!!!!!!!Playing Field: ${field.getAttacker}, ${field.getDefender}")
    field
  }

  def getPlayer1: IPlayer = {
    val player1 = game.getPlayer1
//    println(s"!!!!!!!!!!!!!!Player 1: ${player1.name}")
    player1
  }

  def getPlayer2: IPlayer = {
    val player2 = game.getPlayer2
//    println(s"!!!!!!!!!!!!Player 2: ${player2.name}")
    player2
  }

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers(Events.Undo)
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers(Events.Redo)
  }

  private def executeCommand(command: ICommand, event: Events): Unit = {
    undoManager.doStep(command)
    notifyObservers(event)
  }
//  def selectDefenderPosition(): Int = game.selectDefenderPosition()
  def executeSingleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createSingleAttackCommand(defenderPosition), Events.RegularAttack)
  }

  def executeDoubleAttackCommand(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createDoubleAttackCommand(defenderPosition), Events.DoubleAttack)
  }

  def boostDefender(defenderPosition: Int): Unit = {
    executeCommand(commandFactory.createBoostDefenderCommand(defenderPosition), Events.BoostDefender)
  }

  def boostGoalkeeper(): Unit = {
    executeCommand(commandFactory.createBoostGoalkeeperCommand(), Events.BoostGoalkeeper)
  }

  def regularSwap(index: Int): Unit = {
    executeCommand(commandFactory.createRegularSwapCommand(index), Events.RegularSwap)
  }

  def circularSwap(index: Int): Unit = {
    executeCommand(commandFactory.createCircularSwapCommand(index), Events.CircularSwap)
  }

  def startGame(player1: String, player2: String): Unit =
    executeCommand(commandFactory.createStartGameCommand(game, player1, player2), Events.PlayingField)

  def quit(): Unit =
    executeCommand(commandFactory.createQuitCommand(game), Events.Quit)

  def saveGame(): Unit =
    executeCommand(commandFactory.createSaveGameCommand(), Events.SaveGame)

  def loadGame(fileName: String): Unit = {
    executeCommand(commandFactory.createLoadGameCommand(fileName), Events.PlayingField)
  }

  override def reset(): Unit = {
    println("🔄 Resetting game controller...")

    game.reset()

    println("✅ Game controller reset completed!")
  }


}