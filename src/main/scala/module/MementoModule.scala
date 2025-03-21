package module
import com.google.inject.AbstractModule
import controller.command.memento.factory.*
import controller.command.memento.componenets.*
import com.google.inject.assistedinject.FactoryModuleBuilder

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
        classOf[IMementoCreatorFactory], classOf[IMementoRestorerFactory]))
      .asEagerSingleton()
  }
}
