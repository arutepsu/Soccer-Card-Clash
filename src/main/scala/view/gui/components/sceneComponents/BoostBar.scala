package view.gui.components.sceneView

import scalafx.scene.layout.HBox
import scalafx.geometry.Pos
import controller.IController
import view.gui.components.uiFactory.GameButtonFactory
import scalafx.scene.control.Button

class BoostBar(controller: IController) extends HBox {
  spacing = 10
  alignment = Pos.CENTER

  // ✅ Initialize Boost Button
  private val boostButton: Button = GameButtonFactory.createGameButton(
    text = "Boost",
    width = 150,
    height = 50
  ) { () => println("Boost button clicked!") } // ✅ Placeholder action

  children.addAll(boostButton)

  /** ✅ Method to set external action for Boost */
  def setBoostAction(action: () => Unit): Unit = boostButton.onAction = _ => action()

  /** ✅ Return Boost Button */
  def getBoostButton: Button = boostButton
}
