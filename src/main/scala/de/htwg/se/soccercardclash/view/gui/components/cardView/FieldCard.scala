package de.htwg.se.soccercardclash.view.gui.components.cardView

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.view.gui.components.cardView.GameCard
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.CardAnimationFactory
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint.Color

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
object FieldCardFactory {
  def createDefaultFieldCard(card: ICard): FieldCard = {
    val fieldCard = FieldCard(flipped = false, card = card)
    fieldCard.styleClass.add("field-card")
    fieldCard.effect = new DropShadow(10, Color.BLACK)

    // Apply boost visual if needed
    card match
      case boosted: BoostedCard => CardAnimationFactory.applyBoostEffect(fieldCard)
      case _ =>

    fieldCard
  }

  def createSelectableFieldCard(
                                 card: ICard,
                                 index: Int,
                                 selectedIndex: => Option[Int],
                                 isGoalkeeper: Boolean = false,
                                 onSelected: Int => Unit
                               ): FieldCard = {
    val fieldCard = FieldCard(flipped = false, card = card)
    fieldCard.styleClass.add("field-card")
    fieldCard.effect = new DropShadow(10, Color.BLACK)

    // Boost animation
    card match
      case boosted: BoostedCard => CardAnimationFactory.applyBoostEffect(fieldCard)
      case _ =>

    // Hover
    fieldCard.onMouseEntered = _ => CardAnimationFactory.applyHoverEffect(fieldCard, selectedIndex, index)
    fieldCard.onMouseExited = _ => CardAnimationFactory.removeHoverEffect(fieldCard, selectedIndex, index)

    // Click selection
    fieldCard.onMouseClicked = _ => {
      if (selectedIndex.contains(index)) {
        fieldCard.effect = null
        onSelected(-1)
      } else {
        onSelected(index)
        fieldCard.effect = new DropShadow(20, Color.GOLD)
      }
    }

    fieldCard
  }

}
