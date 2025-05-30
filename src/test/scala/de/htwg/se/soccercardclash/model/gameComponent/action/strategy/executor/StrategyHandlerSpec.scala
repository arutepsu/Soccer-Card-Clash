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

import scala.reflect.ClassTag


class StrategyHandlerSpec extends AnyFlatSpec with Matchers {

  class DummyStrategy extends IActionStrategy {
    override def execute(
                          playingField: IGameState
                        ): (Boolean, IGameState, List[ObservableEvent]) =
      (true, playingField, List.empty)
  }

  "StrategyHandler" should "execute strategy if executor can handle it" in {
    val state = mock(classOf[IGameState])
    val strategy = new DummyStrategy

    val executor = mock(classOf[StrategyExecutor[DummyStrategy]])
    val expected = (true, state, List(mock(classOf[ObservableEvent])))

    when(executor.canHandle(strategy)).thenReturn(true)
    when(executor.execute(strategy, state)).thenReturn(expected)

    given StrategyExecutor[DummyStrategy] = executor
    given ClassTag[DummyStrategy] = ClassTag(classOf[DummyStrategy])

    val handler = new StrategyHandler[DummyStrategy]
    val result = handler.handle(strategy, state)

    result shouldBe Some(expected)
  }

  it should "delegate to next handler if executor cannot handle" in {
    val state = mock(classOf[IGameState])
    val strategy = new DummyStrategy

    val executor = mock(classOf[StrategyExecutor[DummyStrategy]])
    when(executor.canHandle(strategy)).thenReturn(false)

    given StrategyExecutor[DummyStrategy] = executor
    given ClassTag[DummyStrategy] = ClassTag(classOf[DummyStrategy])

    val handler = new StrategyHandler[DummyStrategy]

    val nextHandler = mock(classOf[IActionHandler])
    val expected = Some((true, state, List(mock(classOf[ObservableEvent]))))
    when(nextHandler.handle(strategy, state)).thenReturn(expected)

    handler.setNext(nextHandler)

    val result = handler.handle(strategy, state)
    result shouldBe expected
  }

  it should "return None if executor cannot handle and no next handler is set" in {
    val state = mock(classOf[IGameState])
    val strategy = new DummyStrategy

    val executor = mock(classOf[StrategyExecutor[DummyStrategy]])
    when(executor.canHandle(strategy)).thenReturn(false)

    given StrategyExecutor[DummyStrategy] = executor
    given ClassTag[DummyStrategy] = ClassTag(classOf[DummyStrategy])

    val handler = new StrategyHandler[DummyStrategy]
    val result = handler.handle(strategy, state)

    result shouldBe None
  }
}