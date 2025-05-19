package de.htwg.se.soccercardclash.view.gui.components.sceneComponents
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import javafx.application.Platform
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import scalafx.Includes.*
import scalafx.animation.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{GridPane, HBox, Region, VBox}
import scalafx.scene.paint.Color
import scalafx.util.Duration
import de.htwg.se.soccercardclash.view.gui.components.cardView.{FieldCardFactory, HandCard}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.CardAnimationFactory
import de.htwg.se.soccercardclash.view.gui.utils.{CardImageRegistry, Styles}

trait FieldCardRenderer {
  def createDefenderRow(player: IPlayer, getGameState: () => IGameState): HBox
  def createGoalkeeperRow(player: IPlayer, getGameState: () => IGameState): HBox
}
class PlayersFieldBar(
                       val player: IPlayer,
                       getGameState: () => IGameState,
                       renderer: FieldCardRenderer
                     ) extends VBox {

  alignment = Pos.CENTER
  spacing = 10
  this.getStylesheets.add(Styles.playersFieldBarCss)
  styleClass.add("players-field-bar")

  private val playerLabel = new Label {
    text = s"${player.name}'s Field"
    styleClass.add("player-label")
  }

  private var currentDefenderRow: HBox = renderer.createDefenderRow(player, getGameState)
  private var currentGoalkeeperRow: HBox = renderer.createGoalkeeperRow(player, getGameState)

  children = Seq(playerLabel, currentDefenderRow, currentGoalkeeperRow)

  def selectedDefenderIndex: Option[Int] = renderer match {
    case selectable: SelectableFieldCardRenderer => selectable.getSelectedDefenderIndex
    case _ => None
  }

  def isGoalkeeperSelected: Boolean = renderer match {
    case selectable: SelectableFieldCardRenderer => selectable.isGoalkeeperSelected
    case _ => false
  }

  def updateBar(gameState: IGameState): Unit = {
    println(s"Updating PlayersFieldBar for ${player.name}...")
    val newDefenderRow = renderer.createDefenderRow(player, () => gameState)
    val newGoalkeeperRow = renderer.createGoalkeeperRow(player, () => gameState)

    children.clear()
    children.addAll(playerLabel, newDefenderRow, newGoalkeeperRow)

    currentDefenderRow = newDefenderRow
    currentGoalkeeperRow = newGoalkeeperRow
  }
  
  def resetSelectedDefender(): Unit = renderer match {
    case selectable: SelectableFieldCardRenderer => selectable.resetSelection()
    case _ => ()
  }

}
object DefaultFieldCardRenderer extends FieldCardRenderer {
  override def createDefenderRow(player: IPlayer, getGameState: () => IGameState): HBox = {
    val defenderCards = getGameState().getDataManager.getPlayerDefenders(player)
    val defenderCardNodes = defenderCards.map(card =>
      FieldCardFactory.createDefaultFieldCard(card)
    )
    new HBox { alignment = Pos.CENTER; spacing = 10; children = defenderCardNodes }
  }

  override def createGoalkeeperRow(player: IPlayer, getGameState: () => IGameState): HBox = {
    val goalkeeper = getGameState().getDataManager.getPlayerGoalkeeper(player)
    val fieldCard = FieldCardFactory.createDefaultFieldCard(goalkeeper)

    new HBox {
      alignment = Pos.CENTER
      spacing = 10
      padding = Insets(0, 0, 0, 0)
      children = Seq(fieldCard)
    }
  }
}

class SelectableFieldCardRenderer(getGameState: () => IGameState) extends FieldCardRenderer {

  private var selectedDefenderIndex: Option[Int] = None
  private var _isGoalkeeperSelected: Boolean = false

  private def handleCardSelected(clickedIndex: Int): Unit = {
    if (selectedDefenderIndex.contains(clickedIndex)) {
      println(s"Deselected: index $clickedIndex")
      selectedDefenderIndex = None
      _isGoalkeeperSelected = false
    } else {
      println(s"Selected: index $clickedIndex")
      selectedDefenderIndex = Some(clickedIndex)
      _isGoalkeeperSelected = clickedIndex == -1
    }
  }

  def getSelectedDefenderIndex: Option[Int] = selectedDefenderIndex
  def isGoalkeeperSelected: Boolean = _isGoalkeeperSelected


  override def createDefenderRow(player: IPlayer, getGameState: () => IGameState): HBox = {
    val defenderCards = getGameState().getDataManager
      .getPlayerDefenders(player)
      .padTo(3, None)

    val nodes = defenderCards.zipWithIndex.map { case (cardOpt, index) =>
      val desc = cardOpt.map(_.toString).getOrElse("Defeated")
      cardOpt match {
        case Some(card) =>
          FieldCardFactory.createSelectableFieldCard(
            card = Some(card),
            index = index,
            selectedIndex = selectedDefenderIndex,
            isGoalkeeper = false,
            onSelected = handleCardSelected
          )
        case None =>
          FieldCardFactory.createStaticImageCard(
            CardImageRegistry.getDefeatedImage(),
            index
          )
      }
    }

    new HBox {
      alignment = Pos.CENTER
      spacing = 10
      children = nodes
    }
  }



  override def createGoalkeeperRow(player: IPlayer, getGameState: () => IGameState): HBox = {
    val goalkeeper = getGameState().getDataManager.getPlayerGoalkeeper(player)
    val fieldCard = FieldCardFactory.createSelectableFieldCard(
      card = goalkeeper,
      index = -1,
      selectedIndex = selectedDefenderIndex,
      isGoalkeeper = true,
      onSelected = handleCardSelected
    )

    new HBox {
      alignment = Pos.CENTER
      spacing = 10
      padding = Insets(0, 0, 0, 0)
      children = Seq(fieldCard)
    }
  }


  def resetSelection(): Unit = {
    selectedDefenderIndex = None
    _isGoalkeeperSelected = false
  }
}

