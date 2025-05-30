package de.htwg.se.soccercardclash.controller.base.command.actionCommandTypes.attackActionCommands
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.SingleAttackActionCommand
import scala.util.{Failure, Success, Try}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.util.ObservableEvent
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.controller.command.CommandResult
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionExecutor

class SingleAttackActionCommandSpec extends AnyFlatSpec with Matchers {

  "SingleAttackActionCommand" should "return Some(...) when actionManager returns success" in {
    val state = mock(classOf[IGameState])
    val updatedState = mock(classOf[IGameState])
    val event = mock(classOf[ObservableEvent])
    val manager = mock(classOf[IActionExecutor])

    when(manager.singleAttack(state, 0)).thenReturn((true, updatedState, List(event)))

    val command = new SingleAttackActionCommand(0, manager)
    val result = command.executeAction(state)

    result shouldBe Some((updatedState, List(event)))
  }

  it should "return None when actionManager returns (false, ...)" in {
    val state = mock(classOf[IGameState])
    val manager = mock(classOf[IActionExecutor])

    when(manager.singleAttack(state, 1)).thenReturn((false, state, Nil))

    val command = new SingleAttackActionCommand(1, manager)
    val result = command.executeAction(state)

    result shouldBe None
  }

  it should "return None when actionManager throws an exception" in {
    val state = mock(classOf[IGameState])
    val manager = mock(classOf[IActionExecutor])

    when(manager.singleAttack(state, 2)).thenThrow(new RuntimeException("fail"))

    val command = new SingleAttackActionCommand(2, manager)
    val result = command.executeAction(state)

    result shouldBe None
  }
}
