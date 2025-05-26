package de.htwg.se.soccercardclash.tui.tuiCommandTypes
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, RegularSwapTuiCommand, ReverseSwapTuiCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ReverseSwapTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ReverseSwapTuiCommand" should {

    "always call controller.reverseSwap regardless of input" in {
      val controller = mock[IController]
      val context = mock[GameContext]
      val contextHolder = mock[IGameContextHolder]
      when(contextHolder.get).thenReturn(context)

      val cmd = new ReverseSwapTuiCommand(controller, contextHolder)

      cmd.execute(Some("1"))
      cmd.execute(None)

      verify(controller, times(2)).reverseSwap(context)
    }
  }
}
