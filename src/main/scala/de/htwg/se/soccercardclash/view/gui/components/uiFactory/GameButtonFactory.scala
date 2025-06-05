package de.htwg.se.soccercardclash.view.gui.components.uiFactory

import scalafx.animation.TranslateTransition
import scalafx.animation.{KeyFrame, KeyValue, Timeline}
import scalafx.util.Duration
import scalafx.scene.control.Button
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.{ButtonAnimationFactory, CardAnimationFactory}
import de.htwg.se.soccercardclash.view.gui.utils.BoostImage
import scalafx.scene.text.Font
import javafx.event.ActionEvent
import javafx.event.EventHandler
object GameButtonFactory {

  private val defaultImagePath = "/images/data/buttons/button.png"

  def createGameButton(
                        text: String,
                        width: Double,
                        height: Double,
                        imagePath: String = defaultImagePath
                      )(onFinishedAction: () => Unit): Button = {
    val button = new Button(text)
    button.prefWidth = width
    button.prefHeight = height

    val imageUrl = Option(getClass.getResource(imagePath))
      .map(_.toExternalForm)
      .getOrElse {
        println(s"Error: Image not found at $imagePath")
        ""
      }

    button.style =
      s"""-fx-background-image: url('$imageUrl');
         |-fx-background-size: 100% 200%;
         |-fx-background-repeat: no-repeat;
         |-fx-background-position: center center;
         |-fx-background-color: transparent;
         |
         |-fx-text-fill: white;
         |-fx-font-size: 20px;
         |-fx-font-family: "Rajdhani";
         |-fx-font-weight: regular;
         |-fx-text-alignment: center;
         |-fx-alignment: center;
         |-fx-content-display: center;
         |-fx-padding: 0;
         |""".stripMargin


    button.effect = new DropShadow {
      radius = 4
      color = Color.web("rgba(158, 75, 223, 0.8)")
      spread = 0.3
      offsetY = 1
    }

    val translateDownAnimation: TranslateTransition = ButtonAnimationFactory.createButtonAnimation(button, 1, 10)
    val translateUpAnimation: TranslateTransition = ButtonAnimationFactory.createButtonAnimation(button, -1, -10)

    val hoverEnterAnimation: TranslateTransition = ButtonAnimationFactory.createTranslationAnimation(
      node = button,
      deltaX = 0,
      deltaY = -5,
      durationMillis = 100,
      cycleCount = 1,
      autoReverse = false
    )

    val hoverExitAnimation: TranslateTransition = ButtonAnimationFactory.createTranslationAnimation(
      node = button,
      deltaX = 0,
      deltaY = 5,
      durationMillis = 100,
      cycleCount = 1,
      autoReverse = false
    )

    ButtonAnimationFactory.setupButtonHoverEffects(button, hoverEnterAnimation, hoverExitAnimation)

    translateUpAnimation.onFinished = _ => {
      button.translateX = 0
      button.translateY = 0
      if (button.isHover) {
        onFinishedAction()
      }
    }

    button.onMousePressed = _ => {
      translateDownAnimation.playFromStart()
    }

    button.onMouseReleased = _ => {
      translateUpAnimation.playFromStart()
    }

    button
  }

  def applyGlitchEffect(button: Button): Unit = {
    val glitchTimeline = new Timeline {
      cycleCount = Timeline.Indefinite
      autoReverse = false
      keyFrames = Seq(
        KeyFrame(Duration(0), null, null, Set(
          KeyValue(button.translateX, 0),
          KeyValue(button.rotate, 0),
          KeyValue(button.opacity, 1)
        )),
        KeyFrame(Duration(50), null, null, Set(
          KeyValue(button.translateX, 3),
          KeyValue(button.rotate, 2),
          KeyValue(button.opacity, 0.9)
        )),
        KeyFrame(Duration(100), null, null, Set(
          KeyValue(button.translateX, -3),
          KeyValue(button.rotate, -2),
          KeyValue(button.opacity, 1)
        )),
        KeyFrame(Duration(150), null, null, Set(
          KeyValue(button.translateX, 2),
          KeyValue(button.rotate, 1),
          KeyValue(button.opacity, 0.95)
        )),
        KeyFrame(Duration(200), null, null, Set(
          KeyValue(button.translateX, 0),
          KeyValue(button.rotate, 0),
          KeyValue(button.opacity, 1)
        ))
      )
    }

    button.onMouseEntered = _ => glitchTimeline.playFromStart()
    button.onMouseExited = _ => {
      glitchTimeline.stop()
      button.translateX = 0
      button.rotate = 0
      button.opacity = 1
    }
  }
}