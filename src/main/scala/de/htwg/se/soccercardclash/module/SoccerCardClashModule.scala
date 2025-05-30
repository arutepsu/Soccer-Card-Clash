package de.htwg.se.soccercardclash.module

import com.google.inject.AbstractModule
import de.htwg.se.soccercardclash.module.*

class SoccerCardClashModule extends AbstractModule {
  override def configure(): Unit = {
    install(new ControllerModule())
    install(new PlayerModule())
    install(new CardModule())
    install(new GameStateModule())
    install(new HandCardsQueueModule())
    install(new GameCoreModule())
    install(new FileIOModule())
    install(new RandomProviderModule())
  }
}
