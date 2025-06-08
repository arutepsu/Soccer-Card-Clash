package de.htwg.se.soccercardclash.view.tui.tuiCommand.factory

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.BoostDefenderActionCommand
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.IPrompter
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.*
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.*

trait ITuiCommandFactory {
  def createSingleAttackTuiCommand(): ITuiCommand
  def createDoubleAttackTuiCommand(): ITuiCommand
  def createBoostDefenderTuiCommand(): ITuiCommand
  def createBoostGoalkeeperTuiCommand(): ITuiCommand
  def createRegularSwapTuiCommand(): ITuiCommand
  def createReverseSwapTuiCommand(): ITuiCommand
  def createStartGameTuiCommand(player1: String, player2: String): ITuiCommand
  def createStartGameWithAITuiCommand(human: String, ai: String): ITuiCommand
  def createSaveGameTuiCommand(): ITuiCommand
  def createLoadGameTuiCommand(fileName: String): ITuiCommand
  def createLoadSelectedGameTuiCommand(index: Int): ITuiCommand
  def createCreatePlayersNameTuiCommand(): CreatePlayersNameTuiCommand
  def createCreatePlayersNameWithAITuiCommand(): CreatePlayersNameWithAITuiCommand
  def createShowGamesTuiCommand() :ITuiCommand
  def createUndoTuiCommand(): ITuiCommand
  def createRedoTuiCommand(): ITuiCommand
  def createExitTuiCommand(): ITuiCommand

}

class TuiCommandFactory(controller: IController, contextHolder: IGameContextHolder, prompter: IPrompter) extends ITuiCommandFactory {

  override def createSingleAttackTuiCommand(): ITuiCommand = new AttackTuiCommand(controller, contextHolder)
  override def createDoubleAttackTuiCommand(): ITuiCommand = new DoubleAttackTuiCommand(controller, contextHolder)
  override def createBoostDefenderTuiCommand(): ITuiCommand = new BoostDefenderTuiCommand(controller, contextHolder)
  override def createBoostGoalkeeperTuiCommand(): ITuiCommand = new BoostGoalkeeperTuiCommand(controller, contextHolder)
  override def createRegularSwapTuiCommand(): ITuiCommand = new RegularSwapTuiCommand(controller, contextHolder)
  override def createReverseSwapTuiCommand(): ITuiCommand = new ReverseSwapTuiCommand(controller, contextHolder)
  override def createCreatePlayersNameTuiCommand(): CreatePlayersNameTuiCommand =
    new CreatePlayersNameTuiCommand(controller, contextHolder)
  override def createCreatePlayersNameWithAITuiCommand(): CreatePlayersNameWithAITuiCommand =
    new CreatePlayersNameWithAITuiCommand(controller, contextHolder)

  override def createStartGameTuiCommand(player1: String, player2: String): ITuiCommand =
    new StartGameTuiCommand(controller,contextHolder, player1, player2)

  override def createStartGameWithAITuiCommand(human: String, ai: String): ITuiCommand =
    new StartGameTuiCommandWithAI(controller, contextHolder, human, ai)

  override def createSaveGameTuiCommand(): ITuiCommand =
    new SaveGameTuiCommand(controller, contextHolder)

  override def createLoadGameTuiCommand(fileName: String): ITuiCommand = {
    new LoadGameTuiCommand(controller, fileName)
  }

  override  def createLoadSelectedGameTuiCommand(index: Int): ITuiCommand = {
    new LoadSelectedGameTuiCommand(controller, index, this)
  }

  override def createShowGamesTuiCommand() : ITuiCommand = {
  new ShowAvailableGamesTuiCommand(controller, prompter, this)
  }

  override def createUndoTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.undo(contextHolder.get))
  override def createRedoTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.redo(contextHolder.get))
  override def createExitTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.quit())

}

