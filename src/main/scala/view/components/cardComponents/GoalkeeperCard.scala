package view.components.cardComponents

import model.cardComponent.cardBaseImplementation.Card
import view.components.cardComponents.GameCard

case class GoalkeeperCard(
                           card: Card,
                           flipped: Boolean = true,
                           isSelectable: Boolean = false,
                           scaleFactor: Float = mainCardScaleFactor
                         ) extends GameCard(flipped, isLastCard = false, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
}
