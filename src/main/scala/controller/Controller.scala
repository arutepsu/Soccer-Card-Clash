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
import model.cardComponent.Deck

class Controller extends Observable {
  private val undoManager = new UndoManager

  private var player1: Player = _
  private var player2: Player = _
  private var pf: PlayingField = _

  def getPlayingField: PlayingField = pf
  def getPlayer1: Player = player1
  def getPlayer2: Player = player2

  def setPlayerName(playerIndex: Int, name: String): Unit = {
    if (playerIndex == 1) {
      getPlayingField.getAttacker.setName(name)
      println(s"✅ Player 1 name set to: $name")
    } else if (playerIndex == 2) {
      getPlayingField.getDefender.setName(name)
      println(s"✅ Player 2 name set to: $name")
    } else {
      println("❌ Invalid player index! Use 1 or 2.")
    }
    notifyObservers() // ✅ Notify UI of the change
  }

  def startGame(): Unit = {
    println("🎲 Starting new game...")

    // ✅ Deal cards and initialize players
    val (p1, p2) = dealCards()
    player1 = p1
    player2 = p2

    // ✅ Convert hands to queues
    val player1HandQueue = player1.getCards.to(mutable.Queue)
    val player2HandQueue = player2.getCards.to(mutable.Queue)

    // ✅ Initialize playing field
    pf = new PlayingField(player1, player1HandQueue, player2, player2HandQueue)
    pf.setPlayingField()

    println(s"✅ Game initialized with ${player1.name} and ${player2.name}")
    notifyObservers()
  }

  private def dealCards(): (Player, Player) = {
    val deck = Deck.createDeck()
    Deck.shuffleDeck(deck)

    val hand1 = for (_ <- 1 to 26) yield deck.dequeue()
    val hand2 = for (_ <- 1 to 26) yield deck.dequeue()

    val p1 = Player("Player 1", hand1.toList)
    val p2 = Player("Player 2", hand2.toList)

    (p1, p2)
  }

  def executeAttackCommand(defenderPosition: Int): Unit = {
    val command = new AttackCommand(defenderPosition, pf)
    undoManager.doStep(command)
    notifyObservers()
  }

  def executeAttackCommandDouble(defenderPosition: Int): Unit = {
    val command = new SpecialAttackCommand(defenderPosition, pf)
    undoManager.doStep(command)
    notifyObservers()
  }

  def boostDefender(defenderPosition: Int): Unit = {
    val defenders = pf.playerDefenders(pf.getAttacker)

//    if (defenderPosition < 0 || defenderPosition >= defenders.size) {
//      println(s"⚠️ Boost prevented! Invalid defender index: $defenderPosition")
//      return
//    }

    val selectedDefender = defenders(defenderPosition)
//    if (selectedDefender.wasBoosted) {
//      println(s"⚠️ Boost prevented! Defender ${selectedDefender} has already been boosted.")
//      return
//    }

    println("✅ Boosting defender!")
    val command = new BoostDefenderCommand(defenderPosition, pf)
    undoManager.doStep(command)
    notifyObservers()
  }

  def boostGoalkeeper(): Unit = {
    pf.getGoalkeeper(pf.getAttacker) match {
      case Some(goalkeeper) =>
        if (goalkeeper.wasBoosted) {
          println(s"⚠️ Boost prevented! Goalkeeper ${goalkeeper} has already been boosted.")
          return
        }

        println("✅ Boosting goalkeeper!")
        val command = new BoostGoalkeeperCommand(pf)
        undoManager.doStep(command)
        notifyObservers()

      case None =>
        println("⚠️ No goalkeeper available to boost!")
    }
  }

  def swapAttackerCard(index: Int): Unit = {
    println(s"🔄 Swapping attacker card at index: $index")
    val command = new SwapCommand(index, pf)
    undoManager.doStep(command)
    notifyObservers()
  }

  private def endGame(): Unit = {
    val winner = if (pf.getScorePlayer1 >= 10) player1 else player2
    val winnerScore = if (pf.getScorePlayer1 >= 10) pf.getScorePlayer1 else pf.getScorePlayer2
    notifyObservers()
    println(s"🏆 ${winner.name} wins with $winnerScore points!")
  }

  def selectDefenderPosition(): Int = {
    val currentDefender = pf.getDefender
    if (pf.allDefendersBeaten(currentDefender)) -1
    else -2
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
    startGame()
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
      val controller = new Controller()
      controller.player1 = loadedPlayer1
      controller.player2 = loadedPlayer2
      controller.pf = loadedPf
      controller
    }
  }
}


