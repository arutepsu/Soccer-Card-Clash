package view.components.cardComponents

import model.cardComponent.ICard
import model.cardComponent.base.{Suit, Value}
import scalafx.animation.ScaleTransition
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.HBox
import scalafx.util.Duration
import view.utils.CardImageLoader

val defaultCardStyle =
  "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 5, 5);"
val mainCardScaleFactor: Float = 0.4

/** Base Class for a Visual Card Component */
abstract class GameCard(
                         flipped: Boolean = true,
                         isLastCard: Boolean = false, // ✅ Track last card in hand
                         isSelectable: Boolean = false,
                         card: ICard,
                         scaleFactor: Float = mainCardScaleFactor
                       ) extends HBox {

  var isSelected: Boolean = false

  // Path for images


  // Image Handling
  val cardImage = CardImageLoader.loadCardImage(card, flipped, isLastCard, scaleFactor)

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
}
