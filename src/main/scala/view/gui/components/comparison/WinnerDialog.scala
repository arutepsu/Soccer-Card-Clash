package view.gui.components.comparison

import model.playerComponent.IPlayer
import view.gui.components.playerView.PlayerAvatar
import view.gui.overlay.Overlay
import controller.{Events, IController}
import scalafx.scene.control.Button
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.image.{Image, ImageView}
import scalafx.geometry.Pos
import scalafx.scene.Node
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.application.Platform
import view.gui.components.uiFactory.GameButtonFactory

class WinnerDialog(winner: IPlayer, overlay: Overlay, controller: IController, autoHide: Boolean) {

  // 🎨 Load Background Image
  val backgroundImagePath = "/images/data/frames/pause (1).png"
  val imageUrl = Option(getClass.getResource(backgroundImagePath))
    .map(_.toExternalForm)
    .getOrElse {
      println(s"Error: Image not found at $backgroundImagePath")
      ""
    }

  val backgroundImageView = new ImageView(new Image(imageUrl)) {
    fitWidth = 800 // ✅ Adjust based on desired size
    fitHeight = 500
    preserveRatio = false
  }

  // 🏆 Determine Winner's Avatar Path
  val winnerAvatarPath = if (winner == controller.getCurrentGame.getPlayer1) {
    "/images/data/players/player1.jpeg"
  } else {
    "/images/data/players/player2.jpeg"
  }

  // 👤 Load Winner Avatar
  val avatarImage = new ImageView(new Image(winnerAvatarPath)) {
    fitWidth = 150
    fitHeight = 150
    preserveRatio = true
  }

  // 🏅 Winner Label
  val winnerLabel = new Text(s"🏆 ${winner.name} Wins!") {
    style = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;"
  }

  // 🔙 Main Menu Button (From `GameButtonFactory`)
  val mainMenuButton: Button = GameButtonFactory.createGameButton(
    text = "Main Menu",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide() // ✅ Hide the overlay first
    Future {
      Thread.sleep(300) // ✅ Small delay before switching scenes for smooth transition
      Platform.runLater {
        controller.notifyObservers(Events.MainMenu) // ✅ Notify SceneManager
        controller.resetGame()
      }
    }
  }

  // 🖼️ Dialog Layout
  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    children = Seq(
      winnerLabel,
      avatarImage,
      mainMenuButton // ✅ Added Main Menu Button
    )
  }

  // 🔲 StackPane to Layer Background Image & Content
  val dialogPane = new StackPane {
    alignment = Pos.CENTER
    children = Seq(backgroundImageView, layout) // ✅ Background First, Then Content
  }

  // 🎬 Show in Overlay
  overlay.show(dialogPane, autoHide) // ✅ Winner popup stays until manually closed
}