package view.components.gameComponents

import scalafx.scene.layout.{VBox, HBox}
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import model.cardComponent.Card
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.scene.effect.DropShadow
import scalafx.Includes._
import scalafx.scene.paint.Color
import view.components.cardComponents.FieldCard
import view.components.uiFactory.{CardAnimationFactory, CardImageLoader}

/** This version allows selecting ANY card (defenders + goalkeeper) */
class SelectablePlayersFieldBar(player: Player, playingField: PlayingField) extends PlayersFieldBar(player, playingField) {

  /** Store the currently selected card index */
  private var _selectedCardIndex: Option[Int] = None

  /** ✅ Public method to get the selected card */
  def getSelectedCard: Option[FieldCard] = selectedCard
  /** Public getter for selected card index */
  def selectedCardIndex: Option[Int] = _selectedCardIndex

  /** Public setter to reset selection */
  def resetSelectedCard(): Unit = {
    _selectedCardIndex = None
  }

  /** Store the currently selected card */
  private var selectedCard: Option[FieldCard] = None
  private def getDefenderCards: List[Card] = playingField.playerDefenders(player)

  /** ✅ Creates Defender Row (Selectable) */
  override def createDefenderRow(): HBox = {
    val defenderCards = getDefenderCards

    println(s"🛡️ Creating selectable defender row for ${player.name} with cards: $defenderCards")

    val defenderCardNodes = defenderCards.zipWithIndex.map { case (card, index) =>
      val defenderCard = new FieldCard(flipped = false, card = card)
      if (defenderCard.card.getAdditionalValue > 0) {
        CardAnimationFactory.createFireEffect(defenderCard)
      }

      defenderCard.onMouseEntered = (_: MouseEvent) => CardAnimationFactory.applyHoverEffect(defenderCard, _selectedCardIndex, index)
      defenderCard.onMouseExited = (_: MouseEvent) => CardAnimationFactory.removeHoverEffect(defenderCard, _selectedCardIndex, index)

      // ✅ Allow any defender to be selected
      defenderCard.onMouseClicked = (_: MouseEvent) => {
        if (_selectedCardIndex.contains(index)) {
          println(s"❌ Deselected: $card (Index: $index)")
          defenderCard.effect = null
          _selectedCardIndex = None
          selectedCard = None
        } else {
          _selectedCardIndex.foreach { _ =>
            println(s"🔄 Deselecting previous card")
            selectedCard.foreach(_.effect = null)
          }

          println(s"🛡️ Selected: $card (Index: $index)")
          _selectedCardIndex = Some(index)
          selectedCard = Some(defenderCard)
          defenderCard.effect = new DropShadow(20, Color.GOLD)
        }
      }
      defenderCard
    }

    new HBox {
      alignment = Pos.CENTER
      spacing = 10
      children = defenderCardNodes
    }
  }

  /** ✅ Creates Goalkeeper Row (Selectable) */
  override def createGoalkeeperRow(): HBox = {
    val goalkeeperCard = playingField.playerGoalkeeper(player) match {
      case Some(card) => new FieldCard(flipped = false, card = card)
      case None => throw new IllegalStateException("No goalkeeper set! The game logic must always have one.")
    }

    if (goalkeeperCard.card.getAdditionalValue > 0) {
      CardAnimationFactory.applyBoostEffect(goalkeeperCard)
    }

    goalkeeperCard.onMouseEntered = (_: MouseEvent) => CardAnimationFactory.applyHoverEffect(goalkeeperCard, _selectedCardIndex, 0)
    goalkeeperCard.onMouseExited = (_: MouseEvent) => CardAnimationFactory.removeHoverEffect(goalkeeperCard, _selectedCardIndex, 0)

    goalkeeperCard.onMouseClicked = (_: MouseEvent) => {
      if (_selectedCardIndex.contains(0)) {
        println(s"❌ Deselected Goalkeeper")
        goalkeeperCard.effect = null
        _selectedCardIndex = None
        selectedCard = None
      } else {
        _selectedCardIndex.foreach { _ =>
          println(s"🔄 Deselecting previous card")
          selectedCard.foreach(_.effect = null)
        }

        println(s"🛡️ Selected Goalkeeper")
        _selectedCardIndex = Some(0)
        selectedCard = Some(goalkeeperCard)
        goalkeeperCard.effect = new DropShadow(20, Color.GOLD)
      }
    }

    new HBox {
      alignment = Pos.CENTER
      spacing = 10
      children = Seq(goalkeeperCard)
    }
  }

  // ✅ Update UI with selectable cards
  override def updateField(): Unit = {
    println(s"🔄 Updating selectable defender's field for ${player.name}...")
    children.clear()
    children.addAll(createDefenderRow(), createGoalkeeperRow())
    playingField.notifyObservers()
  }
}
