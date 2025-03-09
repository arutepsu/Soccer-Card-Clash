package controller.command.factory
import controller.command.ICommand
import controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapActionCommands.{CircularSwapActionCommand, HandSwapActionCommand}
import com.google.inject.Inject
import controller.Events
import model.gameComponent.IGame
import controller.command.base.workflow.{LoadGameWorkflowCommand, QuitWorkflowCommand, SaveGameWorkflowCommand, StartGameWorkflowCommand, WorkflowCommand}
import model.cardComponent.ICard
class CommandFactory @Inject() (game: IGame) extends ICommandFactory {
  override def createSingleAttackCommand(defenderPosition: Int): ICommand =
    new SingleAttackActionCommand(defenderPosition, game)

  override def createDoubleAttackCommand(defenderPosition: Int): ICommand =
    new DoubleAttackActionCommand(defenderPosition, game)

  override def createBoostDefenderCommand(defenderPosition: Int): ICommand =
    new BoostDefenderActionCommand(defenderPosition, game)

  override def createBoostGoalkeeperCommand(): ICommand =
    new BoostGoalkeeperActionCommand(game)

  override def createRegularSwapCommand(index: Int): ICommand =
    new HandSwapActionCommand(index, game)

  override def createCircularSwapCommand(index: Int): ICommand =
    new CircularSwapActionCommand(index, game)

  override def createStartGameCommand(game: IGame, player1: String, player2: String): WorkflowCommand =
    new StartGameWorkflowCommand(game, player1, player2)

  override def createQuitCommand(game: IGame): WorkflowCommand =
    new QuitWorkflowCommand(game)


  override def createLoadGameCommand(): WorkflowCommand =
    new LoadGameWorkflowCommand(game)

  override def createSaveGameCommand(): WorkflowCommand =
    new SaveGameWorkflowCommand(game)
  
}
trait ICommandFactory {
  def createSingleAttackCommand(defenderPosition: Int): ICommand
  def createDoubleAttackCommand(defenderPosition: Int): ICommand
  def createBoostDefenderCommand(defenderPosition: Int): ICommand
  def createBoostGoalkeeperCommand(): ICommand
  def createRegularSwapCommand(index: Int): ICommand
  def createCircularSwapCommand(index: Int): ICommand
  def createStartGameCommand(game: IGame, player1: String, player2: String): WorkflowCommand
  def createQuitCommand(game: IGame): WorkflowCommand
  def createSaveGameCommand(): WorkflowCommand
  def createLoadGameCommand(): WorkflowCommand

}