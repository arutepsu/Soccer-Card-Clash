package de.htwg.se.soccercardclash.view.gui.components.uiFactory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import scalafx.scene.image.{Image, ImageView}

object BoostLoader {

  private val cardImagePath = "/images/data/cards/"
  private val defaultWidth = 200.0
  private val defaultHeight = 220.0

  def getCardImageView(card: ICard, width: Double = defaultWidth, height: Double = defaultHeight): ImageView = {
    val fileName = card.fileName
    val filePath = s"$cardImagePath$fileName"

    val resource = getClass.getResource(filePath)

    val cardImage = if (resource != null) new Image(resource.toString)
    else new Image(getClass.getResource("/view/data/cards/card_back.png").toString)

    val imageView = new ImageView(cardImage) {
      preserveRatio = true
      fitWidth = Math.min(cardImage.width.value, width)
      fitHeight = Math.min(cardImage.height.value, height)
    }

    imageView
  }

}

