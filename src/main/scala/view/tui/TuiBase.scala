package view.tui
import controller.IController
import util.{ObservableEvent, Observer}
trait TuiBase extends Observer {
  def run(): Unit

  /** ✅ Automatically update TUI state */
  override def update(e: ObservableEvent): Unit = {
    println("🔄 TUI Updating!")
    run() // ✅ Re-run the current TUI state when the game changes
  }
}
