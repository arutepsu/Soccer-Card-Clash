package de.htwg.se.soccercardclash.view.gui.scenes.sceneManager

import scalafx.application.Platform
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*
import scala.concurrent.Future

class UIAction(val delay: Int, val block: () => Unit)

object UIAction {
  // Enables syntax: UIAction(3000) { ... }
  def apply(delay: Int)(block: => Unit): UIAction =
    new UIAction(delay, () => block)
}


class UIActionScheduler(using ec: ExecutionContext) {

  private def run(action: UIAction): Future[Unit] = Future {
    Thread.sleep(action.delay)
    Platform.runLater(action.block())
  }

  /** Run a sequence of actions in order, each after its own delay. */
  def runSequence(actions: UIAction*): Unit = {
    actions.foldLeft(Future.successful(())) { (f, next) =>
      f.flatMap(_ => run(next))
    }
  }

  /** Run an async operation, then schedule a UIAction with its result. */
  def runAsyncThen[A](future: => Future[A])(next: A => UIAction): Unit = {
    future.foreach(result => run(next(result)))
  }
}
