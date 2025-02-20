import scalafx.application.JFXApp3
import controller.{Events, IController}
import controller.base.Controller
import model.cardComponent.base.RegularCard
import view.gui.Gui
import view.tui.Tui
import scala.io.StdIn.readLine

object SoccerCardClash {
  private val controller: IController = new Controller()
  private val gui: Gui = Gui(controller)
  private val tui: Tui = Tui(controller)

  def main(args: Array[String]): Unit = {
    new Thread(() => {
      gui.main(Array.empty)
    }).start()

    var input: String = ""

    while (input != ":quit") {
      input = readLine()
      tui.processInputLine(input)
    }
  }
}

