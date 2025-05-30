package de.htwg.se.soccercardclash.controller.base.command.actionCommandTypes.boostActionCommand
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
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.BoostDefenderActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.{IActionExecutor, IPlayerActionManager}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}

class BoostDefenderActionCommandSpec extends AnyFlatSpec with Matchers {

  "BoostDefenderActionCommand" should "return Some(...) when actionExecutor returns success" in {
    val state = mock(classOf[IGameState])
    val updatedState = mock(classOf[IGameState])
    val event = mock(classOf[ObservableEvent])
    val executor = mock(classOf[IActionExecutor])
    val playerActionManager = mock(classOf[IPlayerActionManager])

    when(executor.execute(any(), eqTo(state))).thenReturn((true, updatedState, List(event)))

    val command = new BoostDefenderActionCommand(0, executor, playerActionManager)
    val result = command.executeAction(state)

    result shouldBe Some((updatedState, List(event)))
  }

  it should "return None when actionExecutor returns (false, ...)" in {
    val state = mock(classOf[IGameState])
    val executor = mock(classOf[IActionExecutor])
    val playerActionManager = mock(classOf[IPlayerActionManager])

    when(executor.execute(any(), eqTo(state))).thenReturn((false, state, Nil))

    val command = new BoostDefenderActionCommand(1, executor, playerActionManager)
    val result = command.executeAction(state)

    result shouldBe None
  }

  it should "return None when actionExecutor throws an exception" in {
    val state = mock(classOf[IGameState])
    val executor = mock(classOf[IActionExecutor])
    val playerActionManager = mock(classOf[IPlayerActionManager])

    when(executor.execute(any(), eqTo(state))).thenThrow(new RuntimeException("fail"))

    val command = new BoostDefenderActionCommand(2, executor, playerActionManager)
    val result = command.executeAction(state)

    result shouldBe None
  }
}
