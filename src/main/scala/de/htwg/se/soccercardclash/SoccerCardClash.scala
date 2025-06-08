package de.htwg.se.soccercardclash

import com.google.inject.{Guice, Injector}
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.model.cardComponent.base.types.RegularCard
import de.htwg.se.soccercardclash.module.SoccerCardClashModule
import de.htwg.se.soccercardclash.view.gui.Gui
import de.htwg.se.soccercardclash.view.tui.Tui
import scalafx.application.JFXApp3

import scala.io.StdIn.readLine
object SoccerCardClash {
  private val injector: Injector = Guice.createInjector(new SoccerCardClashModule())

  private val controller: IController = injector.getInstance(classOf[IController])
  private val contextHolder: IGameContextHolder = injector.getInstance(classOf[IGameContextHolder])

  private val gui: Gui = new Gui(controller, contextHolder, injector)
  private val tui: Tui = new Tui(controller, contextHolder)

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
    guiThread.setDaemon(true)
    guiThread.start()
  }
}