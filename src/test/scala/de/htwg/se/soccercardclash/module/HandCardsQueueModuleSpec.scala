package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice, Injector}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.{HandCardsQueueDeserializer, HandCardsQueueFactory, IHandCardsQueueFactory}
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*

class HandCardsQueueModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Test-specific module to satisfy the constructor of HandCardsQueueDeserializer
  class TestBindings extends AbstractModule {
    override def configure(): Unit = {
      val mockCardDeserializer = mock[CardDeserializer]

      bind(classOf[CardDeserializer]).toInstance(mockCardDeserializer)
    }
  }

  "HandCardsQueueModule" should {

    "bind IHandCardsQueueFactory to HandCardsQueueFactory" in {
      val injector: Injector = Guice.createInjector(new HandCardsQueueModule, new TestBindings)
      val factory = injector.getInstance(classOf[IHandCardsQueueFactory])

      factory shouldBe a[HandCardsQueueFactory]
    }

    "construct HandCardsQueueDeserializer with injected dependencies" in {
      val injector: Injector = Guice.createInjector(new HandCardsQueueModule, new TestBindings)

      val deserializer = injector.getInstance(classOf[HandCardsQueueDeserializer])
      deserializer shouldBe a[HandCardsQueueDeserializer]
    }
  }
}