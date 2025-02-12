package view.components.gameComponents

import scalafx.scene.layout.HBox
import scalafx.geometry.Pos
import controller.Controller
import view.components.uiFactory.GameButtonFactory
import scalafx.scene.control.Button

class DoubleAttackBar(controller: Controller) extends HBox {
  spacing = 10
  alignment = Pos.CENTER

  // ✅ Initialize Attack Button
  private val attackButton: Button = GameButtonFactory.createGameButton(
    text = "Double Attack",
    width = 150,
    height = 50
  ) { () => println("Attack button clicked!") } // ✅ Placeholder action


  children.addAll(attackButton)

  /** ✅ Method to set external action for Attack */
  def setAttackAction(action: () => Unit): Unit = attackButton.onAction = _ => action()

  /** ✅ Method to set external action for Boost */

  /** ✅ Return Attack Button */
  def getAttackButton: Button = attackButton

  /** ✅ Return Boost Button */
}
