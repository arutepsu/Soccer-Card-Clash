package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.executor

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.ActionExecutor
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar

class ActionHandlerSpec extends AnyFlatSpec with Matchers {

  "ActionHandler" should "delegate to next handler if set" in {
    val state = mock(classOf[IGameState])
    val strategy = mock(classOf[IActionStrategy])
    val nextHandler = mock(classOf[IActionHandler])
    val expectedResult = Some((true, state, List.empty[ObservableEvent]))

    when(nextHandler.handle(strategy, state)).thenReturn(expectedResult)

    val handler = new TestDelegatingHandler
    handler.setNext(nextHandler)

    val result = handler.handle(strategy, state)
    result shouldBe expectedResult
  }

  it should "return None if no next handler is set" in {
    val state = mock(classOf[IGameState])
    val strategy = mock(classOf[IActionStrategy])

    val handler = new TestDelegatingHandler
    val result = handler.handle(strategy, state)

    result shouldBe None
  }

  it should "support method chaining with setNext" in {
    val handler1 = new TestDelegatingHandler
    val handler2 = new TestDelegatingHandler

    val chained = handler1.setNext(handler2)
    chained shouldBe theSameInstanceAs(handler2)
  }
  "ActionExecutor.execute" should "delegate to handler and return result" in {
    val state = mock(classOf[IGameState])
    val strategy = mock(classOf[IActionStrategy])
    val event = mock(classOf[ObservableEvent])
    val handler = mock(classOf[IActionHandler])

    when(handler.handle(strategy, state)).thenReturn(Some((true, state, List(event))))

    val executor = new ActionExecutor(handler)
    val (success, newState, events) = executor.execute(strategy, state)

    success shouldBe true
    newState shouldBe state
    events shouldBe List(event)
  }

  it should "return (false, state, Nil) when handler returns None" in {
    val state = mock(classOf[IGameState])
    val strategy = mock(classOf[IActionStrategy])
    val handler = mock(classOf[IActionHandler])

    when(handler.handle(strategy, state)).thenReturn(None)

    val executor = new ActionExecutor(handler)
    val (success, newState, events) = executor.execute(strategy, state)

    success shouldBe false
    newState shouldBe state
    events shouldBe empty
  }
}
