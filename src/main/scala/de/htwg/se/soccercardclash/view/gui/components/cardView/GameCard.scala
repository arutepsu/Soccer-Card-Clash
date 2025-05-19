package de.htwg.se.soccercardclash.view.gui.components.cardView

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import scalafx.animation.ScaleTransition
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.HBox
import scalafx.util.Duration
import de.htwg.se.soccercardclash.view.gui.utils.CardImageRegistry

val defaultCardStyle =
  "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 5, 5);"

val mainCardScaleFactor: Float = 0.45f

abstract class GameCard(
                         flipped: Boolean = true,
                         isLastCard: Boolean = false,
                         isSelectable: Boolean = false,
                         card: Option[ICard],
                         scaleFactor: Float = mainCardScaleFactor
                       ) extends HBox {

  var isSelected: Boolean = false

  val image: Image = {
    val fileName = if (flipped && !isLastCard)
      "images/data/cards/defeated.png"
    else
      card.map(_.fileName).getOrElse("king_of_clubs2.png")

    CardImageRegistry.getImage(fileName)
  }


  val cardImage = new ImageView(image) {
    fitWidth = 525 * scaleFactor
    fitHeight = 325 * scaleFactor
    preserveRatio = true
    smooth = true
  }

  val pulsateTransition = new ScaleTransition(Duration(1000), cardImage) {
    fromX = 1.0
    toX = 1.05
    fromY = 1.0
    toY = 1.05
    cycleCount = ScaleTransition.Indefinite
    autoReverse = true
  }

  style = defaultCardStyle
  children.add(cardImage)

  def flip(): GameCard
}
