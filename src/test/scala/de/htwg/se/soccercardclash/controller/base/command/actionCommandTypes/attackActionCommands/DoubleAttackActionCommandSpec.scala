package de.htwg.se.soccercardclash.controller.base.command.actionCommandTypes.attackActionCommands

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.util.{Failure, Success, Try}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.util.ObservableEvent
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.controller.command.CommandResult
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.DoubleAttackActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionManager

class DoubleAttackActionCommandSpec extends AnyFlatSpec with Matchers {

  "DoubleAttackActionCommand" should "return Some(...) when actionManager returns success" in {
    val state = mock(classOf[IGameState])
    val updatedState = mock(classOf[IGameState])
    val event = mock(classOf[ObservableEvent])
    val manager = mock(classOf[IActionManager])

    when(manager.doubleAttack(state, 1)).thenReturn((true, updatedState, List(event)))

    val command = new DoubleAttackActionCommand(1, manager) // Use real class here
    val result = command.executeAction(state)

    result shouldBe Some((updatedState, List(event)))
  }

  it should "return None when actionManager returns (false, ...)" in {
    val state = mock(classOf[IGameState])
    val manager = mock(classOf[IActionManager])

    when(manager.doubleAttack(state, 2)).thenReturn((false, state, Nil))

    val command = new DoubleAttackActionCommand(2, manager)
    val result = command.executeAction(state)

    result shouldBe None
  }

  it should "return None when actionManager throws an exception" in {
    val state = mock(classOf[IGameState])
    val manager = mock(classOf[IActionManager])

    when(manager.doubleAttack(state, 3)).thenThrow(new RuntimeException("fail"))

    val command = new DoubleAttackActionCommand(3, manager)
    val result = command.executeAction(state)

    result shouldBe None
  }
}
