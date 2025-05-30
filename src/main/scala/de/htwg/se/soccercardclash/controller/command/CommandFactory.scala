package de.htwg.se.soccercardclash.controller.command

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands.{RegularSwapActionCommand, ReverseSwapActionCommand}
import de.htwg.se.soccercardclash.controller.command.workflow.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.{IActionExecutor, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.IRevertBoostStrategyFactory
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.WorkflowTuiCommand

class CommandFactory @Inject()(gameService: IGameService,
                               actionExecutor: IActionExecutor,
                               playerActionManager: IPlayerActionManager,
                               revertBoostFactory: IRevertBoostStrategyFactory) extends ICommandFactory {

  override def createSingleAttackCommand(defenderPosition: Int): ICommand =
    new SingleAttackActionCommand(defenderPosition, actionExecutor, revertBoostFactory)

  override def createDoubleAttackCommand(defenderPosition: Int): ICommand =
    new DoubleAttackActionCommand(defenderPosition, actionExecutor, playerActionManager, revertBoostFactory)

  override def createBoostDefenderCommand(defenderPosition: Int): ICommand =
    new BoostDefenderActionCommand(defenderPosition, actionExecutor, playerActionManager)

  override def createBoostGoalkeeperCommand(): ICommand =
    new BoostGoalkeeperActionCommand(actionExecutor, playerActionManager)

  override def createRegularSwapCommand(index: Int): ICommand =
    new RegularSwapActionCommand(index, actionExecutor, playerActionManager)

  override def createReverseSwapCommand(): ICommand =
    new ReverseSwapActionCommand(actionExecutor, playerActionManager)

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