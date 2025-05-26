package de.htwg.se.soccercardclash.view.gui.components.uiFactory

import javafx.event.EventHandler
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import scalafx.Includes.*
import scalafx.animation.{FadeTransition, ScaleTransition, StrokeTransition, TranslateTransition}
import scalafx.scene.control.Button
import scalafx.scene.effect.{DropShadow, Glow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.util.Duration
import de.htwg.se.soccercardclash.view.gui.components.cardView.{FieldCard, HandCard}
import scalafx.animation.{KeyFrame, PauseTransition, ScaleTransition, Timeline}
import scalafx.scene.effect.Glow
import scalafx.util.Duration
import scalafx.scene.image.ImageView
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Pane, StackPane}
import de.htwg.se.soccercardclash.view.gui.utils.BoostImage
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.BoostLoader
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane
import scalafx.scene.effect.DropShadow
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import de.htwg.se.soccercardclash.view.gui.components.cardView.GameCard

object CardAnimationFactory {
  
  def applyBoostEffect(card: FieldCard): Unit = {
    val scaleFactor = 0.2f
    val boostImage = BoostImage.getBoostImage

    val boostView = new ImageView(boostImage) {
      preserveRatio = true
      fitWidth = Math.min(boostImage.width.value * scaleFactor, 100)
      fitHeight = Math.min(boostImage.height.value * scaleFactor, 120)
    }

    boostView.translateX = card.width.value * 0.4
    boostView.translateY = -card.height.value * 0.4


      boostView.translateX = card.width.value * 0.4
      boostView.translateY = -card.height.value * 0.4

      Platform.runLater {
        val parentOpt = Option(card.parent.value)

        parentOpt match {
          case Some(javafxParent: javafx.scene.layout.Pane) =>
            val parentChildren = javafxParent.getChildren
            val originalIndex = parentChildren.indexOf(card)

            if (originalIndex != -1) {
              parentChildren.remove(card)

              val cardStack = new StackPane {
                children = Seq(card, boostView)
                prefWidth = card.width.value
                prefHeight = card.height.value
              }

              parentChildren.add(originalIndex, cardStack)
            } else {
              println("Card was not found in parent container!")
            }

          case _ =>
            println("Card has no valid parent container!")
        }
      }
  }

  def highlightLastHandCard(player: IPlayer, playingField: IGameState): Option[HandCard] = {
    val handCards = playingField.getGameCards.getPlayerHand(player).toList

    handCards.lastOption.map { lastCard =>
      val cardView = new HandCard(flipped = false, card = lastCard)

      cardView.effect = new DropShadow(20, Color.GOLD)
      cardView.effect = new Glow(0.8)

      val selectAnimation = new ScaleTransition(Duration(200), cardView)
      selectAnimation.toX = 1.2
      selectAnimation.toY = 1.2
      selectAnimation.play()

      cardView
    }
  }

  def applyHoverEffect(card: GameCard, selectedIndex: Option[Int], index: Int): Unit = {
    if (!selectedIndex.contains(index)) { // Do not apply if selected

      // Ensure any previous animation is stopped before starting a new one
      removeHoverEffect(card, selectedIndex, index)

      card.effect = new Glow(0.8)

      val zoomIn = new ScaleTransition(Duration(200), card)
      zoomIn.toX = 1.2
      zoomIn.toY = 1.2

      val zoomOut = new ScaleTransition(Duration(200), card)
      zoomOut.toX = 1.0
      zoomOut.toY = 1.0

      val pause = new PauseTransition(Duration(200)) // Short pause before repeating

      // Sequence: Zoom In -> Pause -> Zoom Out -> Pause -> Repeat only if mouse is still on the card
      zoomIn.setOnFinished(_ => if (card.userData == zoomIn) pause.play())
      pause.setOnFinished(_ => if (card.userData == zoomIn) zoomOut.play())
      zoomOut.setOnFinished(_ => if (card.userData == zoomIn) applyHoverEffect(card, selectedIndex, index)) // Restart if mouse is still there

      // Store animation reference in userData
      card.userData = zoomIn
      zoomIn.play()
    }
  }
  def removeHoverEffect(card: GameCard, selectedIndex: Option[Int], index: Int): Unit = {
    if (!selectedIndex.contains(index)) {

      card.userData match {
        case animation: ScaleTransition => animation.stop()
        case _ =>
      }
      card.userData = Option.empty[Any]
      card.effect = Option.empty[javafx.scene.effect.Effect].orNull

      val zoomOut = new ScaleTransition(Duration(200), card)
      zoomOut.toX = 1.0
      zoomOut.toY = 1.0
      zoomOut.play()
    }
  }

}