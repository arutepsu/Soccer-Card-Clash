package controller.command.factories
import controller.command.ICommand
import controller.command.commandTypes.attackCommands.{DoubleAttackCommand, SingleAttackCommand}
import controller.command.commandTypes.boostCommands.{BoostDefenderCommand, BoostGoalkeeperCommand}
import controller.command.commandTypes.swapCommands.{CircularSwapCommand, HandSwapCommand}
import com.google.inject.Inject
import model.gameComponent.IGame
class CommandFactory @Inject() (game: IGame) extends ICommandFactory {
  override def createSingleAttackCommand(defenderPosition: Int): ICommand =
    new SingleAttackCommand(defenderPosition, game.getActionManager)

  override def createDoubleAttackCommand(defenderPosition: Int): ICommand =
    new DoubleAttackCommand(defenderPosition, game.getActionManager)

  override def createBoostDefenderCommand(defenderPosition: Int): ICommand =
    new BoostDefenderCommand(defenderPosition, game.getActionManager)

  override def createBoostGoalkeeperCommand(): ICommand =
    new BoostGoalkeeperCommand(game.getActionManager)

  override def createRegularSwapCommand(index: Int): ICommand =
    new HandSwapCommand(index, game.getActionManager)

  override def createCircularSwapCommand(index: Int): ICommand =
    new CircularSwapCommand(index, game.getActionManager)
}
