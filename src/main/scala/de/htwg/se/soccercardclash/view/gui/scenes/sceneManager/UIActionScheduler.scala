package de.htwg.se.soccercardclash.view.gui.scenes.sceneManager

import scalafx.animation.PauseTransition
import scalafx.application.Platform
import scalafx.util.Duration

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

case class UIAction(delay: Int, block: () => Unit)

object UIAction {
  def delayed(delay: Int)(block: => Unit): UIAction =
    UIAction(delay, () => block)
}


class UIActionScheduler {

  def runSequence(actions: UIAction*): Unit = {
    def run(index: Int): Unit = {
      if (index >= actions.length) return

      val UIAction(delay, block) = actions(index)
      val pause = new PauseTransition(Duration(delay))
      pause.setOnFinished(_ => {
        Platform.runLater {
          block()
        }
        run(index + 1)
      })
      Platform.runLater {
        pause.play()
      }
    }

    run(0)
  }
}
