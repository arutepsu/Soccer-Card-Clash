package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.{AttackManager, IAttackStrategy}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class AttackManagerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "AttackHandler" should "return true when the strategy executes successfully" in {
    val mockField = mock[IGameState]
    val mockStrategy = mock[IAttackStrategy]

    when(mockStrategy.execute(mockField)).thenReturn(true)

    val handler = new AttackManager(mockField)
    handler.executeAttack(mockStrategy) shouldBe true

    verify(mockStrategy).execute(mockField)
  }

  it should "return false when the strategy fails" in {
    val mockField = mock[IGameState]
    val mockStrategy = mock[IAttackStrategy]

    when(mockStrategy.execute(mockField)).thenReturn(false)

    val handler = new AttackManager(mockField)
    handler.executeAttack(mockStrategy) shouldBe false

    verify(mockStrategy).execute(mockField)
  }
}