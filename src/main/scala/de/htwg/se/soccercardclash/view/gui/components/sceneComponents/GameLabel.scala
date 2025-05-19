package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.Label
import scalafx.scene.text.Font

class GameLabel(initialText: String, scalingFactor: Double = 1.0)
  extends Label {
  this.getStylesheets.add(Styles.loadGameCss)
  val inititalFontSize = 20
  text = initialText
}