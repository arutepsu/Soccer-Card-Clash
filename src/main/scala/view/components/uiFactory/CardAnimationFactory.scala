package view.components.uiFactory

import javafx.event.EventHandler
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.Includes.*
import scalafx.animation.{ScaleTransition, TranslateTransition}
import scalafx.scene.control.Button
import scalafx.scene.effect.{DropShadow, Glow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.util.Duration
import view.components.cardComponents.{FieldCard, HandCard}
import scalafx.animation.{ScaleTransition, Timeline, KeyFrame, PauseTransition}
import scalafx.scene.effect.Glow
import scalafx.util.Duration
object CardAnimationFactory {
  
  def highlightLastHandCard(player: Player, playingField: PlayingField): Option[HandCard] = {
    val hand = playingField.getHand(player)

    if (hand.nonEmpty) {
      val newLastCard = hand.last
      val selectedCard = Some(new HandCard(flipped = false, card = newLastCard))

      selectedCard.foreach { cardView =>
        // Apply gold glow effect
        cardView.effect = new DropShadow(20, Color.GOLD)
        cardView.effect = new Glow(0.8)

        // Scale animation to highlight the card
        val selectAnimation = new ScaleTransition(Duration(200), cardView)
        selectAnimation.toX = 1.2
        selectAnimation.toY = 1.2
        selectAnimation.play()
      }

      selectedCard
    } else {
      None
    }
  }

//  def applyHoverEffect(card: FieldCard, selectedIndex: Option[Int], index: Int): Unit = {
//    if (!selectedIndex.contains(index)) { // Do not apply if selected
//      card.effect = new Glow(0.8)
//
//      val zoomIn = new ScaleTransition(Duration(200), card)
//      zoomIn.toX = 1.2
//      zoomIn.toY = 1.2
//      zoomIn.play()
//    }
//  }
//
//  /** Removes the hover effect (glow & zoom-out) when the mouse leaves */
//  def removeHoverEffect(card: FieldCard, selectedIndex: Option[Int], index: Int): Unit = {
//    if (!selectedIndex.contains(index)) { // Do not remove selection shadow
//      card.effect = null
//
//      val zoomOut = new ScaleTransition(Duration(200), card)
//      zoomOut.toX = 1.0
//      zoomOut.toY = 1.0
//      zoomOut.play()
//    }
//  }
  /** Applies a hover effect with automatic fade-out after some time */

  /** Applies a hover effect that loops while the cursor is over the card */
  def applyHoverEffect(card: FieldCard, selectedIndex: Option[Int], index: Int): Unit = {
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

  /** Stops the hover effect when the mouse leaves */
  def removeHoverEffect(card: FieldCard, selectedIndex: Option[Int], index: Int): Unit = {
    if (!selectedIndex.contains(index)) { // Do not remove if selected
      // Stop ongoing animations
      card.userData match {
        case animation: ScaleTransition => animation.stop()
        case _ =>
      }

      // Reset stored animation to prevent restarting
      card.userData = null

      // Reset the card effect
      card.effect = null

      val zoomOut = new ScaleTransition(Duration(200), card)
      zoomOut.toX = 1.0
      zoomOut.toY = 1.0
      zoomOut.play()
    }
  }

  /** Handles goalkeeper selection and deselection */
  def toggleGoalkeeperSelection(goalkeeperCard: FieldCard, selectedIndex: Option[Int]): Option[Int] = {
    selectedIndex match {
      case Some(_) =>
        // Deselect goalkeeper
        println(s"❌ Deselected goalkeeper: ${goalkeeperCard.card}")
        goalkeeperCard.effect = null
        None

      case None =>
        // Select goalkeeper
        println(s"🛡️ Selected goalkeeper: ${goalkeeperCard.card}")
        goalkeeperCard.effect = new DropShadow(20, Color.GOLD) // Apply gold shadow
        Some(0) // Since only one goalkeeper exists, we use index 0
    }
  }


}