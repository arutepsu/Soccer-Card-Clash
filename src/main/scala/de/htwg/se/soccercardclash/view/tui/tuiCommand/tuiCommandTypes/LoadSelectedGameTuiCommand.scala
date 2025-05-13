package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.{GlobalObservable, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory

import java.io.File

class LoadSelectedGameTuiCommand(controller: IController, index: Int, tuiCommandFactory: ITuiCommandFactory) extends ITuiCommand {

  override def execute(input: Option[String] = None): Unit = {
    GlobalObservable.notifyObservers(SceneSwitchEvent.LoadGame)
    val saveDirectory = new File("games")
    val savedGames = saveDirectory.listFiles()
      .filter(file => file.getName.endsWith(".xml") || file.getName.endsWith(".json"))
      .map(_.getName)

    if (index >= 0 && index < savedGames.length) {
      val selectedFile = savedGames(index)
      val loadGameCommand = tuiCommandFactory.createLoadGameTuiCommand(selectedFile)
      loadGameCommand.execute()
    } else {
      println("Invalid selection.")
    }
  }
}

