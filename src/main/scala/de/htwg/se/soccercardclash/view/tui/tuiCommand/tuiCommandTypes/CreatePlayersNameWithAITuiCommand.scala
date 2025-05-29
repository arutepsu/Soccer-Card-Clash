package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.util.{GlobalObservable, IGameContextHolder, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.gui.utils.AIRegistry
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.StartGameTuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class CreatePlayersNameWithAITuiCommand(
                                         controller: IController,
                                         contextHolder: IGameContextHolder
                                       ) extends ITuiCommand {

  private var waitingForInput: Boolean = false
  def isWaitingForNames: Boolean = waitingForInput

  private var playerNameOpt: Option[String] = None
  private var phase: Int = 0
  override def execute(input: Option[String] = None): Unit = {
    waitingForInput = true
    phase = 0
    GlobalObservable.notifyObservers(SceneSwitchEvent.SinglePlayer)
    println("Please enter your name (you will play against an AI):")
  }

  def handlePlayerNames(input: String): Boolean = {
    if (!waitingForInput) return false

    if (phase == 0) {
      val name = input.trim
      if (name.isEmpty || name.contains(" ") || name.startsWith(":")) {
        println("Invalid input! Please enter a single name (no spaces or commands).")
        return false
      }

      playerNameOpt = Some(name)
      phase = 1

      println(s"Available AIs:")
      GlobalObservable.notifyObservers(SceneSwitchEvent.AISelection)
      AIRegistry.aiProfiles.zipWithIndex.foreach { case (ai, idx) =>
        println(s"  ${idx + 1}. ${ai.name} - ${ai.description}")
      }
      println("Please enter the name of the AI you'd like to play against:")
      return true

    } else if (phase == 1) {
      val aiName = input.trim
      AIRegistry.getProfileByName(aiName) match {
        case Some(aiProfile) =>
          val playerName = playerNameOpt.get
          println(s"Players set: $playerName vs ${aiProfile.name}")
          val startGameCommand = new StartGameTuiCommandWithAI(controller, contextHolder, playerName, aiProfile.name)
          startGameCommand.execute()
          waitingForInput = false
          phase = 0
          return true

        case None =>
          println("Invalid AI name! Please choose from the list shown.")
          return false
      }
    }

    false
  }
}
