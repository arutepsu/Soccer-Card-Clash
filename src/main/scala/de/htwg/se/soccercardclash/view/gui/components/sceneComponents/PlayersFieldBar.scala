//package de.htwg.se.soccercardclash.view.gui.components.sceneComponents
//
//import de.htwg.se.soccercardclash.model.cardComponent.ICard
//import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
//import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
//import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IGameState
//import scalafx.Includes.*
//import scalafx.animation.ScaleTransition
//import scalafx.geometry.Pos
//import scalafx.scene.control.Label
//import scalafx.scene.effect.{DropShadow, Glow}
//import scalafx.scene.input.MouseEvent
//import scalafx.scene.layout.{HBox, VBox}
//import scalafx.scene.paint.Color
//import scalafx.util.Duration
//import de.htwg.se.soccercardclash.view.gui.components.cardView.FieldCard
//import de.htwg.se.soccercardclash.view.gui.components.uiFactory.CardAnimationFactory
//import de.htwg.se.soccercardclash.view.gui.utils.Styles
//class PlayersFieldBar(val player: IPlayer, playingField: IGameState) extends VBox {
//
//  alignment = Pos.CENTER
//  spacing = 10
//  this.getStylesheets.add(Styles.playersFieldBarCss)
//  styleClass.add("players-field-bar")
//  private val statusLabel = new Label {
//    text = s"${playingField.getRoles.attacker.name} attacks ${playingField.getRoles.defender.name}!"
//    styleClass.add("status-label")
//  }
//
//  private val playerLabel = new Label {
//    text = s"${player.name}'s Field"
//    styleClass.add("player-label")
//  }
//
//  def getDefenderCards: List[ICard] = playingField.getDataManager.getPlayerDefenders(player)
//
//
//  private var selectedDefender: Option[FieldCard] = None // Track selected defender card
//
//  /** Store the selected defender index */
//  private var _selectedDefenderIndex: Option[Int] = None
//
//  /** Public getter for selected defender index */
//  def selectedDefenderIndex: Option[Int] = _selectedDefenderIndex
//
//  /** Public setter to reset selection */
//  def resetSelectedDefender(): Unit = {
//    _selectedDefenderIndex = None
//  }
//
//  /** Creates Defender Row */
//
//  /** Store the currently selected defender card */
//  private var selectedDefenderCard: Option[FieldCard] = None
//
//
//  /** Creates Defender Row */
//
//  def createDefenderRow(): HBox = {
//
//    val defenderCards = getDefenderCards
//    val defenderCardNodes = defenderCards.zipWithIndex.map { case (card, index) =>
//      val defenderCard = new FieldCard(flipped = false, card = card)
//      defenderCard.styleClass.add("field-card")
//
//      // ✅ Use isInstanceOf to detect BoostedCard2
//      defenderCard.card match
//        case boostedCard: BoostedCard =>
//          CardAnimationFactory.applyBoostEffect(defenderCard)
//        case _ =>
//
//      defenderCard.onMouseEntered = (_: MouseEvent) =>
//        CardAnimationFactory.applyHoverEffect(defenderCard, _selectedDefenderIndex, index)
//
//      defenderCard.onMouseExited = (_: MouseEvent) =>
//        CardAnimationFactory.removeHoverEffect(defenderCard, _selectedDefenderIndex, index)
//
//      if (playingField.getRoles.defender == player) {
//        defenderCard.onMouseClicked = (_: MouseEvent) => {
//          if (_selectedDefenderIndex.contains(index)) {
//            println(s"❌ Deselected: $card (Index: $index)")
//            defenderCard.effect = null
//            _selectedDefenderIndex = None
//            selectedDefenderCard = None
//            defenderCard.styleClass.remove("selected-card")
//          } else {
//            _selectedDefenderIndex.foreach { _ =>
//              println(s"🔄 Deselecting previous defender")
//              selectedDefenderCard.foreach(_.effect = null)
//              defenderCard.styleClass.add("selected-card")
//            }
//            println(s"🛡️ Selected: $card (Index: $index)")
//            _selectedDefenderIndex = Some(index)
//            selectedDefenderCard = Some(defenderCard)
//            defenderCard.effect = new DropShadow(20, Color.GOLD)
//          }
//        }
//      }
//
//      defenderCard
//    }
//
//    new HBox {
//      alignment = Pos.CENTER
//      spacing = 10
//      children = defenderCardNodes
//    }
//  }
//
//  def createGoalkeeperRow(): HBox = {
//    val goalkeeperCard = playingField.getDataManager.getPlayerGoalkeeper(player) match {
//      case Some(card) => new FieldCard(flipped = false, card = card)
//      case None => throw new IllegalStateException("No goalkeeper set! The game logic must always have one.")
//    }
//    goalkeeperCard.card match {
//      case boosted: BoostedCard =>
//        CardAnimationFactory.applyBoostEffect(goalkeeperCard)
//      case _ =>
//    }
//
//    goalkeeperCard.styleClass.add("field-card") // ✅ Apply styling
//
//    // Track selected goalkeeper index
//    var _selectedGoalkeeperIndex: Option[Int] = None
//
//    goalkeeperCard.onMouseEntered = (_: MouseEvent) => CardAnimationFactory.applyHoverEffect(goalkeeperCard, _selectedGoalkeeperIndex, 0)
//    goalkeeperCard.onMouseExited = (_: MouseEvent) => CardAnimationFactory.removeHoverEffect(goalkeeperCard, _selectedGoalkeeperIndex, 0)
//    goalkeeperCard.onMouseClicked = (_: MouseEvent) => _selectedGoalkeeperIndex = CardAnimationFactory.toggleGoalkeeperSelection(goalkeeperCard, _selectedGoalkeeperIndex)
//
//    new HBox {
//      styleClass.add("goalkeeper-row") // ✅ Apply styling
//      alignment = Pos.CENTER
//      //      spacing = 10
//      children = Seq(goalkeeperCard)
//    }
//  }
//
//  // ✅ **Initialize UI (without re-updating the goalkeeper)**
//  children = Seq(statusLabel, playerLabel, createDefenderRow(), createGoalkeeperRow())
//
//  /** **Update the entire field dynamically WITHOUT updating goalkeeper** */
//  def updateBar(): Unit = {
//    val newDefender = playingField.getRoles.defender
//    val newAttacker = playingField.getRoles.attacker
//    // Check if the player is now the new defender
//    val isNowDefender = player == newDefender
//
//    if (!isNowDefender) {
//      resetSelectedDefender()
//    }
//
//    children.clear()
//    children.addAll(statusLabel, playerLabel, createDefenderRow(), createGoalkeeperRow())
//
////    playingField.notifyObservers() // ✅ Ensure UI refreshes
//  }
//
//  /** **Update game status dynamically** */
//  def updateGameStatus(): Unit = {
//    statusLabel.text = s"${playingField.getRoles.attacker.name} attacks ${playingField.getRoles.defender.name}!"
//  }
//}