//class Controller(player1: Player, player2: Player, pf: PlayingField) extends Observable {
//  private val undoManager = new UndoManager
//
//  def getPlayingField: PlayingField = pf
//  // Getters for players
//  def getPlayer1: Player = player1
//  def getPlayer2: Player = player2
//
//  def getPlayerHand(player: Player): List[Card] = {
//    pf.getHand(player).toList // ✅ Convert the hand queue to a List
//  }
//
//  def startGame(): Unit = {
//    pf.initializeRoles()
//
//    while (pf.getScorePlayer1 < 10 && pf.getScorePlayer2 < 10) {
//      notifyObservers() // Update game state display
//
//      val defenderPosition = selectDefenderPosition()
//      executeAttackCommand(defenderPosition)
//
//      if (pf.getScorePlayer1 >= 10 || pf.getScorePlayer2 >= 10) {
//        endGame()
//      }
//    }
//  }
//
//  def initRoles(): Unit = {
//    pf.initializeRoles()
//  }
//  def executeAttackCommand(defenderPosition: Int): Unit = {
//    val command = new AttackCommand(defenderPosition, pf)
//    undoManager.doStep(command)
//    notifyObservers() // Update state after command execution
//  }
//
//  def executeAttackCommandDouble(defenderPosition: Int): Unit = {
//    val command = new SpecialAttackCommand(defenderPosition, pf)
//    undoManager.doStep(command)
//    notifyObservers() // Update state after command execution
//  }
//
//  def boostDefender(defenderPosition: Int): Unit = {
//    val defenders = pf.playerDefenders(pf.getAttacker)
//
//    // ✅ Prevent out-of-range index
//    if (defenderPosition < 0 || defenderPosition >= defenders.size) {
//      println(s"⚠️ Boost prevented! Invalid defender index: $defenderPosition")
//      return
//    }
//
//    val selectedDefender = defenders(defenderPosition)
//
//    // ✅ Prevent multiple boosts
//    if (selectedDefender.wasBoosted) {
//      println(s"⚠️ Boost prevented! Defender ${selectedDefender} has already been boosted.")
//      return
//    }
//
//    println("✅ Boosting defender!")
//    val command = new BoostDefenderCommand(defenderPosition, pf)
//    undoManager.doStep(command)
//    notifyObservers()
//  }
//
//  def boostGoalkeeper(): Unit = {
//    val goalkeeperOpt = pf.getGoalkeeper(pf.getAttacker)
//
//    goalkeeperOpt match {
//      case Some(goalkeeper) =>
//        // ✅ Prevent multiple boosts
//        if (goalkeeper.wasBoosted) {
//          println(s"⚠️ Boost prevented! Goalkeeper ${goalkeeper} has already been boosted.")
//          return
//        }
//
//        println("✅ Boosting goalkeeper!")
//        val command = new BoostGoalkeeperCommand(pf)
//        undoManager.doStep(command)
//        notifyObservers()
//
//      case None =>
//        println("⚠️ No goalkeeper available to boost!")
//    }
//  }
//
//  def swapAttackerCard(index: Int): Unit = {
//    println(s"🔄 Swapping attacker card at index: $index")
//    val command = new SwapCommand(index, pf)
//    undoManager.doStep(command)
//    notifyObservers()
//  }
//
//
//  private def endGame(): Unit = {
//    val winner = if (pf.getScorePlayer1 >= 10) player1 else player2
//    val winnerScore = if (pf.getScorePlayer1 >= 10) pf.getScorePlayer1 else pf.getScorePlayer2
//    notifyObservers()
//    println(s"${winner.name} wins with $winnerScore points!")
//  }
//
//  def selectDefenderPosition(): Int = {
//    val currentDefender = pf.getDefender
//    if (pf.allDefendersBeaten(currentDefender)) -1 // Attack goalkeeper
//    else -2 // Signal regular defender selection
//  }
//
//  def undo(): Unit = {
//    undoManager.undoStep()
//    notifyObservers()
//  }
//
//  def redo(): Unit = {
//    undoManager.redoStep()
//    notifyObservers()
//  }
//
//  def nextPhase(): Unit = {
//    println("Transitioning to the next phase...")
//    startGame() // Calls the existing `startGame()` method to begin the game
//  }
//
//  def saveGame(filePath: String): Try[Unit] = {
//    Try {
//      val oos = new ObjectOutputStream(new FileOutputStream(filePath))
//      oos.writeObject(player1)
//      oos.writeObject(player2)
//      oos.writeObject(pf)
//      oos.close()
//    }
//  }
//
//  def loadGame(filePath: String): Try[Controller] = {
//    Try {
//      val ois = new ObjectInputStream(new FileInputStream(filePath))
//      val loadedPlayer1 = ois.readObject().asInstanceOf[Player]
//      val loadedPlayer2 = ois.readObject().asInstanceOf[Player]
//      val loadedPf = ois.readObject().asInstanceOf[PlayingField]
//      ois.close()
//      new Controller(loadedPlayer1, loadedPlayer2, loadedPf)
//    }
//  }
//}
//


