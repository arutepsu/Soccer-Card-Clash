package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice}
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.cardComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.module.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar

class SoccerCardClashModuleSpec extends AnyWordSpec with Matchers {
  "SoccerCardClashModule" should {
    val injector = Guice.createInjector(new SoccerCardClashModule)

    "provide a fully wired IController" in {
      val controller = injector.getInstance(classOf[IController])
      controller shouldBe a[Controller]
    }

    "provide a PlayerFactory" in {
      val playerFactory = injector.getInstance(classOf[IPlayerFactory])
      playerFactory shouldBe a[PlayerFactory]
    }

    "provide a CardFactory" in {
      val cardFactory = injector.getInstance(classOf[ICardFactory])
      cardFactory shouldBe a[CardFactory]
    }

    "provide an ActionManagerFactory" in {
      val factory = injector.getInstance(classOf[IActionManagerFactory])
      factory shouldBe a[ActionManagerFactory]
    }
    "provide a GameDeserializer" in {
      val deserializer = injector.getInstance(classOf[GameDeserializer])
      deserializer shouldBe a[GameDeserializer]
    }

  }
}
