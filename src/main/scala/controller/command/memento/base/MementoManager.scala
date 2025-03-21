package controller.command.memento.base

import controller.Events
import controller.command.memento.IMementoManager
import controller.command.memento.base.Memento
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.gameComponent.IGame
import model.playerComponent.playerAction.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IActionManager
import com.google.inject.{Inject, Singleton}
import controller.command.memento.componenets.{IMementoCreator, IMementoRestorer}
class MementoManager(
                      game: IGame,
                      mementoCreator: IMementoCreator,
                      mementoRestorer: IMementoRestorer
                    ) extends IMementoManager {

  override def createMemento(): Memento = mementoCreator.createMemento()

  override def restoreGameState(memento: Memento): Unit =
    mementoRestorer.restoreGameState(memento)

  override def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit =
    mementoRestorer.restoreBoosts(memento, lastBoostedIndex)

  override def restoreGoalkeeperBoost(memento: Memento): Unit =
    mementoRestorer.restoreGoalkeeperBoost(memento)
}
