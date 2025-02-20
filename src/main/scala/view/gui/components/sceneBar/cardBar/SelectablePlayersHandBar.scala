package view.gui.components.sceneBar.cardBar

import model.playerComponent.IPlayer
import model.playingFiledComponent.PlayingField
import scalafx.Includes.*
import scalafx.geometry.Pos
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import view.gui.components.cardView.HandCard
import view.gui.components.uiFactory.CardAnimationFactory
import view.gui.components.sceneBar.cardBar.PlayersHandBar

class SelectablePlayersHandBar(player: IPlayer, playingField: PlayingField, isLeftSide: Boolean) extends PlayersHandBar(player, playingField, isLeftSide) {

  private var _selectedCardIndex: Option[Int] = None
  def selectedCardIndex: Option[Int] = _selectedCardIndex

  override def createHandCardRow(): HBox = {
    val hand = playingField.fieldState.getPlayerHand(player)
    val handCards = hand.zipWithIndex.map { case (card, index) =>
      val handCard = new HandCard(flipped = false, card = card)
      handCard.effect = new DropShadow(10, Color.BLACK)

      handCard.onMouseEntered = (_: MouseEvent) => CardAnimationFactory.applyHoverEffect(handCard, _selectedCardIndex, index)
      handCard.onMouseExited = (_: MouseEvent) => CardAnimationFactory.removeHoverEffect(handCard, _selectedCardIndex, index)

      // ✅ Allow card selection
      handCard.onMouseClicked = (event: MouseEvent) => {
        if (_selectedCardIndex.contains(index)) {
          println(s"❌ Deselected: $card (Index: $index)")
          handCard.effect = null
          _selectedCardIndex = None
        } else {
          _selectedCardIndex.foreach { _ =>
            println(s"🔄 Deselecting previous card")
          }
          println(s"🃏 Selected: $card (Index: $index)")
          _selectedCardIndex = Some(index)
          handCard.effect = new DropShadow(20, Color.GOLD)
        }
      }

      handCard
    }

    new HBox {
      alignment = Pos.CENTER
      spacing = -25
      children = handCards
    }
  }

  override def updateBar(): Unit = {
    println("🔄 Updating Hand UI...")
    children.clear()
    children.addAll(createHandCardRow())
    playingField.notifyObservers()
  }
}
