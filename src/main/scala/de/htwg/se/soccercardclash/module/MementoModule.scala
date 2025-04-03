package de.htwg.se.soccercardclash.module

import com.google.inject.AbstractModule
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.{IMementoManagerFactory, MementoManagerFactory}
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import com.google.inject.assistedinject.FactoryModuleBuilder
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory

class MementoModule extends AbstractModule {

  override def configure(): Unit = {

    install(new FactoryModuleBuilder()
      .implement(classOf[IMementoCreator], classOf[MementoCreator])
      .build(classOf[IMementoCreatorFactory]))

    install(new FactoryModuleBuilder()
      .implement(classOf[IMementoRestorer], classOf[MementoRestorer])
      .build(classOf[IMementoRestorerFactory]))

    bind(classOf[IMementoManagerFactory])
      .toConstructor(classOf[MementoManagerFactory].getConstructor(
        classOf[IMementoCreatorFactory],
        classOf[IMementoRestorerFactory]
      ))
      .asEagerSingleton()
  }
}
