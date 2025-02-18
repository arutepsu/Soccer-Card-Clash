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
//    // ✅ DropShadow Effect for Hover
//    val shadowEffect = new DropShadow()
//    shadowEffect.color = Color.Gray
//    shadowEffect.radius = 10
//    shadowEffect.offsetX = 5
//    shadowEffect.offsetY = 5
//    // ✅ Handle Hover Effects (Now Resets Before Playing)
//    button.onMouseEntered = _ => {
//      if (button.translateY.value == 0) { // ✅ Fix: Extract value from DoubleProperty
//        button.effect = shadowEffect
//        hoverEnterAnimation.playFromStart()
//      }
//    }
//
//    button.onMouseExited = _ => {
//      if (button.translateY.value == -5) { // ✅ Fix: Extract value from DoubleProperty
//        button.effect = null
//        hoverExitAnimation.playFromStart()
//      }
//    }
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

//package view.components
//
//import scalafx.animation.TranslateTransition
//import scalafx.scene.control.Button
//import scalafx.scene.image.ImageView
//import scalafx.scene.layout.{Background, BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize}
//import scalafx.util.Duration
//import view.components.uiFactory.AnimationFactory
//import view.utils.ImageUtils
//
//object GameButtonFactory {
//
//  private val defaultImagePath = "/view/data/buttons/button.png" // Default button image path
//
//  def createGameButton(
//                        text: String,
//                        width: Double,
//                        height: Double,
//                        imagePath: String = defaultImagePath // Default to predefined image
//                      )(onFinishedAction: () => Unit): Button = {
//    val button = new Button(text)
//    button.prefWidth = width
//    button.prefHeight = height
//
//    // Load the button image (use default if not provided)
//    val buttonImage = ImageUtils.importImage(imagePath)
//
//    // Set background image
//    button.background = new Background(Array(
//      new BackgroundImage(
//        buttonImage,
//        BackgroundRepeat.NoRepeat,
//        BackgroundRepeat.NoRepeat,
//        BackgroundPosition.Center,
//        new BackgroundSize(width, height, false, false, true, true)
//      )
//    ))
//
//    // Optional: Adjust text style for visibility
//    button.style = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"
//
//    // ✅ Use AnimationFactory for Correct Animations
//    val translateDownAnimation: TranslateTransition = AnimationFactory
//      .createTranslationAnimation(
//        node = button,
//        deltaX = 1,
//        deltaY = 10,
//        durationMillis = 15,
//        cycleCount = 1,
//        autoReverse = false
//      )
//
//    val translateUpAnimation: TranslateTransition = AnimationFactory
//      .createTranslationAnimation(
//        node = button,
//        deltaX = -1,
//        deltaY = -10,
//        durationMillis = 15,
//        cycleCount = 1,
//        autoReverse = false
//      )
//
//    // ✅ Ensure Button Resets Correctly After Animation
//    translateUpAnimation.onFinished = _ => {
//      button.translateX = 0
//      button.translateY = 0
//      if (button.isHover) {
//        onFinishedAction()
//      }
//    }
//
//    // ✅ Handle Mouse Press and Release Properly
//    var mouseStillOnButton = false
//
//    button.onMousePressed = _ => {
//      mouseStillOnButton = true
//      translateDownAnimation.playFromStart()
//    }
//
//    button.onMouseReleased = _ => {
//      mouseStillOnButton = false
//      translateUpAnimation.playFromStart()
//    }
//
//    button
//  }
//}
//package view.components
//
//import scalafx.animation.TranslateTransition
//import scalafx.scene.control.Button
//import scalafx.util.Duration
//import scalafx.scene.effect.DropShadow
//import scalafx.scene.input.MouseEvent
//import scalafx.scene.paint.Color
//import javafx.event.EventHandler
//import view.components.uiFactory.AnimationFactory
//
//object GameButtonFactory {
//  def createGameButton(
//                        text: String,
//                        width: Double,
//                        height: Double
//                      )(onFinishedAction: () => Unit): Button = {
//    val button = new Button(text)
//    button.prefWidth = width
//    button.prefHeight = height
//
//    val translateDownAnimation: TranslateTransition = AnimationFactory
//      .createTranslationAnimation(
//        node = button,
//        deltaX = 1,
//        deltaY = 10,
//        durationMillis = 15,
//        cycleCount = 1,
//        autoReverse = false
//      )
//
//    val translateUpAnimation: TranslateTransition = AnimationFactory
//      .createTranslationAnimation(
//        node = button,
//        deltaX = -1,
//        deltaY = -10,
//        durationMillis = 15,
//        cycleCount = 1,
//        autoReverse = false
//      )
//
//    /*     val shadow = new DropShadow()
//    shadow.color = Color.Gray
//    shadow.radius = 5
//    shadow.offsetX = 3
//    shadow.offsetY = 3
//    button.effect = shadow */
//
//    // button.onAction = _ => translateAnimation.playFromStart()
//
//    translateUpAnimation.onFinished = _ => {
//      button.translateX = 0 // Reset X position
//      button.translateY = 0 // Reset Y position
//      if (button.isHover) {
//        onFinishedAction()
//      }
//    }
//
//    var mouseStillOnButton = false
//
//    button.onMousePressed = _ => {
//      mouseStillOnButton = true
//      translateDownAnimation.playFromStart()
//    }
//
//    button.onMouseReleased = _ => {
//      mouseStillOnButton = false
//      translateUpAnimation.playFromStart()
//    }
//
//    /*     button.onMousePressed = _ => translateDownAnimation.playFromStart()
//    button.onMouseReleased = _ => translateUpAnimation.playFromStart()
//    translateUpAnimation.onFinished = _ => onFinishedAction */
//
//    button
//  }
//}