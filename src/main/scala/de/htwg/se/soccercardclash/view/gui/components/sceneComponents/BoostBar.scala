package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import scalafx.scene.layout.HBox
import scalafx.geometry.Pos
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import scalafx.scene.control.Button

class BoostBar(controller: IController) extends HBox {
  spacing = 10
  alignment = Pos.CENTER
  
  private val boostButton: Button = GameButtonFactory.createGameButton(
    text = "Boost",
    width = 150,
    height = 50
  ) { () => println("Boost button clicked!") }

  children.addAll(boostButton)
  
  def setBoostAction(action: () => Unit): Unit = boostButton.onAction = _ => action()
  
  def getBoostButton: Button = boostButton
}
