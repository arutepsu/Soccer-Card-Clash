package de.htwg.se.soccercardclash.module

import com.google.inject.Singleton
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.command.factory.*
import de.htwg.se.soccercardclash.util.Observable
import com.google.inject.AbstractModule

class ControllerModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
    bind(classOf[Observable]).to(classOf[IController])
    bind(classOf[ICommandFactory]).to(classOf[CommandFactory])

  }
}
