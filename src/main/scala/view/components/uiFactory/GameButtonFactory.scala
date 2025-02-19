package view.components.uiFactory

import scalafx.animation.TranslateTransition
import scalafx.scene.control.Button
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.util.Duration
import view.components.uiFactory.{ButtonAnimationFactory, CardAnimationFactory}
import view.utils.ImageUtils
object GameButtonFactory {

  private val defaultImagePath = "/images/data/buttons/button.png" // Default button image path

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
    val buttonImage = ImageUtils.importImage(imagePath)

    // Set background image
    button.background = new Background(Array(
      new BackgroundImage(
        buttonImage,
        BackgroundRepeat.NoRepeat,
        BackgroundRepeat.NoRepeat,
        BackgroundPosition.Center,
        new BackgroundSize(width, height, false, false, true, true)
      )
    ))

    // Optional: Adjust text style for visibility
    button.style = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"

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
    // Usage Example

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
