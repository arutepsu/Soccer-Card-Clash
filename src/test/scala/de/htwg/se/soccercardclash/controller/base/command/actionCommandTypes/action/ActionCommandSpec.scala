package de.htwg.se.soccercardclash.controller.base.command.actionCommandTypes.action

import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ActionCommandSpec extends AnyFlatSpec with Matchers {

  trait ObservableEvent
  trait IGameState
  trait ICommand {
    def execute(state: IGameState): CommandResult
  }
  case class CommandResult(success: Boolean, state: IGameState, events: List[ObservableEvent])

  abstract class MyCommand extends ICommand {
    def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])]

    override def execute(state: IGameState): CommandResult =
      executeAction(state) match {
        case Some((updatedState, events)) => CommandResult(success = true, updatedState, events)
        case None                         => CommandResult(success = false, state, Nil)
      }
  }

  "Command.execute" should "return success=true and update state/events on Some" in {
    val initialState = mock(classOf[IGameState])
    val updatedState = mock(classOf[IGameState])
    val event = mock(classOf[ObservableEvent])

    val command = new MyCommand {
      override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] =
        Some((updatedState, List(event)))
    }

    val result = command.execute(initialState)

    result.success shouldBe true
    result.state shouldBe theSameInstanceAs(updatedState)
    assert(result.events.contains(event))

  }

  it should "return success=false and same state on None" in {
    val initialState = mock(classOf[IGameState])

    val command = new MyCommand {
      override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] =
        None
    }

    val result = command.execute(initialState)

    result.success shouldBe false
    result.state shouldBe theSameInstanceAs(initialState)
    result.events shouldBe empty
  }
}
