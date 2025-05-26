package de.htwg.se.soccercardclash.view.gui.utils

import scalafx.scene.image.{Image, ImageView}

object ImageUtils {
  private var boostImage: Option[Image] = None

  def preloadImages(): Unit = {
    boostImage = Some(importImage("/images/data/cards/effects/boost.png"))
  }

  def getBoostImage: Image = {
    boostImage.getOrElse {
      throw new IllegalStateException("Boost image not loaded. Call preloadImages() first.")
    }
  }

  def importImage(imageUrl: String): Image = {
    val resource = getClass.getResourceAsStream(imageUrl)
    if (resource == null) {
      throw new IllegalArgumentException(s"Image not found: $imageUrl. Check that it exists in src/main/resources/")
    }
    new Image(resource)
  }
}
