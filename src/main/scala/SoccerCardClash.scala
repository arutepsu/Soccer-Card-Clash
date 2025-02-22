import controller.base.Controller
import controller.{Events, IController}
import scalafx.application.JFXApp3
import view.gui.Gui
import view.tui.Tui

import scala.io.StdIn.readLine
import com.google.inject.{Guice, Injector}
import controller.base.Controller
import controller.{Events, IController}
import scalafx.application.JFXApp3
import view.gui.Gui
import view.tui.Tui

import scala.io.StdIn.readLine
import com.google.inject.{Guice, Injector}
import model.cardComponent.base.types.RegularCard

object SoccerCardClash {
  private val injector: Injector = Guice.createInjector(new SoccerCardClashModule())

  private val controller: IController = injector.getInstance(classOf[IController])
  private val gui: Gui = Gui(controller)
  private val tui: Tui = Tui(controller)

  @volatile private var input: String = ""

  def main(args: Array[String]): Unit = {
    startGui()

    while ({ input = readLine().trim; input } != ":quit") {
      tui.processInputLine(input)
    }

    println("Exiting game. Goodbye!")
    System.exit(0)
  }

  private def startGui(): Unit = {
    val guiThread = new Thread(() => gui.main(Array.empty))
    guiThread.setDaemon(true) // Ensures GUI thread does not block app exit
    guiThread.start()
  }
}