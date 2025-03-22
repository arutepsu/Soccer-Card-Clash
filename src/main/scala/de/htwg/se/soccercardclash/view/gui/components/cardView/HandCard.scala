package de.htwg.se.soccercardclash.view.gui.components.cardView

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.view.gui.components.cardView.GameCard

case class HandCard(
                     card: ICard,
                     flipped: Boolean = true,
                     isLastCard: Boolean = false, // ✅ Mark if it's the last card
                     isSelectable: Boolean = false,
                     scaleFactor: Float = mainCardScaleFactor
                   ) extends GameCard(flipped, isLastCard, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
  
}