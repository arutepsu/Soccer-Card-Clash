package de.htwg.se.soccercardclash.view.gui.utils

import scalafx.geometry.Pos
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.VBox

object Assets {

  lazy val logoImage: Image = new Image(
    getClass.getResource("/images/data/logo/logo1k.png").toExternalForm
  )

  def createLogoImageView(): ImageView = new ImageView(logoImage) {
    fitWidth = 300
    preserveRatio = true
    smooth = true
  }

}