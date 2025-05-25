package de.htwg.se.soccercardclash.controller.command

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands.{RegularSwapActionCommand, ReverseSwapActionCommand}
import de.htwg.se.soccercardclash.controller.command.workflow.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.WorkflowTuiCommand
class CommandFactory @Inject()(gameService: IGameService, actionManager: IActionManager) extends ICommandFactory {

  override def createSingleAttackCommand(defenderPosition: Int): ICommand =
    new SingleAttackActionCommand(defenderPosition, actionManager)

  override def createDoubleAttackCommand(defenderPosition: Int): ICommand =
    new DoubleAttackActionCommand(defenderPosition, actionManager)

  override def createBoostDefenderCommand(defenderPosition: Int): ICommand =
    new BoostDefenderActionCommand(defenderPosition, actionManager)

  override def createBoostGoalkeeperCommand(): ICommand =
    new BoostGoalkeeperActionCommand(actionManager)

  override def createRegularSwapCommand(index: Int): ICommand =
    new RegularSwapActionCommand(index, actionManager)

  override def createReverseSwapCommand(): ICommand =
    new ReverseSwapActionCommand(actionManager)

  override def createCreateGameCommand(player1: String, player2: String): WorkflowCommand =
    new CreateGameWorkflowCommand(gameService, player1, player2)

  override def createQuitCommand(): WorkflowCommand =
    new QuitWorkflowCommand()

  override def createLoadGameCommand(fileName: String): WorkflowCommand =
    new LoadGameWorkflowCommand(gameService, fileName)

  override def createSaveGameCommand(): WorkflowCommand =
    new SaveGameWorkflowCommand(gameService)
}

trait ICommandFactory {
  def createSingleAttackCommand(defenderPosition: Int): ICommand

  def createDoubleAttackCommand(defenderPosition: Int): ICommand

  def createBoostDefenderCommand(defenderPosition: Int): ICommand

  def createBoostGoalkeeperCommand(): ICommand

  def createRegularSwapCommand(index: Int): ICommand

  def createReverseSwapCommand(): ICommand

  def createCreateGameCommand(player1: String, player2: String): WorkflowCommand

  def createQuitCommand(): WorkflowCommand

  def createSaveGameCommand(): WorkflowCommand

  def createLoadGameCommand(fileName: String): WorkflowCommand
}