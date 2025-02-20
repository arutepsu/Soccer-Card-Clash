package view.gui.components.cardView

import model.cardComponent.ICard
import view.gui.components.cardView.GameCard

case class GoalkeeperCard(
                           card: ICard,
                           flipped: Boolean = true,
                           isSelectable: Boolean = false,
                           scaleFactor: Float = mainCardScaleFactor
                         ) extends GameCard(flipped, isLastCard = false, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
}
