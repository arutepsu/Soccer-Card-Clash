package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.util.ObservableEvent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.util.*
import org.mockito.Mockito.when

class AttackManagerSpec extends AnyWordSpec with Matchers with MockitoSugar{
  "AttackManager" should {

    "delegate to strategy's execute method" in {
      val strategy = mock[IAttackStrategy]
      val state = mock[IGameState]
      val expected = (true, state, List(GameActionEvent.RegularAttack))

      when(strategy.execute(state)).thenReturn(expected)

      val manager = new AttackManager()
      manager.executeAttack(strategy, state) shouldBe expected
    }

    "handle strategy returning false result" in {
      val strategy = mock[IAttackStrategy]
      val state = mock[IGameState]
      val expected = (false, state, Nil)

      when(strategy.execute(state)).thenReturn(expected)

      new AttackManager().executeAttack(strategy, state) shouldBe expected
    }
  }

}
