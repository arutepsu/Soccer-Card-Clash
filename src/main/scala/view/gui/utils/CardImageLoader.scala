package view.gui.utils

import model.cardComponent.ICard
import model.cardComponent.base.components.{Suit, Value}
import scalafx.animation.ScaleTransition
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.HBox
import scalafx.util.Duration
import view.gui.utils.ImageUtils
object CardImageLoader {
  val cardsPath = "/images/data/cards/"

  def loadCardImage(card: ICard, flipped: Boolean, isLastCard: Boolean, scaleFactor: Float): ImageView = {
    val imageUrl = if (flipped && !isLastCard) cardsPath + "card_black.png" else getCardFilePath(card) // ✅ All flipped cards except the last one

    val image = try {
      new Image(getClass.getResourceAsStream(imageUrl))
    } catch {
      case e: Exception =>
        println(s"⚠️ [ERROR] Card image not found: $imageUrl (${e.getMessage})")
        null
    }

    new ImageView(image) {
      fitWidth = 400 * scaleFactor // ✅ Smaller width
      fitHeight = 200 * scaleFactor // ✅ Smaller height
      preserveRatio = true
    }
  }

  def getCardFilePath(card: ICard): String = {
    s"$cardsPath${card.fileName}"
  }
}