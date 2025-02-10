package view.components.gameComponents

import scalafx.scene.layout.HBox
import scalafx.geometry.Pos
import controller.Controller
import view.components.uiFactory.GameButtonFactory
import scalafx.scene.control.Button

class SpecialActionsBar(controller: Controller) extends HBox {
  spacing = 10
  alignment = Pos.CENTER

  val attackButton: Button = GameButtonFactory.createGameButton(
    text = "Attack",
    width = 150,
    height = 50
  ) { () => println("Attack button clicked!") } // ✅ Temporary action for testing

  val boostButton: Button = GameButtonFactory.createGameButton(
    text = "Boost Card",
    width = 150,
    height = 50
  ) { () => println("Boost Card button clicked!") } // ✅ Temporary action for testing

  children.addAll(attackButton, boostButton)

  // Methods to set actions externally
  def setAttackAction(action: () => Unit): Unit = attackButton.onAction = _ => action()
  def setBoostAction(action: () => Unit): Unit = boostButton.onAction = _ => action()
}
