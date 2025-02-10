package controller
import model.playingFiledComponent.PlayingField
import util.Observable
import util.Observer
import model.playerComponent.Player
import model.cardComponent.Card
import scala.collection.mutable
import scala.io.StdIn.readLine
import java.io._
import scala.util.{Try, Success, Failure}
import model.playingFiledComponent.PlayingField
import util.{Observable, UndoManager}
import model.playerComponent.Player
import scala.collection.mutable

class Controller(player1: Player, player2: Player, pf: PlayingField) extends Observable {
  private val undoManager = new UndoManager

  def getPlayingField: PlayingField = pf
  // Getters for players
  def getPlayer1: Player = player1
  def getPlayer2: Player = player2
  def startGame(): Unit = {
    pf.initializeRoles()

    while (pf.getScorePlayer1 < 10 && pf.getScorePlayer2 < 10) {
      notifyObservers() // Update game state display

      val defenderPosition = selectDefenderPosition()
      executeAttackCommand(defenderPosition)

      if (pf.getScorePlayer1 >= 10 || pf.getScorePlayer2 >= 10) {
        endGame()
      }
    }
  }

  def initRoles(): Unit = {
    pf.initializeRoles()
  }
  def executeAttackCommand(defenderPosition: Int): Unit = {
    val command = new AttackCommand(defenderPosition, pf)
    undoManager.doStep(command)
    notifyObservers() // Update state after command execution
  }

  def executeAttackCommandDouble(defenderPosition: Int): Unit = {
    val command = new SpecialAttackCommand(defenderPosition, pf)
    undoManager.doStep(command)
    notifyObservers() // Update state after command execution
  }

  def boostCard(defenderPosition: Int): Unit = {
    println("boosst!!!!")
    val command = new BoostCardCommand(defenderPosition, pf)
    undoManager.doStep(command)
    notifyObservers()
  }
  private def endGame(): Unit = {
    val winner = if (pf.getScorePlayer1 >= 10) player1 else player2
    val winnerScore = if (pf.getScorePlayer1 >= 10) pf.getScorePlayer1 else pf.getScorePlayer2
    notifyObservers()
    println(s"${winner.name} wins with $winnerScore points!")
  }

  def selectDefenderPosition(): Int = {
    val currentDefender = pf.getDefender
    if (pf.allDefendersBeaten(currentDefender)) -1 // Attack goalkeeper
    else -2 // Signal regular defender selection
  }

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers()
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers()
  }

  def nextPhase(): Unit = {
    println("Transitioning to the next phase...")
    startGame() // Calls the existing `startGame()` method to begin the game
  }

  def saveGame(filePath: String): Try[Unit] = {
    Try {
      val oos = new ObjectOutputStream(new FileOutputStream(filePath))
      oos.writeObject(player1)
      oos.writeObject(player2)
      oos.writeObject(pf)
      oos.close()
    }
  }

  def loadGame(filePath: String): Try[Controller] = {
    Try {
      val ois = new ObjectInputStream(new FileInputStream(filePath))
      val loadedPlayer1 = ois.readObject().asInstanceOf[Player]
      val loadedPlayer2 = ois.readObject().asInstanceOf[Player]
      val loadedPf = ois.readObject().asInstanceOf[PlayingField]
      ois.close()
      new Controller(loadedPlayer1, loadedPlayer2, loadedPf)
    }
  }
}



//class Controller(player1: Player, player2: Player, pf: PlayingField) extends Observable {
//  def getPlayingField: PlayingField = pf
//  def startGame(): Unit = {
//    pf.initializeRoles()
//
//    while (pf.getScorePlayer1 < 10 && pf.getScorePlayer2 < 10) {
//      notifyObservers()  // Notify TUI to update game state display
//
//      val defenderPosition = selectDefenderPosition()
//      val attackResult = pf.attack(defenderPosition)
//
//      if (attackResult) {
//        println(s"\n${pf.getAttacker.name} succeeded in the attack!\n")
//      } else {
//        println("Defended successfully. Roles are switching.\n")
//      }
//
//      if (pf.getScorePlayer1 >= 10 || pf.getScorePlayer2 >= 10) {
//        println("Game Over!")
//        val winner = if (pf.getScorePlayer1 >= 10) player1 else player2
//        val winnerScore = if (pf.getScorePlayer1 >= 10) pf.getScorePlayer1 else pf.getScorePlayer2
//        println(s"${winner.name} wins with $winnerScore points!")
//        return
//      }
//    }
//  }
//
//  def selectDefenderPosition(): Int = {
//    val currentDefender = pf.getDefender
//
//    if (pf.allDefendersBeaten(currentDefender)) {
//      println("All defenders have been beaten! To attack the goalkeeper, type '0'.")
//      val input = readLine()
//      if (input == "0") {
//        println("You chose to attack the goalkeeper.")
//        return -1
//      } else {
//        println("Invalid input. Please try again.")
//        return selectDefenderPosition()
//      }
//    } else {
//      val defenders = pf.playerDefenders(currentDefender)
//      val defenderCount = defenders.size
//
//      println(s"There are $defenderCount defender(s) available to attack:")
//      for (i <- defenders.indices) {
//        println(s"${i + 1}: ${defenders(i)}")
//      }
//
//      var selectedPosition = 0
//      while (selectedPosition < 1 || selectedPosition > defenderCount) {
//        println(s"Select a defender position to attack (1-$defenderCount): ")
//        selectedPosition = readLine().toIntOption.getOrElse(0)
//        if (selectedPosition < 1 || selectedPosition > defenderCount) {
//          println(s"Invalid input. Please enter a position from 1 to $defenderCount.")
//        }
//      }
//      selectedPosition - 1
//    }
//  }
//}