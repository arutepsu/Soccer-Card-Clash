package de.htwg.se.soccercardclash.controller.base.command.actionCommandTypes.action

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ActionCommandSpec extends AnyFlatSpec with MockitoSugar {

  "ActionCommand" should "return success when executeAction returns Some(updatedState, events)" in {
    val initialState = mock[IGameState]
    val updatedState = mock[IGameState]
    val events = List(GameActionEvent.RegularAttack)

    val command = new ActionCommand {
      override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] =
        Some((updatedState, events))
    }

    val result = command.execute(initialState)

    assert(result.success)
    assert(result.state eq updatedState)
    assert(result.events == events)
  }

  it should "return failure when executeAction returns None" in {
    val initialState = mock[IGameState]

    val command = new ActionCommand {
      override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] =
        None
    }

    val result = command.execute(initialState)

    assert(!result.success)
    assert(result.state eq initialState)
    assert(result.events.isEmpty)
  }
}
