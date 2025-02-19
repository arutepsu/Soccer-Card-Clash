package view.components.cardComponents

import model.cardComponent.ICard
import view.components.cardComponents.GameCard

case class HandCard(
                     card: ICard,
                     flipped: Boolean = true,
                     isLastCard: Boolean = false, // ✅ Mark if it's the last card
                     isSelectable: Boolean = false,
                     scaleFactor: Float = mainCardScaleFactor
                   ) extends GameCard(flipped, isLastCard, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
  
}