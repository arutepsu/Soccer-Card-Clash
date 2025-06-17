package de.htwg.se.soccercardclash.controller.base.command.workflow

import de.htwg.se.soccercardclash.controller.command.workflow.{ExitGameStrategy, ExitStrategy}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExitGameStrategySpec extends AnyFlatSpec with Matchers {

  "ExitGameStrategy" should "be a valid ExitStrategy instance" in {
    ExitGameStrategy shouldBe a[ExitStrategy]
  }
}