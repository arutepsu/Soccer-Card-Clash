package de.htwg.se.soccercardclash.controller.command.memento.factory

import de.htwg.se.soccercardclash.controller.command.memento.IMementoManager
import de.htwg.se.soccercardclash.controller.command.memento.base.{Memento, MementoManager}
import de.htwg.se.soccercardclash.controller.command.memento.componenets.{IMementoCreator, IMementoCreatorFactory, IMementoRestorer, IMementoRestorerFactory}
import de.htwg.se.soccercardclash.model.gameComponent.IGame

import javax.inject.{Inject, Singleton}

trait IMementoManagerFactory {
  def create(game: IGame): IMementoManager
}

@Singleton
class MementoManagerFactory @Inject()(
                                       mementoCreatorFactory: IMementoCreatorFactory,
                                       mementoRestorerFactory: IMementoRestorerFactory
                                     ) extends IMementoManagerFactory {

  override def create(game: IGame): IMementoManager = {
    val creator: IMementoCreator = mementoCreatorFactory.create(game)
    val restorer: IMementoRestorer = mementoRestorerFactory.create(game)
    new MementoManager(game, creator, restorer)
  }
}



