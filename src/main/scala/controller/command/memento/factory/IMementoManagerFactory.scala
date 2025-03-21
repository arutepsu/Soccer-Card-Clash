package controller.command.memento.factory

import controller.command.memento.IMementoManager
import controller.command.memento.base.*
import controller.command.memento.componenets.{IMementoCreator, IMementoCreatorFactory, IMementoRestorer, IMementoRestorerFactory}
import model.gameComponent.IGame

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



