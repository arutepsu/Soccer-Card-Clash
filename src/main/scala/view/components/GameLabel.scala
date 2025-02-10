package view.components

import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.Label
import scalafx.scene.text.Font

class GameLabel(initialText: String, scalingFactor: Double = 1.0)
  extends Label {
  val inititalFontSize = 20
  text = initialText
  style = s"-fx-font-size: ${inititalFontSize * scalingFactor};"
  styleClass.add(
    "game-label"
  )
}