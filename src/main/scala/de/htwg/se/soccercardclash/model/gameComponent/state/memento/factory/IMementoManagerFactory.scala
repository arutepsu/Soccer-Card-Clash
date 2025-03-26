package de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory

import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.{Memento, MementoManager}
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.*
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*

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



