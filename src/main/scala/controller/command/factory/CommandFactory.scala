package controller.command.factory
import controller.command.ICommand
import controller.command.actionCommandTypes.attackCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import controller.command.actionCommandTypes.boostCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import controller.command.actionCommandTypes.swapCommands.{CircularSwapActionCommand, HandSwapActionCommand}
import com.google.inject.Inject
import model.gameComponent.IGame
import controller.command.base.workflow.{LoadGameCommand, QuitCommand, SaveGameCommand, StartGameCommand, WorkflowCommand}
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
    new StartGameCommand(game, player1, player2)

  override def createQuitCommand(game: IGame): WorkflowCommand =
    new QuitCommand(game)

  override def createLoadGameCommand(): WorkflowCommand =
    new LoadGameCommand

  override def createSaveGameCommand(): WorkflowCommand =
    new SaveGameCommand
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