package view.components.cardComponents

import model.cardComponent.cardBaseImplementation.{Card, Suit, Value}
import scalafx.animation.ScaleTransition
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.HBox
import scalafx.util.Duration

val defaultCardStyle =
  "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 5, 5);"
val mainCardScaleFactor: Float = 0.4

/** Base Class for a Visual Card Component */
abstract class GameCard(
                         flipped: Boolean = true,
                         isLastCard: Boolean = false, // ✅ Track last card in hand
                         isSelectable: Boolean = false,
                         card: Card,
                         scaleFactor: Float = mainCardScaleFactor
                       ) extends HBox {

  var isSelected: Boolean = false

  // Path for images
  val cardsPath = "/view/data/cards/"

  // Image Handling
  val cardImage = loadCardImage(card, flipped, isLastCard, scaleFactor)

  // Apply a glow effect on selection
  val pulsateTransition = new ScaleTransition(Duration(1000), cardImage)
  pulsateTransition.fromX = 1.0
  pulsateTransition.toX = 1.05
  pulsateTransition.fromY = 1.0
  pulsateTransition.toY = 1.05
  pulsateTransition.cycleCount = ScaleTransition.Indefinite
  pulsateTransition.autoReverse = true

  // Styling
  style = defaultCardStyle
  children.add(cardImage)

  // Abstract flip method
  def flip(): GameCard

  // Image loading method
  private def loadCardImage(card: Card, flipped: Boolean, isLastCard: Boolean, scaleFactor: Float): ImageView = {
    val imageUrl = if (flipped && !isLastCard) cardsPath + "card_black.png" else getCardFilePath(card) // ✅ All flipped cards except the last one

    val image = try {
      new Image(getClass.getResourceAsStream(imageUrl))
    } catch {
      case e: Exception =>
        println(s"⚠️ [ERROR] Card image not found: $imageUrl (${e.getMessage})")
        null
    }

    new ImageView(image) {
      fitWidth = 400 * scaleFactor  // ✅ Smaller width
      fitHeight = 200 * scaleFactor // ✅ Smaller height
      preserveRatio = true
    }
  }

  // Constructs the correct file path for the card
  private def getCardFilePath(card: Card): String = {
    s"$cardsPath${card.fileName}"
  }
}
