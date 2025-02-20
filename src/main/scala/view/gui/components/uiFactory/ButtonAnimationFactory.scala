package view.gui.components.uiFactory

import javafx.event.EventHandler
import model.playerComponent.base.Player
import model.playingFiledComponent.base.PlayingField
import scalafx.Includes.*
import scalafx.animation.{ScaleTransition, TranslateTransition}
import scalafx.scene.control.Button
import scalafx.scene.effect.{DropShadow, Glow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.util.Duration
import scalafx.scene.Node
import view.gui.components.cardView.{FieldCard, HandCard}
object ButtonAnimationFactory {
  def createTranslationAnimation(
                                  node: Button,
                                  deltaX: Double,
                                  deltaY: Double,
                                  durationMillis: Double,
                                  cycleCount: Int,
                                  autoReverse: Boolean
                                ): TranslateTransition = {
    val translateTransition =
      new TranslateTransition(Duration(durationMillis), node)
    translateTransition.byX = deltaX
    translateTransition.byY = deltaY
    translateTransition.cycleCount = cycleCount
    translateTransition.autoReverse = autoReverse
    translateTransition
  }
  def createButtonAnimation(button: Button, deltaX: Double, deltaY: Double): TranslateTransition = {
    ButtonAnimationFactory.createTranslationAnimation(
      node = button,
      deltaX = deltaX,
      deltaY = deltaY,
      durationMillis = 15,
      cycleCount = 1,
      autoReverse = false
    )
  }

  def createDropShadowEffect(color: Color, radius: Double, offsetX: Double, offsetY: Double): DropShadow = {
    val shadow = new DropShadow()
    shadow.color = color
    shadow.radius = radius
    shadow.offsetX = offsetX
    shadow.offsetY = offsetY
    shadow
  }

  def setupButtonHoverEffects(button: Button, hoverEnterAnimation: TranslateTransition, hoverExitAnimation: TranslateTransition): Unit = {
    val shadowEffect = createDropShadowEffect(Color.Gray, radius = 10, offsetX = 5, offsetY = 5)

    button.onMouseEntered = _ => {
      if (button.translateY.value == 0) {
        button.effect = shadowEffect
        hoverEnterAnimation.playFromStart()
      }
    }

    button.onMouseExited = _ => {
      if (button.translateY.value == -5) {
        button.effect = null
        hoverExitAnimation.playFromStart()
      }
    }
  }
}
