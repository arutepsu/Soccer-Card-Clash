package view.components.cardComponents

import model.cardComponent.Card
import view.components.cardComponents.GameCard

case class HandCard(
                     card: Card,
                     flipped: Boolean = true,
                     isLastCard: Boolean = false, // ✅ Mark if it's the last card
                     isSelectable: Boolean = false,
                     scaleFactor: Float = mainCardScaleFactor
                   ) extends GameCard(flipped, isLastCard, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)


//onMouseClicked = _ => {
//    if (isSelectable) {
//      isSelected = !isSelected
//      if (isSelected) pulsateTransition.play()
//      else pulsateTransition.stop()
//    }
//  }
}