package de.htwg.se.soccercardclash.tui.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, DoubleAttackTuiCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class DoubleAttackTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "DoubleAttackTuiCommand" should {

    "execute a valid double attack with integer input" in {
      val controller = mock[IController]
      val context = mock[GameContext]
      val contextHolder = mock[IGameContextHolder]

      when(contextHolder.get).thenReturn(context)

      val cmd = new DoubleAttackTuiCommand(controller, contextHolder)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        cmd.execute(Some("2"))
      }

      output.toString should include("Executing attack on position: 2")
      verify(controller).doubleAttack(2, context)
    }

    "print error message for non-integer input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val cmd = new DoubleAttackTuiCommand(controller, contextHolder)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        cmd.execute(Some("abc"))
      }

      output.toString should include("Invalid attack format")
      verifyNoInteractions(controller)
    }

    "print error message for missing input" in {
      val controller = mock[IController]
      val contextHolder = mock[IGameContextHolder]
      val cmd = new DoubleAttackTuiCommand(controller, contextHolder)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        cmd.execute(None)
      }

      output.toString should include("Missing position")
      verifyNoInteractions(controller)
    }
  }
}
