package view.gui.components.sceneBar.cardBar

import model.cardComponent.ICard
import model.cardComponent.base.BoostedCard
import model.playerComponent.IPlayer
import model.playingFiledComponent.PlayingField
import scalafx.Includes.*
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color
import view.gui.components.cardView.FieldCard
import view.gui.components.uiFactory.{BoostLoader, CardAnimationFactory}
import view.gui.components.sceneBar.cardBar.PlayersFieldBar

/** This version allows selecting ANY card (defenders + goalkeeper) */
class SelectablePlayersFieldBar(player: IPlayer, playingField: PlayingField) extends PlayersFieldBar(player, playingField) {

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
  def getDefenderCards: List[ICard] = playingField.fieldState.getPlayerDefenders(player)
  def getGoalkeeperCard: Option[ICard] = playingField.fieldState.getPlayerGoalkeeper(player)

  /** ✅ Creates Defender Row (Selectable) */
  override def createDefenderRow(): HBox = {
    val defenderCards = getDefenderCards

    println(s"🛡️ Creating selectable defender row for ${player.name} with cards: $defenderCards")

    val defenderCardNodes = defenderCards.zipWithIndex.map { case (card, index) =>
      val defenderCard = new FieldCard(flipped = false, card = card)
      println(s"📌 FieldCard at index $index: ${defenderCard} (Type: ${defenderCard.getClass.getSimpleName})")
      println(s"📌 FieldCard.card at index $index: ${defenderCard.card} (Type: ${defenderCard.card.getClass.getSimpleName})")

      defenderCard.card match
        case boostedCard: BoostedCard =>
          println(s"✅ BoostedCard2 detected: $boostedCard")
          CardAnimationFactory.applyBoostEffect(defenderCard)
        case _ =>
          println("❌ No boost effect applied.") // Optional debug message

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
  private var _isGoalkeeperSelected: Boolean = false
  
  def isGoalkeeperSelected: Boolean = _isGoalkeeperSelected
  
  override def createGoalkeeperRow(): HBox = {
    val goalkeeperCard = playingField.fieldState.getPlayerGoalkeeper(player) match {
      case Some(card) => new FieldCard(flipped = false, card = card)
      case None => throw new IllegalStateException("No goalkeeper set! The game logic must always have one.")
    }

    goalkeeperCard.card match {
      case boosted: BoostedCard =>
        CardAnimationFactory.applyBoostEffect(goalkeeperCard)
      case _ =>
        println("No boost effect applied.") // Optional debug message
    }
    goalkeeperCard.onMouseEntered = (_: MouseEvent) =>
      CardAnimationFactory.applyHoverEffect(goalkeeperCard, _selectedCardIndex, 0)

    goalkeeperCard.onMouseExited = (_: MouseEvent) =>
      CardAnimationFactory.removeHoverEffect(goalkeeperCard, _selectedCardIndex, 0)

    goalkeeperCard.onMouseClicked = (_: MouseEvent) => {
      if (_selectedCardIndex.contains(0)) {
        println(s"❌ Deselected Goalkeeper")
        goalkeeperCard.effect = null
        _selectedCardIndex = None
        selectedCard = None
        _isGoalkeeperSelected = false // ✅ Deselect goalkeeper
      } else {
        _selectedCardIndex.foreach { _ =>
          println(s"🔄 Deselecting previous card")
          selectedCard.foreach(_.effect = null)
        }

        println(s"🛡️ Selected Goalkeeper")
        _selectedCardIndex = Some(0)
        selectedCard = Some(goalkeeperCard)
        goalkeeperCard.effect = new DropShadow(20, Color.GOLD)
        _isGoalkeeperSelected = true // ✅ Mark goalkeeper as selected
      }
    }

    new HBox {
        alignment = Pos.CENTER
        spacing = 10
        children = Seq(goalkeeperCard)
      }
  }
  children = Seq(createDefenderRow(), createGoalkeeperRow())
  // ✅ Update UI with selectable cards
    override def updateBar(): Unit = {
      println(s"🔄 Updating defender's field for ${player.name}...")

      // ✅ Remove all previous UI components
      children.clear()

      // ✅ Re-add only the existing rows (NO new rows created)
      children.addAll(createDefenderRow(), createGoalkeeperRow())

      // ✅ Ensure the UI refreshes properly
      playingField.notifyObservers()
    }


}