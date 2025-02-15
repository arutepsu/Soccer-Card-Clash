
import scalafx.application.JFXApp3
import view.Tui
import controller.IController
import controller.baseControllerImplementation.Controller
import view.Gui

object Main extends JFXApp3 {
  private val controller: IController = new Controller()
  override def start(): Unit = {
    // ✅ Start TUI in a separate thread
    new Thread(() => {
      val tui = new Tui(controller)
      tui.start()
    }).start()

    // ✅ Start GUI
    val gui = new Gui(controller)
    gui.start()

    // ✅ Start the game (sets players & field)
    //    controller.startGame()
  }
}
