package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.{HandCardsQueueDeserializer, HandCardsQueueFactory, IHandCardsQueueFactory, *}
import de.htwg.se.soccercardclash.model.cardComponent.factory.{CardDeserializer, *}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar

class HandCardsQueueModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  class TestHandCardsQueueModule extends AbstractModule {
    override def configure(): Unit = {
      bind(classOf[CardDeserializer]).toInstance(mock[CardDeserializer])
      // Let HandCardsQueueModule bind IHandCardsQueueFactory
      install(new HandCardsQueueModule)
    }
  }

  "HandCardsQueueModule" should {
    val injector = Guice.createInjector(new TestHandCardsQueueModule)

    "bind IHandCardsQueueFactory to HandCardsQueueFactory" in {
      val factory = injector.getInstance(classOf[IHandCardsQueueFactory])
      factory shouldBe a[HandCardsQueueFactory]
    }

    "bind HandCardsQueueDeserializer with constructor injection" in {
      val deserializer = injector.getInstance(classOf[HandCardsQueueDeserializer])
      deserializer shouldBe a[HandCardsQueueDeserializer]
    }
  }
}
