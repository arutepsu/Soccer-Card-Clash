package de.htwg.se.soccercardclash.view.gui.components.cardView

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.view.gui.components.cardView.GameCard
import scalafx.Includes.*
import scalafx.geometry.Pos
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import de.htwg.se.soccercardclash.view.gui.components.cardView.HandCard
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.CardAnimationFactory
import scalafx.animation.ScaleTransition
import scalafx.util.Duration
case class HandCard(
                     card: ICard,
                     flipped: Boolean = true,
                     isLastCard: Boolean = false,
                     isSelectable: Boolean = false,
                     scaleFactor: Float = mainCardScaleFactor
                   ) extends GameCard(flipped, isLastCard, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
  
}
object HandCardFactory {
  def createSelectableHandCard(
                                card: ICard,
                                index: Int,
                                selectedIndex: => Option[Int],
                                onSelected: Int => Unit
                              ): HandCard = {
    val handCard = new HandCard(flipped = false, card = card)
    handCard.effect = new DropShadow(10, Color.BLACK)

    handCard.onMouseEntered = _ =>
      CardAnimationFactory.applyHoverEffect(handCard, selectedIndex, index)

    handCard.onMouseExited = _ =>
      CardAnimationFactory.removeHoverEffect(handCard, selectedIndex, index)

    handCard.onMouseClicked = _ => {
      if (selectedIndex.contains(index)) {
        handCard.effect = null
        onSelected(-1)
      } else {
        onSelected(index)
        handCard.effect = new DropShadow(20, Color.GOLD)
      }
    }

    handCard
  }
}
