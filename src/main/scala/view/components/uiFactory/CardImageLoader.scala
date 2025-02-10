package view.components.uiFactory

import model.cardComponent.Card
import scalafx.scene.image.{Image, ImageView}

object CardImageLoader {

  private val cardImagePath = "/view/data/cards/"

  /**
   * Loads an ImageView from resources.
   */
  def getCardImageView(card: Card, width: Double = 80, height: Double = 120): ImageView = {
    val fileName = card.fileName  // Example: "4_of_diamonds.png"
    val filePath = s"$cardImagePath$fileName"  // Example: "/view/data/cards/4_of_diamonds.png"

    val resource = getClass.getResource(filePath)

    val cardImage = if (resource != null) new Image(resource.toString) else {
      new Image(getClass.getResource("/view/data/cards/card_back.png").toString) // Fallback
    }

    new ImageView(cardImage) {
      fitWidth = width
      fitHeight = height
      preserveRatio = true
    }
  }
}
