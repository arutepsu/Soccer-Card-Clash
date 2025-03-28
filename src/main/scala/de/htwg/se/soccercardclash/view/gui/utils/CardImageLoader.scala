package de.htwg.se.soccercardclash.view.gui.utils

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import scalafx.animation.ScaleTransition
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.HBox
import scalafx.util.Duration
import scala.util.{Try, Success, Failure}
import scala.Option
import de.htwg.se.soccercardclash.view.gui.utils.ImageUtils
object CardImageLoader {
  val cardsPath = "/images/data/cards/"

  def loadCardImage(card: ICard, flipped: Boolean, isLastCard: Boolean, scaleFactor: Float): ImageView = {
    val imageUrl = if (flipped && !isLastCard) cardsPath + "card_black.png" else getCardFilePath(card)

    val imageOpt = Option(getClass.getResourceAsStream(imageUrl)).map(new Image(_))

    val image = imageOpt.getOrElse {
      // Fallback to a default placeholder image if the image is not found
      new Image(getClass.getResourceAsStream(cardsPath + "card_black.png"))
    }

    new ImageView(image) {
      fitWidth = 475 * scaleFactor
      fitHeight = 275 * scaleFactor
      preserveRatio = true
    }
  }

  def getCardFilePath(card: ICard): String = {
    s"$cardsPath${card.fileName}"
  }
}