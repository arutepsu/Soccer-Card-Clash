package view.gui.components.cardView

import model.cardComponent.ICard
import view.gui.components.cardView.GameCard

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
