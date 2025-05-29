package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.model.playerComponent.util.*

class RandomProviderModule extends AbstractModule {
  override def configure(): Unit = {}

  @Provides
  @Singleton
  def provideRandomProviders(): Map[String, IRandomProvider] = {
    Map(
      "Taka"      -> new RandomProvider(1),
      "Bitstorm"  -> new RandomProvider(2),
      "Defendra"  -> new RandomProvider(3),
      "MetaAI"    -> new RandomProvider(4)
    )
  }
}
