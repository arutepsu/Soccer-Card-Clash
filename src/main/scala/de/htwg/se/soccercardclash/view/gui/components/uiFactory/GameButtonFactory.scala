package de.htwg.se.soccercardclash.view.gui.components.uiFactory

import scalafx.animation.TranslateTransition
import scalafx.scene.control.Button
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.util.Duration
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.{ButtonAnimationFactory, CardAnimationFactory}
import de.htwg.se.soccercardclash.view.gui.utils.ImageUtils
object GameButtonFactory {

  private val defaultImagePath = "/images/data/buttons/button (1).png" // Default button image path

  def createGameButton(
                        text: String,
                        width: Double,
                        height: Double,
                        imagePath: String = defaultImagePath // Default to predefined image
                      )(onFinishedAction: () => Unit): Button = {
    val button = new Button(text)
    button.prefWidth = width
    button.prefHeight = height

    // Load the button image (use default if not provided)
    val imageUrl = Option(getClass.getResource(imagePath))
      .map(_.toExternalForm)
      .getOrElse {
        println(s"Error: Image not found at $imagePath")
        "" // Fallback to empty string if image is not found
      }

    button.style =
      s"""-fx-background-image: url('$imageUrl');
         |-fx-background-size: 200% 200%; /* Ensures image fully covers button */
         |-fx-background-repeat: no-repeat; /* Prevents repeating */
         |-fx-background-position: center; /* Centers image */
         |-fx-background-color: transparent; /* ✅ Removes white background */
         |
         |-fx-text-fill: white;
         |-fx-font-size: 16px;
         |-fx-font-weight: bold;
         |-fx-text-alignment: center;
         |-fx-padding: 0; /* ✅ Ensures no extra padding that might cause white border */
         |""".stripMargin


    val translateDownAnimation: TranslateTransition = ButtonAnimationFactory.createButtonAnimation(button, 1, 10)
    val translateUpAnimation: TranslateTransition = ButtonAnimationFactory.createButtonAnimation(button, -1, -10)

    // ✅ Hover Animation (Moves Slightly Up on Hover)
    val hoverEnterAnimation: TranslateTransition = ButtonAnimationFactory.createTranslationAnimation(
      node = button,
      deltaX = 0,
      deltaY = -5,  // Moves up by 5px
      durationMillis = 100,
      cycleCount = 1,
      autoReverse = false
    )

    val hoverExitAnimation: TranslateTransition = ButtonAnimationFactory.createTranslationAnimation(
      node = button,
      deltaX = 0,
      deltaY = 5,  // Moves back down
      durationMillis = 100,
      cycleCount = 1,
      autoReverse = false
    )

    // ✅ Setup Hover Effects
    ButtonAnimationFactory.setupButtonHoverEffects(button, hoverEnterAnimation, hoverExitAnimation)

    // ✅ Ensure Button Resets Correctly After Animation
    translateUpAnimation.onFinished = _ => {
      button.translateX = 0
      button.translateY = 0
      if (button.isHover) {
        onFinishedAction()
      }
    }

    // ✅ Handle Mouse Press and Release Properly
    button.onMousePressed = _ => {
      translateDownAnimation.playFromStart()
    }

    button.onMouseReleased = _ => {
      translateUpAnimation.playFromStart()
    }

    button
  }
}
