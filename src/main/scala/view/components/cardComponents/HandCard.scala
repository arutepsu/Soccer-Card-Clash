package view.components.cardComponents

import model.cardComponent.base.Card
import view.components.cardComponents.GameCard

case class HandCard(
                     card: Card,
                     flipped: Boolean = true,
                     isLastCard: Boolean = false, // ✅ Mark if it's the last card
                     isSelectable: Boolean = false,
                     scaleFactor: Float = mainCardScaleFactor
                   ) extends GameCard(flipped, isLastCard, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
  
}