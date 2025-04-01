package de.htwg.se.soccercardclash.view.tui.tuiCommand.factory

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.BoostDefenderActionCommand
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
  def createSaveGameTuiCommand(): ITuiCommand
  def createLoadGameTuiCommand(fileName: String): ITuiCommand
  def createLoadSelectedGameTuiCommand(index: Int): ITuiCommand
  def createCreatePlayersNameTuiCommand(): CreatePlayersNameTuiCommand
  def createShowGamesTuiCommand() :ITuiCommand
  def createUndoTuiCommand(): ITuiCommand
  def createRedoTuiCommand(): ITuiCommand
  def createExitTuiCommand(): ITuiCommand

}

class TuiCommandFactory(controller: IController, prompter: IPrompter) extends ITuiCommandFactory {

  override def createSingleAttackTuiCommand(): ITuiCommand = new AttackTuiCommand(controller)
  override def createDoubleAttackTuiCommand(): ITuiCommand = new DoubleAttackTuiCommand(controller)
  override def createBoostDefenderTuiCommand(): ITuiCommand = new BoostDefenderTuiCommand(controller)
  override def createBoostGoalkeeperTuiCommand(): ITuiCommand = new BoostGoalkeeperTuiCommand(controller)
  override def createRegularSwapTuiCommand(): ITuiCommand = new RegularSwapTuiCommand(controller)
  override def createReverseSwapTuiCommand(): ITuiCommand = new ReverseSwapTuiCommand(controller)
  override def createCreatePlayersNameTuiCommand(): CreatePlayersNameTuiCommand =
    new CreatePlayersNameTuiCommand(controller)

  override def createStartGameTuiCommand(player1: String, player2: String): ITuiCommand =
    new StartGameTuiCommand(controller, player1, player2)

  override def createSaveGameTuiCommand(): ITuiCommand =
    new SaveGameTuiCommand(controller)

  override def createLoadGameTuiCommand(fileName: String): ITuiCommand = {
    new LoadGameTuiCommand(controller, fileName)
  }

  override  def createLoadSelectedGameTuiCommand(index: Int): ITuiCommand = {
    new LoadSelectedGameTuiCommand(controller, index, this)
  }

  override def createShowGamesTuiCommand() : ITuiCommand = {
  new ShowAvailableGamesTuiCommand(controller, prompter, this)
  }

  override def createUndoTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.undo())
  override def createRedoTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.redo())
  override def createExitTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.quit())

}

