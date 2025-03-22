package de.htwg.se.soccercardclash.controller.command.memento.base

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.controller.Events
import de.htwg.se.soccercardclash.controller.command.memento.IMementoManager
import de.htwg.se.soccercardclash.controller.command.memento.base.Memento
import de.htwg.se.soccercardclash.controller.command.memento.componenets.{IMementoCreator, IMementoRestorer}
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IActionManager

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
