package view.scenes
import controller.IController
import sceneManager.SceneManager
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.components.gameComponents.*
import view.components.uiFactory.GameButtonFactory
import view.utils.ImageUtils
import scalafx.stage.Stage
import view.utils.Styles
import controller.ControllerEvents
import scalafx.application.Platform
import view.components.gameComponents.ButtonBar
case class PlayingFieldScene(
                              controller: IController,
                              windowWidth: Double,
                              windowHeight: Double,
                            ) extends Scene(windowWidth, windowHeight) with Observer {

  this.getStylesheets.add(Styles.playingFieldCss)

  val player1 = controller.getPlayer1
  val player2 = controller.getPlayer2
  val playingField = controller.getPlayingField

  val player1HandBar = new PlayersHandBar(player1, playingField, isLeftSide = true)
  val player2HandBar = new PlayersHandBar(player2, playingField, isLeftSide = false)
  val player1FieldBar = new PlayersFieldBar(player1, playingField)
  val player2FieldBar = new PlayersFieldBar(player2, playingField)

  var attacker = playingField.getAttacker
  var defender = playingField.getDefender

  val attackerHandBar = if (attacker == player1) player1HandBar else player2HandBar
  val defenderFieldBar = if (defender == player1) player1FieldBar else player2FieldBar
  var attackersDefenders = new SelectablePlayersFieldBar(playingField.getAttacker, playingField)

  val gameStatusBar = new GameStatusBar

  val playerFields = new HBox {
    alignment = Pos.CENTER
    spacing = 150
    children = Seq(defenderFieldBar)
  }

  val playerHands = new HBox {
    alignment = Pos.CENTER
    spacing = 300
    children = Seq(attackerHandBar)
  }

  val player1ScoreLabel = new Label {
    text = s"${player1.name} Score: ${playingField.scores.getScorePlayer1}"
    style = "-fx-font-size: 20; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;"
  }

  val player2ScoreLabel = new Label {
    text = s"${player2.name} Score: ${playingField.scores.getScorePlayer2}"
    style = "-fx-font-size: 20; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;"
  }

  val buttonBar = new ButtonBar(controller, playingField, this, gameStatusBar)

  // ✅ Create "Make Swap" button to switch to AttackerHandScene

  val playersBar = new PlayersBar(controller)

  root = new StackPane {
    children = Seq(
      new HBox {
        alignment = Pos.CENTER_LEFT
        spacing = 20
        children = Seq(
          buttonBar, // Using ButtonBar here
          new VBox {
            padding = Insets(10)
            alignment = Pos.CENTER
            children = Seq(
              playersBar,
              gameStatusBar,
              playerFields,
              playerHands,
              player1ScoreLabel,
              player2ScoreLabel,
            )
          }
        )
      }
    )
  }
  override def update(e: ObservableEvent): Unit = {
    updateDisplay()
  }

  /** ✅ Refreshes the UI after any game state update */
  def updateDisplay(): Unit = {
    attacker = playingField.getAttacker
    defender = playingField.getDefender

    val newAttackerHandBar = if (attacker == player1) player1HandBar else player2HandBar
    val newDefenderFieldBar = if (defender == player1) player1FieldBar else player2FieldBar

    playerFields.children.clear()
    playerFields.children.add(newDefenderFieldBar)

    playerHands.children.clear()
    playerHands.children.add(newAttackerHandBar)

    gameStatusBar.updateStatus(GameStatusMessages.ATTACK_INITIATED, attacker.name, defender.name)

    player1ScoreLabel.text = s"${player1.name} Score: ${playingField.scores.getScorePlayer1}"
    player2ScoreLabel.text = s"${player2.name} Score: ${playingField.scores.getScorePlayer2}"

    newAttackerHandBar.updateBar()
    newDefenderFieldBar.updateBar()

    println(controller.getPlayingField) // Print current game state in TUI for debugging
  }

}