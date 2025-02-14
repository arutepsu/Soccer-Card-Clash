package view.components.gameComponents

import scalafx.scene.layout.{VBox, HBox}
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import model.cardComponent.Card
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.scene.effect.DropShadow
import scalafx.Includes._          // ✅ Ensures JavaFX → ScalaFX conve
import scalafx.scene.paint.Color
import scalafx.scene.effect.{Glow, DropShadow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.animation.ScaleTransition
import scalafx.util.Duration
import scalafx.Includes._
import view.components.cardComponents.FieldCard
import view.components.uiFactory.{CardAnimationFactory, CardImageLoader}
import view.utils.Styles
class PlayersFieldBar(player: Player, playingField: PlayingField) extends VBox {

  alignment = Pos.CENTER
  spacing = 10
  this.getStylesheets.add(Styles.playersFieldBarCss) // ✅ Load external CSS
  styleClass.add("players-field-bar") // ✅ Apply panel style
  /** Label indicating the current game state */

  private val statusLabel = new Label {
    text = s"${playingField.getAttacker.name} attacks ${playingField.getDefender.name}!"
    styleClass.add("status-label") // ✅ Apply CSS class
  }

  /** Label indicating which player's field this is */
  /** Label indicating which player's field this is */
  private val playerLabel = new Label {
    text = s"${player.name}'s Field"
    styleClass.add("player-label") // ✅ Apply CSS class
  }

  /** Retrieves defender cards */
  private def getDefenderCards: List[Card] = playingField.playerDefenders(player)


  private var selectedDefender: Option[FieldCard] = None // Track selected defender card

  /** Store the selected defender index */
  private var _selectedDefenderIndex: Option[Int] = None

  /** Public getter for selected defender index */
  def selectedDefenderIndex: Option[Int] = _selectedDefenderIndex

  /** Public setter to reset selection */
  def resetSelectedDefender(): Unit = {
    _selectedDefenderIndex = None
  }

  /** Creates Defender Row */

  /** Store the currently selected defender card */
  private var selectedDefenderCard: Option[FieldCard] = None


  /** Creates Defender Row */

  def createDefenderRow(): HBox = {
    val defenderCards = getDefenderCards

    println(s"🛡️ Creating defender row for ${player.name} with cards: $defenderCards")

    val defenderCardNodes = defenderCards.zipWithIndex.map { case (card, index) =>
      val defenderCard = new FieldCard(flipped = false, card = card)
      defenderCard.styleClass.add("field-card") // ✅ Apply CSS class

      if(defenderCard.card.wasBoosted) {
//        CardAnimationFactory.createFireEffect(defenderCard)
        CardAnimationFactory.applyBoostEffect(defenderCard)
      }

      defenderCard.onMouseEntered = (_: MouseEvent) => CardAnimationFactory.applyHoverEffect(defenderCard, _selectedDefenderIndex, index)
      defenderCard.onMouseExited = (_: MouseEvent) => CardAnimationFactory.removeHoverEffect(defenderCard, _selectedDefenderIndex, index)

      // Card selection logic
      if (playingField.getDefender == player) {
        defenderCard.onMouseClicked = (_: MouseEvent) => {
          if (_selectedDefenderIndex.contains(index)) {
            // Deselect the card
            println(s"❌ Deselected: $card (Index: $index)")
            defenderCard.effect = null
            _selectedDefenderIndex = None
            selectedDefenderCard = None
            defenderCard.styleClass.remove("selected-card")
          } else {

            // Deselect the previously selected card
            _selectedDefenderIndex.foreach { _ =>
              println(s"🔄 Deselecting previous defender")
              selectedDefenderCard.foreach(_.effect = null)
              defenderCard.styleClass.add("selected-card") //
            }

            println(s"🛡️ Selected: $card (Index: $index)")
            _selectedDefenderIndex = Some(index)
            selectedDefenderCard = Some(defenderCard)

            // Apply gold shadow effect for selection
            defenderCard.effect = new DropShadow(20, Color.GOLD)
          }
        }
      }
      defenderCard
    }

    new HBox {
      styleClass.add("defender-row") // ✅ Apply styling
//      alignment = Pos.CENTER
//      spacing = 10
      children = defenderCardNodes
    }
  }
  /** **Creates UI row for goalkeeper card** */
  def createGoalkeeperRow(): HBox = {
    val goalkeeperCard = playingField.playerGoalkeeper(player) match {
      case Some(card) => new FieldCard(flipped = false, card = card)
      case None => throw new IllegalStateException("No goalkeeper set! The game logic must always have one.")
    }
    if (goalkeeperCard.card.additionalValue > 0) {
      CardAnimationFactory.applyBoostEffect(goalkeeperCard)
    }
    goalkeeperCard.styleClass.add("field-card") // ✅ Apply styling

    // Track selected goalkeeper index
    var _selectedGoalkeeperIndex: Option[Int] = None

    goalkeeperCard.onMouseEntered = (_: MouseEvent) => CardAnimationFactory.applyHoverEffect(goalkeeperCard, _selectedGoalkeeperIndex, 0)
    goalkeeperCard.onMouseExited = (_: MouseEvent) => CardAnimationFactory.removeHoverEffect(goalkeeperCard, _selectedGoalkeeperIndex, 0)
    goalkeeperCard.onMouseClicked = (_: MouseEvent) => _selectedGoalkeeperIndex = CardAnimationFactory.toggleGoalkeeperSelection(goalkeeperCard, _selectedGoalkeeperIndex)

    new HBox {
      styleClass.add("goalkeeper-row") // ✅ Apply styling
      alignment = Pos.CENTER
//      spacing = 10
      children = Seq(goalkeeperCard)
    }
  }

  // ✅ **Initialize UI (without re-updating the goalkeeper)**
  children = Seq(statusLabel, playerLabel, createDefenderRow(), createGoalkeeperRow())

  /** **Update the entire field dynamically WITHOUT updating goalkeeper** */
  def updateField(): Unit = {
    println(s"🔄 Updating defender's field for ${player.name}...")
    children.clear()
    children.addAll(statusLabel, playerLabel, createDefenderRow(), createGoalkeeperRow()) // No updateGoalkeeper()
    playingField.notifyObservers() // ✅ Ensure UI refreshes
  }

  /** **Update game status dynamically** */
  def updateGameStatus(): Unit = {
    statusLabel.text = s"${playingField.getAttacker.name} attacks ${playingField.getDefender.name}!"
  }
}
