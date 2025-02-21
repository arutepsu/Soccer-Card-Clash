package controller.command.factories

import com.google.inject.Inject
import controller.command.ICommand

trait ICommandFactory {
  def createSingleAttackCommand(defenderPosition: Int): ICommand
  def createDoubleAttackCommand(defenderPosition: Int): ICommand
  def createBoostDefenderCommand(defenderPosition: Int): ICommand
  def createBoostGoalkeeperCommand(): ICommand
  def createRegularSwapCommand(index: Int): ICommand
  def createCircularSwapCommand(index: Int): ICommand
}
