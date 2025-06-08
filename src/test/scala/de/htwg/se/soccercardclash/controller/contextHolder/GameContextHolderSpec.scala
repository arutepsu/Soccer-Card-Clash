package de.htwg.se.soccercardclash.controller.contextHolder

import de.htwg.se.soccercardclash.controller.contextHolder.GameContextHolder
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class GameContextHolderSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameContextHolder" should {

    "store and retrieve a context" in {
      val holder = new GameContextHolder
      val ctx = mock[GameContext]
      holder.set(ctx)
      holder.get shouldBe ctx
    }

    "throw if get is called before set" in {
      val holder = new GameContextHolder
      an [IllegalStateException] should be thrownBy holder.get
    }

    "clear context" in {
      val holder = new GameContextHolder
      val ctx = mock[GameContext]
      holder.set(ctx)
      holder.clear()
      an [IllegalStateException] should be thrownBy holder.get
    }
  }
}
