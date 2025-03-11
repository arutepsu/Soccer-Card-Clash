package view.tui.tuiCommand.factory

import controller.IController
import controller.command.actionCommandTypes.boostActionCommands.BoostDefenderActionCommand
import view.tui.tuiCommand.factory.*
import view.tui.tuiCommand.base.ITuiCommand
import view.tui.tuiCommand.tuiCommandTypes.*

trait ITuiCommandFactory {
  def createAttackTuiCommand(): ITuiCommand
  def createDoubleAttackTuiCommand(): ITuiCommand
  def createBoostDefenderTuiCommand(): ITuiCommand
  def createRegularSwapTuiCommand(): ITuiCommand
  def createStartGameTuiCommand(player1: String, player2: String): ITuiCommand
  def createCreatePlayersNameTuiCommand(): CreatePlayersNameTuiCommand
  def createUndoTuiCommand(): ITuiCommand
  def createRedoTuiCommand(): ITuiCommand
  def createExitTuiCommand(): ITuiCommand

}

class TuiCommandFactory(controller: IController) extends ITuiCommandFactory {
  
  override def createAttackTuiCommand(): ITuiCommand = new AttackTuiCommand(controller)
  override def createDoubleAttackTuiCommand(): ITuiCommand = new DoubleAttackTuiCommand(controller)
  override def createBoostDefenderTuiCommand(): ITuiCommand = new BoostTuiCommand(controller)
  override def createRegularSwapTuiCommand(): ITuiCommand = new RegularSwapTuiCommand(controller)

  override def createCreatePlayersNameTuiCommand(): CreatePlayersNameTuiCommand =
    new CreatePlayersNameTuiCommand(controller)

  override def createStartGameTuiCommand(player1: String, player2: String): ITuiCommand =
    new StartGameTuiCommand(controller, player1, player2)

  override def createUndoTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.undo())
  override def createRedoTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.redo())
  override def createExitTuiCommand(): ITuiCommand = new WorkflowTuiCommand(() => controller.quit())
}

