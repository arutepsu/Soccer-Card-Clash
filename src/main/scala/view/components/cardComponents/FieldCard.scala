package view.components.cardComponents

import model.cardComponent.cardBaseImplementation.Card
import view.components.cardComponents.GameCard

case class FieldCard(
                      card: Card,
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
