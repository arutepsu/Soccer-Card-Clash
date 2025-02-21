import controller.base.Controller
import controller.{Events, IController}
import model.cardComponent.base.RegularCard
import scalafx.application.JFXApp3
import view.gui.Gui
import view.tui.Tui

import scala.io.StdIn.readLine
import com.google.inject.{Guice, Injector}
import controller.base.Controller
import controller.{Events, IController}
import model.cardComponent.base.RegularCard
import scalafx.application.JFXApp3
import view.gui.Gui
import view.tui.Tui

import scala.io.StdIn.readLine
import com.google.inject.{Guice, Injector}
object SoccerCardClash {
  private val injector: Injector = Guice.createInjector(new SoccerCardClashModule())

  private val controller: IController = injector.getInstance(classOf[IController])
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
