import model.cardComponent.Card
import model.playerComponent.Player
import model.cardComponent.Deck
import scalafx.stage.Stage
import model.cardComponent.Value.{Ace, Eight, Five, Four, Jack, King, Nine, Queen, Seven, Six, Ten, Three, Two, Value}
import model.cardComponent.Suit.{Hearts, Diamonds, Spades, Clubs}
import view.scenes.MainMenuScene
import scala.collection.mutable
//object Main {
//  def main(args: Array[String]): Unit = {
//    startGame()
//  }
//}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import view.Gui
import view.Tui
import model.cardComponent.Deck
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scala.io.StdIn.readLine
import scala.collection.mutable
import scala.util.Try
import controller.Controller
//object Main extends JFXApp3 {
//
//  override def start(): Unit = {
//    stage = new PrimaryStage { // Stage is set here
//      title = "Soccer Card Game"
//    }
//
//    // Now that stage is initialized, we can pass it safely
//    val soccerCardGame = new SoccerCardGame(stage)
//    stage.scene = soccerCardGame.mainMenuScene() // Start with the main menu
//  }
//}
import controller.Controller
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import model.cardComponent.Deck
import scala.collection.mutable
import scalafx.application.JFXApp3
import controller.BoostDefenderCommand

import controller.Controller
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import model.cardComponent.Deck
import scala.collection.mutable
import scalafx.application.JFXApp3
import scalafx.stage.Stage
import view.scenes.sceneManager.SceneManager
import view.scenes.MainMenuScene
import view.Gui

object Main extends JFXApp3 {
  override def start(): Unit = {
    // ✅ Create controller
    val controller = new Controller()

    // ✅ Start TUI in a separate thread
    new Thread(() => {
      val tui = new Tui(controller)
      tui.start()
    }).start()

    // ✅ Start GUI
    val gui = new Gui(controller)
    gui.start()

    // ✅ Start the game (sets players & field)
//    controller.startGame()
  }
}
import controller.Controller
import model.cardComponent.Card

//object Main extends App {
//  val controller = new Controller()
//
//  // Start the game
//  controller.startGame()
//  controller.setPlayerName(1, "Alice")
//  controller.setPlayerName(2, "Bob")
//
//  // Print the initial playing field state
//  println("\n=== Initial Playing Field ===")
//  println(controller.getPlayingField)
//
//  // Boost a defender (Assuming defender at index 0)
//  println("\n=== Boosting Defender at Index 0 ===")
//  println(controller.getPlayingField.getField(controller.getPlayingField.getAttacker))
//  controller.getPlayingField.chooseBoostCardDefender(0)
//
//  // Print the playing field after boosting
//  println("\n=== After Boost ===")
//  println(controller.getPlayingField)
//
//  // Get the boosted card before reverting
//  val boostedCard = controller.getPlayingField.playerDefenders(controller.getPlayingField.getAttacker).head
//  println(s"\n=== Before Reverting ===\nBoosted Card: $boostedCard")
//
//  // Revert the boost using `revertCard`
//  val revertedCard = controller.getPlayingField.revertCard(boostedCard)
//  controller.undo()
//  // Print the reverted card
//  println(s"\n=== After Reverting and udo ===\nReverted Card: $revertedCard")
//
//  // Print the final playing field state
//  println("\n=== Final Playing Field ===")
//  println(controller.getPlayingField)
//
//  println("\n=== Game Simulation Complete ===")
//}

//object Main {
//  def main(args: Array[String]): Unit = {
//    val controller = new Controller()
//
//    println("🎮 Welcome to the Card Battle Game!")
//
//    // ✅ Start Game
//    controller.startGame()
//
//    // ✅ Set Player Names
//    controller.setPlayerName(1, "Alice") // Attacker
//    controller.setPlayerName(2, "Bob")   // Defender
//
//    println(s"✅ Game started with Attacker: ${controller.getPlayer1.name} and Defender: ${controller.getPlayer2.name}")
//
//    // ✅ Print Initial Hands
////    println(s"🃏 ${controller.getPlayer1.name}'s hand: ${controller.getPlayer1.getCards.mkString(", ")}")
//    println(s"🛡️ ${controller.getPlayer2.name}'s field: ${controller.getPlayingField.getField(controller.getPlayer2).mkString(", ")}")
//
//    // ✅ Boost Defender at position 0
//    println("🚀 Boosting Defender at position 0!")
//    controller.getPlayingField.chooseBoostCardDefender(0)
//    println(s"🛡️ ${controller.getPlayer2.name}'s field: ${controller.getPlayingField.getField(controller.getPlayer2).mkString(", ")}")
//    // ✅ Attack Defender at position 0
//    controller.getPlayingField.switchRoles()
//    println("⚔️ Attacking Defender at position 0!")
//    controller.executeAttackCommand(0)
//
//    // ✅ Print Updated Hands
//    println(s"🔄 Updated ${controller.getPlayer1.name}'s hand: ${controller.getPlayer1.getCards.mkString(", ")}")
////    println(s"🔄 Updated ${controller.getPlayer2.name}'s hand: ${controller.getPlayer2.getCards.mkString(", ")}")
//
//    println("🎮 Game action completed!")
//  }
//}