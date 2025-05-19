package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.components.cardView.HandCardFactory
import scalafx.application.Platform
import scalafx.Includes.*
import scalafx.geometry.Pos
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import de.htwg.se.soccercardclash.view.gui.components.cardView.HandCard
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.CardAnimationFactory
import scalafx.animation.{FadeTransition, Interpolator, RotateTransition, ScaleTransition, TranslateTransition}
import scalafx.scene.control.Label
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.util.Duration

trait HandCardRenderer {
  def createHandCardRow(player: IPlayer, gameState: IGameState): HBox

  def calcHandSpacing(handSize: Int): Double =
    -Math.min(40, handSize * 6)
}

class PlayersHandBar(
                      player: IPlayer,
                      playingField: IGameState,
                      renderer: HandCardRenderer
                    ) extends HBox {
  
  alignment = Pos.Center

  val handSize = playingField.getDataManager.getPlayerHand(player).getHandSize
  spacing = -Math.min(80, handSize * 55) // more cards → higher negative spacing
  

  private var selectedCard: Option[HandCard] = None

  def selectedCardIndex: Option[Int] = renderer match {
    case selectable: SelectableHandCardRenderer => selectable.getSelectedIndex
    case _ => None
  }
  
  private val playerLabel = new Label {
    text = s"${player.name}'s Hand"
    style =
      "-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px; -fx-background-radius: 10px;"
  }
  
  private var currentHandRow: HBox = renderer.createHandCardRow(player, playingField)
  
  children = Seq(playerLabel, currentHandRow)
  
  def updateBar(newGameState: IGameState): Unit = {

    val newHandRow = renderer.createHandCardRow(player, newGameState)

    val oldRow = currentHandRow
    
    oldRow.getChildren.forEach { node =>
      val moveTransition = new TranslateTransition(Duration(500), node)
      moveTransition.byX = 25
      moveTransition.interpolator = Interpolator.EaseOut

      val scaleTransition = new ScaleTransition(Duration(400), node)
      scaleTransition.toX = 1.0
      scaleTransition.toY = 1.1
      scaleTransition.autoReverse = true

      val rotateTransition = new RotateTransition(Duration(400), node)
      rotateTransition.byAngle = 8
      rotateTransition.autoReverse = true

      moveTransition.play()
      scaleTransition.play()
      rotateTransition.play()
    }
    
    Future {
      Thread.sleep(500)
      Platform.runLater(() => {
        children.set(1, newHandRow)
        currentHandRow = newHandRow
        val handSize = newGameState.getDataManager.getPlayerHand(player).getHandSize
        spacing = -Math.min(80, handSize * 15)

        newHandRow.getChildren.forEach { node =>
          val fadeTransition = new FadeTransition(Duration(500), node)
          fadeTransition.fromValue = 0.0
          fadeTransition.toValue = 1.0
          fadeTransition.play()
        }

        selectedCard = CardAnimationFactory.highlightLastHandCard(player, newGameState)
      })
    }
  }
}

class SelectableHandCardRenderer(getGameState: () => IGameState) extends HandCardRenderer {
  var selectedCardIndex: Option[Int] = None

  def getSelectedIndex: Option[Int] = selectedCardIndex

  private def handleCardSelected(clickedIndex: Int): Unit =
    selectedCardIndex = if (clickedIndex == -1) None else Some(clickedIndex)
  override def createHandCardRow(player: IPlayer, dummyState: IGameState): HBox = {
    val handList = getGameState().getDataManager.getPlayerHand(player).toList
    val handCards = handList.zipWithIndex.map { case (card, index) =>
      HandCardFactory.createSelectableHandCard(
        card = card,
        index = index,
        flipped = index != handList.size - 1,
        selectedIndex = selectedCardIndex,
        onSelected = handleCardSelected
      )
    }

    new HBox {
      alignment = Pos.CENTER
      spacing = calcHandSpacing(handList.size)
      children = handCards
    }
  }
}




object DefaultHandCardRenderer extends HandCardRenderer {
  def createHandCardRow(player: IPlayer, gameState: IGameState): HBox = {
    val handList = gameState.getDataManager.getPlayerHand(player).toList

    val handCards = handList.zipWithIndex.map { case (card, index) =>
      val isLast = index == handList.size - 1
      val handCard = HandCard(
        flipped = !isLast,
        card = card,
        isLastCard = isLast
      )
      handCard.effect = new DropShadow(10, Color.BLACK)

      handCard.onMouseEntered = _ => {
        val hover = new ScaleTransition(Duration(200), handCard)
        hover.toX = 1.15
        hover.toY = 1.15
        hover.play()
      }
      handCard.onMouseExited = _ => {
        val exit = new ScaleTransition(Duration(200), handCard)
        exit.toX = 1.0
        exit.toY = 1.0
        exit.play()
      }

      handCard
    }

    new HBox {
      alignment = Pos.CENTER
      spacing = calcHandSpacing(handList.size)
      children = handCards
    }
  }
}

