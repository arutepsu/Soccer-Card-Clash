package de.htwg.se.soccercardclash.view.gui.components.cardView

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.view.gui.components.cardView.GameCard

case class FieldCard(
                      card: ICard,
                      flipped: Boolean = true,
                      isSelectable: Boolean = false,
                      scaleFactor: Float = mainCardScaleFactor
                    ) extends GameCard(flipped, isLastCard = false, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)

  // 🔥 Get width of the card
  def getWidth: Double = this.prefWidth.value

  // 🔥 Get height of the card
  def getHeight: Double = this.prefHeight.value
}
