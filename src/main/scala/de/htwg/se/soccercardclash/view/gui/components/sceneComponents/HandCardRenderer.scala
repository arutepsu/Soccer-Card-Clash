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
}

class PlayersHandBar(
                      player: IPlayer,
                      playingField: IGameState,
                      isLeftSide: Boolean,
                      renderer: HandCardRenderer
                    ) extends HBox {
  
  alignment = if (isLeftSide) Pos.CENTER_LEFT else Pos.CENTER_RIGHT

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

  private def handleCardSelected(clickedIndex: Int): Unit = {
    if (clickedIndex == -1) selectedCardIndex = None
    else selectedCardIndex = Some(clickedIndex)
  }

  override def createHandCardRow(player: IPlayer, dummyState: IGameState): HBox = {
    val gameState = getGameState()
    val hand = gameState.getDataManager.getPlayerHand(player)

    val handCards = hand.toList.zipWithIndex.map { case (card, index) =>
      val isLastCard = index == hand.toList.size - 1
      HandCardFactory.createSelectableHandCard(
        card = card,
        index = index,
        flipped = !isLastCard,
        selectedIndex = selectedCardIndex,
        onSelected = handleCardSelected
      )
    }

    val handSize = hand.toList.size
    new HBox {
      alignment = Pos.CENTER
      spacing = -Math.min(40, handSize * 6) // ✅ cleaner and increased spacing
      children = handCards
    }
  }
}



object DefaultHandCardRenderer extends HandCardRenderer {
  def createHandCardRow(player: IPlayer, gameState: IGameState): HBox = {
    val hand = gameState.getDataManager.getPlayerHand(player)
    val handCards = hand.toList.zipWithIndex.map { case (card, index) =>
      val isLastCard = index == hand.toList.size - 1
      val handCard = HandCard(
        flipped = !isLastCard,
        card = card,
        isLastCard = isLastCard
      )
      handCard.effect = new DropShadow(10, Color.BLACK)
      
      handCard.onMouseEntered = _ => {
        val hoverEffect = new ScaleTransition(Duration(200), handCard)
        hoverEffect.toX = 1.15
        hoverEffect.toY = 1.15
        hoverEffect.play()
      }
      handCard.onMouseExited = _ => {
        val exitEffect = new ScaleTransition(Duration(200), handCard)
        exitEffect.toX = 1.0
        exitEffect.toY = 1.0
        exitEffect.play()
      }

      handCard
    }

    val handSize = hand.toList.size
    new HBox {
      alignment = Pos.CENTER
      spacing = -Math.min(40, handSize * 6) // ✅ cleaner and increased spacing
      children = handCards
    }
  }
}

