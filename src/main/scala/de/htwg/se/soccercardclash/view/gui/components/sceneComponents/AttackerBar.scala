package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions}
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.scenes.{GameScene, PlayingFieldScene}
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, Priority, Region, VBox}
import de.htwg.se.soccercardclash.view.gui.utils.HasContextHolder
class AttackerBar(controller: IController, scene: HasContextHolder) extends HBox {

  spacing = 10
  alignment = Pos.TOP_CENTER
  this.getStylesheets.add(Styles.attackerBarCss)
  styleClass += "player-bar"

  private var actionsLabels: Map[IPlayer, Label] = Map.empty
  private var playerBoxes: Seq[VBox] = Seq.empty

  updateBar()

  def updateBar(): Unit = {
    children.clear()
    actionsLabels = Map.empty
    playerBoxes = Seq.empty

    val gameState = scene.getContextHolder.get.state
    val attacker = gameState.getRoles.attacker

    val avatar = PlayerAvatarRegistry.getAvatarView(attacker, scale = 0.07f)
    avatar.styleClass += "neon-avatar"

    val nameLabel = new Label(attacker.name) {
      styleClass += "player-name"
    }

    val actionLabel = new Label("") {
      styleClass += "player-actions"
    }

    actionsLabels += attacker -> actionLabel

    val avatarBox = new VBox(5, avatar) {
      alignment = Pos.CENTER
    }

    val infoBox = new VBox(5, nameLabel, actionLabel) {
      alignment = Pos.CENTER
    }

    val spacerLeft = new Region {
      HBox.setHgrow(this, Priority.ALWAYS)
    }

    val spacerRight = new Region {
      HBox.setHgrow(this, Priority.ALWAYS)
    }

    children.addAll(
      spacerLeft,
      avatarBox,
      infoBox,
      spacerRight
    )

    playerBoxes = Seq(avatarBox)

    refreshActionStates()
  }

  def refreshActionStates(): Unit = {
    val gameState = scene.getContextHolder.get.state
    val attacker = gameState.getRoles.attacker

    actionsLabels.get(attacker).foreach { label =>
      val actionLines = attacker.actionStates.map {
        case (action, CanPerformAction(n)) => s"${action.toString}: $n"
        case (action, OutOfActions)        => s"${action.toString}: 0"
      }
      label.text = actionLines.mkString("\n")
    }
  }
}
