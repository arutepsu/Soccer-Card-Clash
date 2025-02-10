package view.components.uiFactory

import model.cardComponent.Card
import scalafx.scene.image.{Image, ImageView}

object CardImageLoader {

  private val cardImagePath = "/view/data/cards/"
  private val defaultWidth = 80.0  // Default width for cards
  private val defaultHeight = 120.0 // Default height for cards

  /**
   * Loads an ImageView from resources and scales it appropriately.
   */
  def getCardImageView(card: Card, width: Double = defaultWidth, height: Double = defaultHeight): ImageView = {
    val fileName = card.fileName  // Example: "4_of_diamonds.png"
    val filePath = s"$cardImagePath$fileName"  // Example: "/view/data/cards/4_of_diamonds.png"

    val resource = getClass.getResource(filePath)

    // Load card image or fallback
    val cardImage = if (resource != null) new Image(resource.toString)
    else new Image(getClass.getResource("/view/data/cards/card_back.png").toString)

    // Scale image while preserving aspect ratio
    val imageView = new ImageView(cardImage) {
      preserveRatio = true
      fitWidth = Math.min(cardImage.width.value, width)  // Prevent oversized images
      fitHeight = Math.min(cardImage.height.value, height)
    }

    imageView
  }

  /**
   * Loads the burn effect frame and scales it to match the card's dimensions.
   * Throws an exception if the burn image cannot be found.
   */
  def getBurnEffectView(width: Double, height: Double): ImageView = {
    val burnImagePath = "/view/data/cards/effects/burn.png"
    val burnStream = getClass.getResourceAsStream(burnImagePath)

    if (burnStream == null) {
      throw new RuntimeException(s"🔥 [ERROR] Burn effect image not found: $burnImagePath")
    }

    val burnImage = new Image(burnStream)

    // Scale image to match the card
    new ImageView(burnImage) {
      preserveRatio = false // Ensure full coverage
      fitWidth = width
      fitHeight = height
    }
  }
}

