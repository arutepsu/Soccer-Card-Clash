package view.components.gameComponents
import scalafx.animation._
import scalafx.scene.layout.HBox
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint.Color
import scalafx.util.Duration
import javafx.application.Platform
import scala.concurrent.{ExecutionContext, Future}
import model.cardComponent.Card
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.Includes._
import view.components.cardComponents.HandCard
import view.components.uiFactory.CardAnimationFactory
import scala.math._

import scala.concurrent.ExecutionContext.Implicits.global

class PlayersHandBar(player: Player, playingField: PlayingField, isLeftSide: Boolean) extends HBox {

  alignment = if (isLeftSide) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
  spacing = Math.max(-30 + (playingField.getHand(player).size * -2), -50)


  private var selectedCard: Option[HandCard] = None

  /** Label indicating whose hand this is */
  private val playerLabel = new Label {
    text = s"${player.name}'s Hand"
    style = "-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px; -fx-background-radius: 10px;"
  }

  /** Creates ImageViews for all Hand Cards with visual effects */
  private def createHandCardRow(): HBox = {
    val hand = playingField.getHand(player)
    val handCards = hand.zipWithIndex.map { case (card, index) =>
      val isLastCard = index == hand.size - 1
//      val handCard = new HandCard(flipped = !isLastCard, card = card)
      val handCard = new HandCard(flipped = false, card = card)
      // Drop shadow for depth
      handCard.effect = new DropShadow(10, Color.BLACK)

      // Hover effect
      handCard.onMouseEntered = (_: MouseEvent) => {
        val hoverEffect = new ScaleTransition(Duration(200), handCard)
        hoverEffect.toX = 1.15
        hoverEffect.toY = 1.15
        hoverEffect.play()
      }

      handCard.onMouseExited = (_: MouseEvent) => {
        val exitEffect = new ScaleTransition(Duration(200), handCard)
        exitEffect.toX = 1.0
        exitEffect.toY = 1.0
        exitEffect.play()
      }

      handCard
    }

    new HBox {
      alignment = Pos.CENTER
      spacing = -25 // More overlapping for a fanned-out look
      children = handCards
    }
  }

  children = Seq(playerLabel, createHandCardRow())

