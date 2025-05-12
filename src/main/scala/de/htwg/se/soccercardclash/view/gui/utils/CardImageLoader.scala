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
  private val cardsPath = "/images/data/cards/"
  private val imageCache = scala.collection.mutable.Map[String, Image]()

  def loadCardImage(card: ICard, flipped: Boolean, isLastCard: Boolean, scaleFactor: Float): ImageView = {
    val imagePath = if (flipped && !isLastCard) {
      cardsPath + "card_black.png"
    } else {
      getCardFilePath(card)
    }

    val image = getCachedImage(imagePath)

    new ImageView(image) {
      fitWidth = 475 * scaleFactor
      fitHeight = 275 * scaleFactor
      preserveRatio = true
    }
  }

  def getCardFilePath(card: ICard): String = s"$cardsPath${card.fileName}"

  private def getCachedImage(path: String): Image = {
    imageCache.getOrElseUpdate(path, {
      Option(getClass.getResourceAsStream(path))
        .map(new Image(_))
        .getOrElse {
          println(s"⚠️ Image not found: $path — using fallback.")
          imageCache.getOrElseUpdate(cardsPath + "card_black.png", {
            new Image(getClass.getResourceAsStream(cardsPath + "card_black.png"))
          })
        }
    })
  }
  def clearCache(): Unit = {
    imageCache.clear()
  }
}
