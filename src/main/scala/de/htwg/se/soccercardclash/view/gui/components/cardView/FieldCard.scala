package de.htwg.se.soccercardclash.view.gui.components.cardView

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.view.gui.components.cardView.GameCard
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.CardAnimationFactory
import scalafx.scene.control.Tooltip
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.Node

case class FieldCard(
                      card: Option[ICard],
                      flipped: Boolean = true,
                      isSelectable: Boolean = false,
                      scaleFactor: Float = mainCardScaleFactor
                    ) extends GameCard(flipped, isLastCard = false, isSelectable, card, scaleFactor) {

  override def flip(): GameCard = this.copy(flipped = !flipped)
  
  def getWidth: Double = this.prefWidth.value
  
  def getHeight: Double = this.prefHeight.value
}
object FieldCardFactory {
  def createDefaultFieldCard(card: Option[ICard]): FieldCard = {
    val fieldCard = FieldCard(flipped = false, card = card)
    fieldCard.styleClass.add("field-card")
    fieldCard.effect = new DropShadow(10, Color.BLACK)

    card match
      case Some(boosted: BoostedCard) =>
        CardAnimationFactory.applyBoostEffect(fieldCard)
      case _ => ()

    fieldCard
  }

  def createSelectableFieldCard(
                                 card: Option[ICard],
                                 index: Int,
                                 selectedIndex: => Option[Int],
                                 isGoalkeeper: Boolean = false,
                                 onSelected: Int => Unit
                               ): FieldCard = {
    val fieldCard = FieldCard(flipped = false, card = card)
    fieldCard.styleClass.add("field-card")
    fieldCard.effect = new DropShadow(10, Color.BLACK)

    card match {
      case Some(boosted: BoostedCard) =>
        CardAnimationFactory.applyBoostEffect(fieldCard)
      case Some(card) =>
      case None =>
      case _ => ()
    }
    
    fieldCard.onMouseEntered = _ =>
      CardAnimationFactory.applyHoverEffect(fieldCard, selectedIndex, index)

    fieldCard.onMouseExited = _ =>
      CardAnimationFactory.removeHoverEffect(fieldCard, selectedIndex, index)

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

  def createStaticImageCard(image: Image, index: Int): Node = {
    new StackPane {
      children = new ImageView(image) {
        fitWidth = 135
        fitHeight = 125
        preserveRatio = true
      }
    }
  }


}
