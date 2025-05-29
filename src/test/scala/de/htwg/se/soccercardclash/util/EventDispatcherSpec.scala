package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class EventDispatcherSpec extends AnyWordSpec with Matchers {
  class DummyController extends Observable with IController {
    val received = scala.collection.mutable.Buffer[ObservableEvent]()

    override def notifyObservers(event: ObservableEvent): Unit = {
      received += event
      super.notifyObservers(event)
    }

    // Implement other methods with dummy behavior
    override def createGame(p1: String, p2: String): Unit = ()
    override def createGameWithAI(human: String, ai: String): Unit = ()
    override def singleAttack(i: Int, ctx: GameContext) = (ctx, true)
    override def doubleAttack(i: Int, ctx: GameContext) = (ctx, true)
    override def boostDefender(i: Int, ctx: GameContext) = (ctx, true)
    override def boostGoalkeeper(ctx: GameContext) = (ctx, true)
    override def regularSwap(i: Int, ctx: GameContext) = (ctx, true)
    override def reverseSwap(ctx: GameContext) = (ctx, true)
    override def undo(ctx: GameContext) = ctx
    override def redo(ctx: GameContext) = ctx
    override def saveGame(ctx: GameContext): Boolean = true
    override def loadGame(fileName: String): Boolean = true
    override def quit(): Unit = ()
    override def executeAIAction(a: AIAction, ctx: GameContext) = (ctx, true)
  }

  "EventDispatcher" should {

    "dispatch a SceneSwitchEvent to GlobalObservable" in {
      var triggered = false
      GlobalObservable.add(_ => triggered = true)

      val controller = new DummyController
      val sceneEvent = mock(classOf[SceneSwitchEvent])

      EventDispatcher.dispatchSingle(controller, sceneEvent)

      triggered shouldBe true
    }

    "dispatch a SceneSwitchEvent using GlobalObservable" in {
      val controller = new DummyController
      val sceneEvent = mock(classOf[SceneSwitchEvent])

      var triggered = false
      GlobalObservable.add { e =>
        if (e eq sceneEvent) triggered = true
      }

      EventDispatcher.dispatchSingle(controller, sceneEvent)

      assert(triggered)
    }

    "dispatch a list of events to controller" in {
      val controller = new DummyController
      val event1 = new ObservableEvent {}
      val event2 = new ObservableEvent {}

      EventDispatcher.dispatch(controller, List(event1, event2))

      controller.received should contain theSameElementsAs List(event1, event2)
    }


  }
}

