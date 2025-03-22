package de.htwg.se.soccercardclash.controller.command.factory

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.{Events, IController}
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.gameStateCommands.ResetGameCommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands.{RegularSwapActionCommand, ReverseSwapActionCommand}
import de.htwg.se.soccercardclash.controller.command.base.workflow.{CreateGameWorkflowCommand, LoadGameWorkflowCommand, QuitWorkflowCommand, SaveGameWorkflowCommand, WorkflowCommand}
import de.htwg.se.soccercardclash.controller.command.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.WorkflowTuiCommand

class CommandFactory @Inject()(
                                game: IGame,
                                controller: IController,
                                mementoManagerFactory: IMementoManagerFactory
                              ) extends ICommandFactory {

  override def createSingleAttackCommand(defenderPosition: Int): ICommand =
    new SingleAttackActionCommand(defenderPosition, game, mementoManagerFactory)

  override def createDoubleAttackCommand(defenderPosition: Int): ICommand =
    new DoubleAttackActionCommand(defenderPosition, game, mementoManagerFactory)

  override def createBoostDefenderCommand(defenderPosition: Int): ICommand =
    new BoostDefenderActionCommand(defenderPosition, game, mementoManagerFactory)

  override def createBoostGoalkeeperCommand(): ICommand =
    new BoostGoalkeeperActionCommand(game, mementoManagerFactory)

  override def createRegularSwapCommand(index: Int): ICommand =
    new RegularSwapActionCommand(index, game, mementoManagerFactory)

  override def createReverseSwapCommand(): ICommand =
    new ReverseSwapActionCommand(game, mementoManagerFactory)

  override def createResetGameCommand(): ICommand =
    new ResetGameCommand(game, mementoManagerFactory)
    
  override def createCreateGameCommand(game: IGame, player1: String, player2: String): WorkflowCommand =
    new CreateGameWorkflowCommand(game, player1, player2)
    
  override def createQuitCommand(game: IGame): WorkflowCommand =
    new QuitWorkflowCommand(game)


  override def createLoadGameCommand(fileName: String): WorkflowCommand =
    new LoadGameWorkflowCommand(game, fileName)

  override def createSaveGameCommand(): WorkflowCommand =
    new SaveGameWorkflowCommand(game)


}

trait ICommandFactory {
  def createSingleAttackCommand(defenderPosition: Int): ICommand

  def createDoubleAttackCommand(defenderPosition: Int): ICommand

  def createBoostDefenderCommand(defenderPosition: Int): ICommand

  def createBoostGoalkeeperCommand(): ICommand

  def createRegularSwapCommand(index: Int): ICommand

  def createReverseSwapCommand(): ICommand

  def createResetGameCommand(): ICommand

  def createCreateGameCommand(game: IGame, player1: String, player2: String): WorkflowCommand

  def createQuitCommand(game: IGame): WorkflowCommand

  def createSaveGameCommand(): WorkflowCommand

  def createLoadGameCommand(fileName: String): WorkflowCommand
}