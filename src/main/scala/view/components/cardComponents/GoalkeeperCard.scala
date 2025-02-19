package view.components.cardComponents

import model.cardComponent.ICard
import view.components.cardComponents.GameCard

case class GoalkeeperCard(
                           card: ICard,
                           flipped: Boolean = true,
                           isSelectable: Boolean = false,
                           scaleFactor: Float = mainCardScaleFactor
                         ) extends GameCard(flipped, isLastCard = false, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
}
