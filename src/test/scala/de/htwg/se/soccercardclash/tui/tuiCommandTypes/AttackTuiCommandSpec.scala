package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.AttackTuiCommand
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class AttackTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "AttackTuiCommand" should {

    "call controller.singleAttack with valid index" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val context = mock[GameContext]

      when(contextHolder.get).thenReturn(context)

      val cmd = new AttackTuiCommand(controller, contextHolder)
      cmd.execute(Some("2"))

      verify(controller).singleAttack(2, context)
    }

    "print error message for non-integer input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val cmd = new AttackTuiCommand(controller, contextHolder)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        cmd.execute(Some("abc"))
      }

      output.toString should include("Invalid input")
      verifyNoInteractions(controller)
    }

    "print error message for missing input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val cmd = new AttackTuiCommand(controller, contextHolder)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        cmd.execute(None)
      }

      output.toString should include("Invalid input")
      verifyNoInteractions(controller)
    }
  }
}
