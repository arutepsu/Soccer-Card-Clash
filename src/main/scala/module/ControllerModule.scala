package module
import com.google.inject.Singleton
import controller.IController
import controller.base.Controller
import controller.command.factory.*
import util.Observable
import com.google.inject.AbstractModule

class ControllerModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
    bind(classOf[Observable]).to(classOf[Controller]).in(classOf[Singleton])
    bind(classOf[ICommandFactory]).to(classOf[CommandFactory])
//    install(new GameCoreModule())
//    install(new MementoModule())
  }
}