  /** Updates Hand UI with enhanced animations */
  def updateHand(): Unit = {
    println("🔄 Updating Hand UI with enhanced animation...")

    val oldChildren = children.tail // Exclude label
    val newHandRow = createHandCardRow()

    // Animate existing cards with a smoother transition
    oldChildren.foreach {
      case hbox: javafx.scene.layout.HBox =>
        hbox.getChildren.forEach { node =>
          val moveTransition = new TranslateTransition(Duration(500), node) // ⏳ Slower, more impactful
          moveTransition.byX = 25 // Move further
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
      case _ => // Ignore non-HBox elements
    }

    // Add new hand row after animation with fade-in effect
    Future {
      Thread.sleep(500) // Wait for transition
      Platform.runLater(() => {
        children.clear()
        children.addAll(playerLabel, newHandRow)

        // Apply fade-in effect for new cards
        newHandRow.children.foreach { node =>
          val fadeTransition = new FadeTransition(Duration(500), node)
          fadeTransition.fromValue = 0.0
          fadeTransition.toValue = 1.0
          fadeTransition.play()
        }

        selectedCard = CardAnimationFactory.highlightLastHandCard(player, playingField)

        playingField.notifyObservers()
      })
    }
  }
}
//package view.components.gameComponents
//import scalafx.scene.layout.HBox
//import scalafx.geometry.Pos
//import scalafx.scene.control.Label
//import scalafx.scene.input.MouseEvent
//import scalafx.scene.effect.DropShadow
//import scalafx.scene.paint.Color
//import scalafx.animation.{TranslateTransition, ScaleTransition}
//import scalafx.util.Duration
//import model.cardComponent.Card
//import model.playerComponent.Player
//import model.playingFiledComponent.PlayingField
//import scalafx.Includes._
//import view.components.cardComponents.HandCard
//import view.components.uiFactory.{CardAnimationFactory, CardImageLoader}
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import javafx.application.Platform
//
//class PlayersHandBar(player: Player, playingField: PlayingField, isLeftSide: Boolean) extends HBox {
//
//  alignment = if (isLeftSide) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
//  spacing = -30 // Tighter overlap for a sleeker look
//
//  private var selectedCard: Option[HandCard] = None
//
//  /** Label indicating whose hand this is */
//  private val playerLabel = new Label {
//    text = s"${player.name}'s Hand"
//    style = "-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px; -fx-background-radius: 10px;"
//  }
//
//  /** Creates ImageViews for all Hand Cards with visual effects */
//  private def createHandCardRow(): HBox = {
//    val hand = playingField.getHand(player)
//    val handCards = hand.zipWithIndex.map { case (card, index) =>
//      val isLastCard = index == hand.size - 1
//      val handCard = new HandCard(flipped = !isLastCard, card = card)
//
//      // Drop shadow for depth
//      handCard.effect = new DropShadow(10, Color.BLACK)
//
//      // Hover effect
//      handCard.onMouseEntered = (_: MouseEvent) => {
//        val hoverEffect = new ScaleTransition(Duration(150), handCard)
//        hoverEffect.toX = 1.1
//        hoverEffect.toY = 1.1
//        hoverEffect.play()
//      }
//
//      handCard.onMouseExited = (_: MouseEvent) => {
//        val exitEffect = new ScaleTransition(Duration(150), handCard)
//        exitEffect.toX = 1.0
//        exitEffect.toY = 1.0
//        exitEffect.play()
//      }
//
//      handCard
//    }
//
//    new HBox {
//      alignment = Pos.CENTER
//      spacing = -25 // More overlapping for a fanned-out look
//      children = handCards
//    }
//  }
//
//  children = Seq(playerLabel, createHandCardRow())
//
//  /** Updates Hand UI with animations for inserting new cards */
//  def updateHand(): Unit = {
//    println("🔄 Updating Hand UI with animation...")
//
//    val oldChildren = children.tail // Exclude label
//    val newHandRow = createHandCardRow()
//
//    // Ensure that we're filtering only HBox instances correctly
//    oldChildren.foreach {
//      case hbox: javafx.scene.layout.HBox => // Use fully qualified name
//        hbox.getChildren.forEach { node =>
//          val moveTransition = new TranslateTransition(Duration(300), node)
//          moveTransition.byX = 15 // Move right
//          moveTransition.play()
//        }
//      case _ => // Do nothing for non-HBox elements
//    }
//
//    // Add new hand row after animation
//    Future {
//      Thread.sleep(300) // Wait for transition
//      Platform.runLater(() => {
//        children.clear()
//        children.addAll(playerLabel, newHandRow)
//
//        selectedCard = CardAnimationFactory.highlightLastHandCard(player, playingField)
//
//        playingField.notifyObservers()
//      })
//    }
//  }
//
//}
//package view.components.gameComponents
//
//import scalafx.scene.layout.HBox
//import scalafx.geometry.Pos
//import scalafx.scene.control.Label
//import scalafx.scene.input.MouseEvent
//import scalafx.scene.effect.{DropShadow, GaussianBlur, Glow}
//import scalafx.scene.paint.Color
//import scalafx.animation.{TranslateTransition, ScaleTransition}
//import scalafx.util.Duration
//import model.cardComponent.Card
//import model.playerComponent.Player
//import model.playingFiledComponent.PlayingField
//import scalafx.Includes._
//import view.components.cardComponents.HandCard
//import view.components.uiFactory.{CardAnimationFactory, CardImageLoader} // ✅ Fix MouseEvent type mismatch
//
//class PlayersHandBar(player: Player, playingField: PlayingField, isLeftSide: Boolean) extends HBox {
//
//  alignment = if (isLeftSide) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
//  spacing = -30 // Tighter overlap for a sleeker look
//
//  private var selectedCard: Option[HandCard] = None
//
//  /** Label indicating whose hand this is */
//  private val playerLabel = new Label {
//    text = s"${player.name}'s Hand"
//    style = "-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px; -fx-background-radius: 10px;"
//  }
//
//  /** Creates ImageViews for all Hand Cards with visual effects */
//  private def createHandCardRow(): HBox = {
//    val hand = playingField.getHand(player)
//    val handCards = hand.zipWithIndex.map { case (card, index) =>
//      val isLastCard = index == hand.size - 1
//      val handCard = new HandCard(flipped = !isLastCard, card = card)
//
//      // Drop shadow for depth
//      handCard.effect = new DropShadow(10, Color.BLACK)
//
//      // Hover effect
//      handCard.onMouseEntered = (_: MouseEvent) => {
//        val hoverEffect = new ScaleTransition(Duration(150), handCard)
//        hoverEffect.toX = 1.1
//        hoverEffect.toY = 1.1
//        hoverEffect.play()
//      }
//
//      handCard.onMouseExited = (_: MouseEvent) => {
//        val exitEffect = new ScaleTransition(Duration(150), handCard)
//        exitEffect.toX = 1.0
//        exitEffect.toY = 1.0
//        exitEffect.play()
//      }
//
//      handCard
//    }
//
//    new HBox {
//      alignment = Pos.CENTER
//      spacing = -25 // More overlapping for a fanned-out look
//      children = handCards
//    }
//  }
//
//  children = Seq(playerLabel, createHandCardRow())
//
//  /** Updates Hand UI */
//  def updateHand(): Unit = {
//    println("🔄 Updating Hand UI...")
//    children.clear()
//    children.addAll(playerLabel, createHandCardRow())
//
//    selectedCard = CardAnimationFactory.highlightLastHandCard(player, playingField)
//
//    playingField.notifyObservers()
//  }
//}
