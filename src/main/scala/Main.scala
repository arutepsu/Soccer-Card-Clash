
import scalafx.application.JFXApp3
import view.Tui
import controller.IController
import controller.base.Controller
import model.cardComponent.base.RegularCard
import view.Gui
import view.tui.TuiManager
object Main extends JFXApp3 {
  private val controller: IController = new Controller()
  override def start(): Unit = {
    // ✅ Start TUI in a separate thread
    new Thread(() => {
      val tuiManager = new TuiManager(controller)
      tuiManager.start()
    }).start()

    // ✅ Start GUI
    val gui = new Gui(controller)
    gui.start()
    controller.notifyObservers()
  }
}
import model.cardComponent.base.Card
import model.cardComponent.base.Suit.{Hearts, Diamonds, Clubs, Spades, Suit}
import model.cardComponent.base.Value._
import model.cardComponent.base.{BoostedCard, RegularCard, Suit, Value}
import model.cardComponent.ICard
//object Main {
//  def main(args: Array[String]): Unit = {
//    println("🎮 Starting Soccer Clash Card Game Backend Boosting Test...")
//
//    // ✅ Create the game controller
//    val controller = new Controller()
//
//    // ✅ Start the game
//    controller.startGame()
//    println("\n🏆 Game Started!")
//
//    // ✅ Retrieve initial playing field and players
//    val playingField = controller.getPlayingField
//    val player1 = controller.getPlayer1
//    val player2 = controller.getPlayer2
//
//    // ✅ Print Initial Defender Field for Player 1
//    println("\n🛡️ Initial Defender Field for Player 1:")
//    playingField.fieldState.getPlayerDefenders(player1).zipWithIndex.foreach {
//      case (card, index) =>
//        println(s"   [$index] ${card} (Type: ${card.getClass.getSimpleName})")
//    }
//
//    // ✅ Select a defender to boost (e.g., index 0)
//    val defenderIndexToBoost = 0
//    println(s"\n🚀 Boosting Defender at index: $defenderIndexToBoost")
//    controller.boostDefender(defenderIndexToBoost)
//
//    // ✅ Print Updated Defender Field After Boosting
//    println("\n🛡️ Updated Defender Field for Player 1 (After Boosting):")
//    playingField.fieldState.getPlayerDefenders(player1).zipWithIndex.foreach {
//      case (card, index) =>
//        println(s"   [$index] ${card} (Type: ${card.getClass.getSimpleName})")
//    }
//
//    // ✅ Check if the boosted defender is now a BoostedCard2
//    val updatedDefender = playingField.fieldState.getPlayerDefenders(player1)(defenderIndexToBoost)
//
//    if (updatedDefender.isInstanceOf[BoostedCard2]) {
//      println(s"\n✅ Success! Defender at index $defenderIndexToBoost is now a BoostedCard2!")
//    } else {
//      println(s"\n❌ Boosting failed! Defender at index $defenderIndexToBoost is still a RegularCard2.")
//    }
//
//    println("\n🎉 Backend Boosting Test Completed! 🎉")
//  }
//}

