package de.htwg.se.soccercardclash.view.gui.components.cardView

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import scalafx.animation.ScaleTransition
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.HBox
import scalafx.util.Duration
import de.htwg.se.soccercardclash.view.gui.utils.CardImageLoader

val defaultCardStyle =
  "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 5, 5);"
val mainCardScaleFactor: Float = 0.4

abstract class GameCard(
                         flipped: Boolean = true,
                         isLastCard: Boolean = false,
                         isSelectable: Boolean = false,
                         card: ICard,
                         scaleFactor: Float = mainCardScaleFactor
                       ) extends HBox {

  var isSelected: Boolean = false
  
  val cardImage = CardImageLoader.loadCardImage(card, flipped, isLastCard, scaleFactor)
  
  val pulsateTransition = new ScaleTransition(Duration(1000), cardImage)
  pulsateTransition.fromX = 1.0
  pulsateTransition.toX = 1.05
  pulsateTransition.fromY = 1.0
  pulsateTransition.toY = 1.05
  pulsateTransition.cycleCount = ScaleTransition.Indefinite
  pulsateTransition.autoReverse = true
  
  style = defaultCardStyle
  children.add(cardImage)
  
  def flip(): GameCard
}
