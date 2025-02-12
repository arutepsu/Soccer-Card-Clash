package view.components.uiFactory

import javafx.event.EventHandler
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.Includes.*
import scalafx.animation.{ScaleTransition, TranslateTransition, FadeTransition, StrokeTransition}
import scalafx.scene.control.Button
import scalafx.scene.effect.{DropShadow, Glow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.util.Duration
import view.components.cardComponents.{FieldCard, HandCard}
import scalafx.animation.{ScaleTransition, Timeline, KeyFrame, PauseTransition}
import scalafx.scene.effect.Glow
import scalafx.util.Duration
import scalafx.scene.image.ImageView
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{StackPane, Pane}
import view.utils.ImageUtils
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import view.components.uiFactory.CardImageLoader
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane
import scalafx.scene.effect.DropShadow
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import view.components.cardComponents.GameCard

object CardAnimationFactory {
  private val boostEffectPath = "/view/data/cards/effects/boost.png"

  /**
   * Adds a boost effect overlay if the card has an additional value (>0).
   */


//  def applyBoostEffect(card: FieldCard): Unit = {
//    if (card.card.additionalValue > 0) {
//      val scaleFactor = 0.2f // Adjust scale factor for the boost effect
//      val boostView = ImageUtils.importImageAsViewBoost(boostEffectPath, scaleFactor, 30, 50) // Max 100x100 size
//
//      // Positioning the boost effect relative to the card
//      boostView.translateX = card.width.value * 0.4 // Move slightly to the right
//      boostView.translateY = -card.height.value * 0.4 // Move above the card
//
//      Platform.runLater {
//        card.parent.value match {
//          case javafxParent: javafx.scene.layout.Pane =>
//            val parentChildren = javafxParent.getChildren
//
//            if (parentChildren.contains(card)) {
//              parentChildren.remove(card)
//
//              val cardStack = new StackPane {
//                children = Seq(card, boostView) // ✅ Directly overlay the boost on the original card
//                prefWidth = card.width.value
//                prefHeight = card.height.value
//              }
//
//              parentChildren.add(cardStack)
//            } else {
//              println("⚠️ [ERROR] Card was not found in parent container!")
//            }
//
//          case _ =>
//            println("⚠️ [ERROR] Card has no valid parent container!")
//        }
//      }
//    }
//  }
  def applyBoostEffect(card: FieldCard): Unit = {
    if (card.card.additionalValue > 0) {
      val scaleFactor = 0.2f // Adjust scale factor for the boost effect
      val boostView = ImageUtils.importImageAsViewBoost(boostEffectPath, scaleFactor, 30, 50) // Max 100x100 size

      // Positioning the boost effect relative to the card
      boostView.translateX = card.width.value * 0.4 // Move slightly to the right
      boostView.translateY = -card.height.value * 0.4 // Move above the card

      Platform.runLater {
        val parentOpt = Option(card.parent.value) // Ensure parent exists

        parentOpt match {
          case Some(javafxParent: javafx.scene.layout.Pane) =>
            val parentChildren = javafxParent.getChildren
            val originalIndex = parentChildren.indexOf(card) // ✅ Save original position

            if (originalIndex != -1) {
              parentChildren.remove(card)

              // ✅ StackPane ensures proper layering
              val cardStack = new StackPane {
                children = Seq(card, boostView) // ✅ Overlay boost effect
                prefWidth = card.width.value
                prefHeight = card.height.value
              }

              // ✅ Insert the boosted card at the same index
              parentChildren.add(originalIndex, cardStack)
            } else {
              println("⚠️ [ERROR] Card was not found in parent container!")
            }

          case _ =>
            println("⚠️ [ERROR] Card has no valid parent container!")
        }
      }
    }
  }

  /**
   * Creates and applies a glowing fire effect to a boosted card.
   *
   * @param card The card to which the fire effect should be applied.
   */
//  def createFireEffect(card: FieldCard): Unit = {
//    // 🔥 Create a Gold glow effect
//    val glowEffect = new DropShadow {
//      color = Color.Gold
//      radius = 90
//      spread = 0.8
//    }
//
//    // 🔥 Animate the glow intensity (pulsing effect)
//    val glowAnimation = new FadeTransition(Duration(500), card) {
//      fromValue = 0.6
//      toValue = 1.0
//      cycleCount = FadeTransition.Indefinite
//      autoReverse = true
//    }
//
//    // 🔥 Animate the entire card's brightness (pulsing effect)
//    val cardFadeAnimation = new FadeTransition(Duration(500), card) {
//      fromValue = 0.3 // Dimmed gold
//      toValue = 2.0 // Bright gold
//      cycleCount = FadeTransition.Indefinite
//      autoReverse = true
//    }
//
//    // Apply the effect to the card
//    card.effect = glowEffect
//    glowAnimation.play()
//    cardFadeAnimation.play() // 🔥 The whole card now blinks in sync with the glow!
//  }

  def createFireEffect(card: FieldCard): Unit = {
    Platform.runLater {
      card.parent.value match {
        case javafxParent: javafx.scene.layout.Pane =>
          val parentChildren = javafxParent.getChildren

          // 🔥 Load burn effect
          val burnEffect = CardImageLoader.getBurnEffectView(80, 120)

          // 🔥 Animate burn effect
          val burnAnimation = new FadeTransition(Duration(500), burnEffect) {
            fromValue = 0.5
            toValue = 1.0
            cycleCount = FadeTransition.Indefinite
            autoReverse = true
          }

          // Start animation
          burnAnimation.play()

          // ✅ Instead of replacing the card, just overlay the effect
          burnEffect.setLayoutX(card.getLayoutX)
          burnEffect.setLayoutY(card.getLayoutY)
          parentChildren.add(burnEffect) // Add effect without removing card

        case _ =>
          println("⚠️ [ERROR] Card has no valid parent container!")
      }
    }
  }


  //  def createFireEffect(card: FieldCard): Unit = {
//    Platform.runLater {
//      card.parent.value match {
//        case javafxParent: javafx.scene.layout.Pane =>
//          val parentChildren = javafxParent.getChildren
//
//          if (parentChildren.contains(card)) {
//            parentChildren.remove(card)
//
//            // 🔥 Create a blinking border frame
//            val blinkingFrame = new Rectangle {
//              width = card.width.value
//              height = card.height.value
//              arcWidth = 22  // 🔥 Rounded corners for a smooth look
//              arcHeight = 18
//              stroke = Color.Gold  // Initial color
//              strokeWidth = 4
//              fill = Color.Transparent // Only the frame should be visible
//            }
//
//            // 🔥 Animate the border color (blinking effect)
//            val borderAnimation = new StrokeTransition(Duration(500), blinkingFrame) {
//              fromValue = Color.Gold
//              toValue = Color.Orange
//              cycleCount = StrokeTransition.Indefinite
//              autoReverse = true
//            }
//
//            // 🔥 Slight pulsing effect (thickness change)
//            val fadeEffect = new FadeTransition(Duration(500), blinkingFrame) {
//              fromValue = 0.7
//              toValue = 1.0
//              cycleCount = FadeTransition.Indefinite
//              autoReverse = true
//            }
//
//            // Start animations
//            borderAnimation.play()
//            fadeEffect.play()
//
//            val cardStack = new StackPane {
//              children = Seq(card: scalafx.scene.Node, blinkingFrame: scalafx.scene.Node) // ✅ Explicitly cast to Node
//              prefWidth = card.width.value
//              prefHeight = card.height.value
//            }
//
//            parentChildren.add(cardStack)
//          } else {
//            println("⚠️ [ERROR] Card was not found in parent container!")
//          }
//
//        case _ =>
//          println("⚠️ [ERROR] Card has no valid parent container!")
//      }
//    }
//  }
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

  /** Stops the hover effect when the mouse leaves */
  def removeHoverEffect(card: GameCard, selectedIndex: Option[Int], index: Int): Unit = {
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