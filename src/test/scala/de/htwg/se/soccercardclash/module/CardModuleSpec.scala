package de.htwg.se.soccercardclash.module
import com.google.inject.{AbstractModule, Guice, Provides}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value, *}
import de.htwg.se.soccercardclash.model.cardComponent.factory.{CardDeserializer, CardFactory, DeckFactory, ICardFactory, IDeckFactory, *}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class CardModuleSpec extends AnyWordSpec with Matchers {

  "CardModule" should {

    "bind ICardFactory to CardFactory" in {
      val injector = Guice.createInjector(new CardModule)
      val factory = injector.getInstance(classOf[ICardFactory])
      factory shouldBe a [CardFactory]
    }

    "bind IDeckFactory to DeckFactory" in {
      val injector = Guice.createInjector(new CardModule)
      val factory = injector.getInstance(classOf[IDeckFactory])
      factory shouldBe a [DeckFactory]
    }

    "provide CardDeserializer with injected ICardFactory" in {
      val injector = Guice.createInjector(new CardModule)
      val deserializer = injector.getInstance(classOf[CardDeserializer])
      deserializer should not be null
    }
  }
}
