package view.scenes
import controller.Controller
import sceneManager.SceneManager
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.Observer
import view.components.gameComponents.*
import view.components.tui.tui
import view.components.uiFactory.GameButtonFactory
import view.utils.ImageUtils
import scalafx.stage.Stage
import view.utils.Styles


case class PlayingFieldScene(
                            controller: Controller,
                            windowWidth: Double,
                            windowHeight: Double,
                            currentPlayerViewIndex: Int,
                            onGameInfoButtonClick: () => Unit,
                            moveToGamePlayerScene: (index: Int) => Unit,
                            returnToMainMenu: () => Unit
                          ) extends Scene(windowWidth, windowHeight) with Observer {

  // ✅ Register this scene as an Observer
  controller.add(this)
  this.getStylesheets.add(Styles.playingFieldCss)
//  val backgroundView = new Region {
//    style = "-fx-background-color: black;"
//    prefWidth = windowWidth
//    prefHeight = windowHeight
//  }

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
  val speciaDobuleAttackBar = new DoubleAttackBar(controller)
  val backgroundViewSpecial = new HBox {
    alignment = Pos.CENTER_LEFT
    spacing = 20
    children = Seq(speciaDobuleAttackBar)
  }

  speciaDobuleAttackBar.setAttackAction { () =>
    val defenderFieldBar = if (playingField.getDefender == player1) player1FieldBar else player2FieldBar
    val defenderCards = playingField.playerDefenders(playingField.getDefender)

    if (defenderCards.nonEmpty) {
      defenderFieldBar.selectedDefenderIndex match {
        case Some(defenderIndex) =>
          println(s"🔥 Attacking defender at index: $defenderIndex")
          controller.executeAttackCommandDouble(defenderIndex)
          updateDisplay()
          defenderFieldBar.resetSelectedDefender()

        case None =>
          println("⚠️ No defender selected for attack!")
          gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
      }
    } else {
      println("⚽ All defenders are gone! Attacking the goalkeeper!")
      controller.executeAttackCommandDouble(0)
      updateDisplay()
    }
  }

  val attackButton: Button = GameButtonFactory.createGameButton(
    text = "Attack",
    width = 150, // Adjust width as needed
    height = 50 // Adjust height as needed
  ) { () =>
    val defenderFieldBar = if (playingField.getDefender == player1) player1FieldBar else player2FieldBar
    val defenderCards = playingField.playerDefenders(playingField.getDefender)

    if (defenderCards.nonEmpty) {
      defenderFieldBar.selectedDefenderIndex match {
        case Some(defenderIndex) =>
          println(s"🔥 Attacking defender at index: $defenderIndex")
          controller.executeAttackCommand(defenderIndex)
          updateDisplay()
          defenderFieldBar.resetSelectedDefender()

        case None =>
          println("⚠️ No defender selected for attack!")
          gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
      }
    } else {
      println("⚽ All defenders are gone! Attacking the goalkeeper!")
      controller.executeAttackCommand(0)
      updateDisplay()
    }
  }


  val player1ScoreLabel = new Label {
    text = s"${player1.name} Score: ${playingField.getScorePlayer1}"
    style = "-fx-font-size: 20; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;"
  }

  val player2ScoreLabel = new Label {
    text = s"${player2.name} Score: ${playingField.getScorePlayer2}"
    style = "-fx-font-size: 20; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;"
  }

  val undoButton: Button = GameButtonFactory.createGameButton(
    text = "Undo",
    width = 150,
    height = 50
  ) { () =>
    controller.undo()
    updateDisplay()
    gameStatusBar.updateStatus(GameStatusMessages.UNDO_PERFORMED)
  }

  val redoButton: Button = GameButtonFactory.createGameButton(
    text = "Redo",
    width = 150,
    height = 50
  ) { () =>
    controller.redo()
    updateDisplay()
    gameStatusBar.updateStatus(GameStatusMessages.REDO_PERFORMED)
  }

  val mainMenuButton: Button = GameButtonFactory.createGameButton(
    text = "Main Menu",
    width = 180,
    height = 50
  ) { () =>
    SceneManager.switchScene(new MainMenuScene(controller).mainMenuScene()) // ✅ Switch to Main Menu
  }

  // ✅ Create "Make Swap" button to switch to AttackerHandScene


  val actionButtons = new VBox {
    alignment = Pos.CENTER_LEFT
    spacing = 10
    padding = Insets(20)
    children = Seq(attackButton, undoButton, redoButton, mainMenuButton)
  }

  val playersBar = new PlayersBar(controller, moveToGamePlayerScene)

  root = new StackPane {
    children = Seq(
//      backgroundView,
      new HBox {
        alignment = Pos.CENTER_LEFT
        spacing = 20
        children = Seq(
          actionButtons,
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
              backgroundViewSpecial
            )
          }
        )
      }
    )
  }
  // ✅ Display attacker's field (Persistent instance)

  val showDefendersButton: Button = GameButtonFactory.createGameButton(
    text = "Show Defenders",
    width = 180,
    height = 50
  ) { () =>
    SceneManager.switchScene(new AttackerDefendersScene(
      controller = controller,
      playingField = playingField,
      windowWidth = windowWidth,
      windowHeight = windowHeight,
      moveToGamePlayerScene = () => SceneManager.switchScene(this) // Back to GamePlayerScene
    ))
  }

  // ✅ Add "Show Defenders" button to actionButtons
  actionButtons.children.add(showDefendersButton)
  /** ✅ Observer Pattern: Update UI when notified */

  val makeSwapButton: Button = GameButtonFactory.createGameButton(
    text = "Make Swap",
    width = 180,
    height = 50
  ) { () =>
    SceneManager.switchScene(new AttackerHandScene(
      controller = controller,
      playingField = playingField,
      windowWidth = windowWidth,
      windowHeight = windowHeight,
      moveToGamePlayerScene = () => SceneManager.switchScene(this) // ✅ Switch back to GamePlayerScene
    ))
  }

  // ✅ Add "Make Swap" button to actionButtons
  actionButtons.children.add(makeSwapButton)

  override def update: Unit = {
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

    player1ScoreLabel.text = s"${player1.name} Score: ${playingField.getScorePlayer1}"
    player2ScoreLabel.text = s"${player2.name} Score: ${playingField.getScorePlayer2}"

    newAttackerHandBar.updateHand()
    newDefenderFieldBar.updateField()

    println(controller.getPlayingField) // Print current game state in TUI for debugging
  }

}