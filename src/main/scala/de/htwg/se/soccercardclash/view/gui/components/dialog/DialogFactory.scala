package de.htwg.se.soccercardclash.view.gui.components.dialog

import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.Scene
import scalafx.geometry.Pos
import scalafx.scene.Node

import scala.concurrent.Future
import scalafx.application.Platform

import scala.concurrent.ExecutionContext.Implicits.global
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay

object DialogFactory {
  
  def showGameOverPopup(winner: IPlayer, overlay: Overlay, controller: IController,contextHolder: IGameContextHolder, autoHide: Boolean): Unit = {
    new WinnerDialog(winner, overlay, controller, contextHolder, autoHide)
  }

  def showLoadGameConfirmation(gameFile: String, overlay: Overlay, controller: IController): Unit = {
    new ConfirmationDialog(
      overlay,
      s"Load game: $gameFile?\nDo you want to proceed?",
      () => controller.loadGame(gameFile),
      controller
    ).show()
  }

  def showGoalScoredDialog(player: IPlayer,
                           overlay: Overlay,
                           controller: IController,
                           gameContextHolder: IGameContextHolder,
                           autoHide: Boolean = true): Unit = {
    new GoalScoredDialog(player, overlay, controller, gameContextHolder, autoHide)
  }

  def showGameSavedDialog(overlay: Overlay, autoHide: Boolean = false): Unit = {
    new GameSavedDialog(overlay, autoHide)
  }
}
