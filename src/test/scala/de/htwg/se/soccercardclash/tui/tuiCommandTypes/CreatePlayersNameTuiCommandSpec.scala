//package de.htwg.se.soccercardclash.tui.tuiCommandTypes
//import de.htwg.se.soccercardclash.controller.IController
//import de.htwg.se.soccercardclash.controller.base.Controller
//import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
//import de.htwg.se.soccercardclash.util.{IGameContextHolder, Observable}
//import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
//import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.{AttackTuiCommand, BoostDefenderTuiCommand, BoostGoalkeeperTuiCommand, CreatePlayersNameTuiCommand}
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import org.mockito.Mockito.*
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.Mockito.{mock => rawMock, withSettings}
//
//class CreatePlayersNameTuiCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {
//
//  "CreatePlayersNameTuiCommand" should {
//
//    "trigger scene switch and prompt for names when executed" in {
//      val controller = rawMock(classOf[IController], withSettings().extraInterfaces(classOf[Observable]))
//      val context = mock[IGameContextHolder]
//      val cmd = new CreatePlayersNameTuiCommand(controller, context)
//
//      val out = new java.io.ByteArrayOutputStream()
//      Console.withOut(out) {
//        cmd.execute()
//      }
//
//      out.toString should include("Please enter players names")
//    }
//
//    "accept valid player names and start game" in {
//      val controller = rawMock(classOf[IController], withSettings().extraInterfaces(classOf[Observable]))
//      val context = mock[IGameContextHolder]
//      val cmd = new CreatePlayersNameTuiCommand(controller, context)
//
//      // Execute first to set waiting state
//      cmd.execute()
//
//      val out = new java.io.ByteArrayOutputStream()
//      Console.withOut(out) {
//        val result = cmd.handlePlayerNames("Alice Bob")
//        result shouldBe true
//      }
//
//      out.toString should include("Players set: Alice & Bob")
//    }
//
//    "reject invalid player name input and print error" in {
//      val controller = rawMock(classOf[IController], withSettings().extraInterfaces(classOf[Observable]))
//      val context = mock[IGameContextHolder]
//      val cmd = new CreatePlayersNameTuiCommand(controller, context)
//
//      cmd.execute()
//
//      val out = new java.io.ByteArrayOutputStream()
//      Console.withOut(out) {
//        val result = cmd.handlePlayerNames("AliceOnly")
//        result shouldBe false
//      }
//
//      out.toString should include("Invalid format")
//    }
//
//    "ignore handlePlayerNames if execute was not called first" in {
//      val controller = rawMock(classOf[IController], withSettings().extraInterfaces(classOf[Observable]))
//      val context = mock[IGameContextHolder]
//      val cmd = new CreatePlayersNameTuiCommand(controller, context)
//
//      val result = cmd.handlePlayerNames("Alice Bob")
//      result shouldBe false
//    }
//  }
//}
