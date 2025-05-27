package de.htwg.se.soccercardclash.module

import com.google.inject._
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.components._
import de.htwg.se.soccercardclash.model.gameComponent.action.manager._
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy._
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy._
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.scoringStrategy._
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy._
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.scoringStrategy.base.StandardScoring
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito.*

class GameStateModuleSpec extends AnyWordSpec with Matchers {

  "GameStateModule" should {
    val injector = Guice.createInjector(new GameStateModule {
      override def configure(): Unit = {
        bind(classOf[IHandCardsQueueFactory]).toInstance(mock(classOf[IHandCardsQueueFactory]))
        bind(classOf[IFieldCardsFactory]).toInstance(mock(classOf[IFieldCardsFactory]))
        super.configure()
      }
    })

    "bind IGameCardsFactory to GameCardsFactory" in {
      val gameCardsFactory = injector.getInstance(classOf[IGameCardsFactory])
      gameCardsFactory shouldBe a[GameCardsFactory]
    }

    "bind IActionManagerFactory to ActionManagerFactory" in {
      val factory = injector.getInstance(classOf[IActionManagerFactory])
      factory shouldBe a[ActionManagerFactory]
    }

    "bind IRolesFactory to RolesFactory" in {
      val factory = injector.getInstance(classOf[IRolesFactory])
      factory shouldBe a[RolesFactory]
    }

    "bind IScoresFactory to ScoresFactory" in {
      val factory = injector.getInstance(classOf[IScoresFactory])
      factory shouldBe a[ScoresFactory]
    }

    "bind IHandCardsFactory to HandCardsFactory" in {
      val factory = injector.getInstance(classOf[IHandCardsFactory])
      factory shouldBe a[HandCardsFactory]
    }

    "bind IAttackManager to AttackManager singleton" in {
      val m1 = injector.getInstance(classOf[IAttackManager])
      val m2 = injector.getInstance(classOf[IAttackManager])
      m1 shouldBe a[AttackManager]
      m1 shouldBe theSameInstanceAs(m2)
    }

    "bind IBoostManager to BoostManager singleton" in {
      val m1 = injector.getInstance(classOf[IBoostManager])
      val m2 = injector.getInstance(classOf[IBoostManager])
      m1 shouldBe a[BoostManager]
      m1 shouldBe theSameInstanceAs(m2)
    }

    "bind ISwapManager to SwapManager singleton" in {
      val m1 = injector.getInstance(classOf[ISwapManager])
      val m2 = injector.getInstance(classOf[ISwapManager])
      m1 shouldBe a[SwapManager]
      m1 shouldBe theSameInstanceAs(m2)
    }

    "bind IScoringStrategy to StandardScoring" in {
      val strategy = injector.getInstance(classOf[IScoringStrategy])
      strategy shouldBe a[StandardScoring]
    }

    "bind IPlayerActionManager to PlayerActionManager" in {
      val manager = injector.getInstance(classOf[IPlayerActionManager])
      manager shouldBe a[PlayerActionManager]
    }
  }
}
