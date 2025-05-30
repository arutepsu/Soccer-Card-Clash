package de.htwg.se.soccercardclash.controller.base.command

import de.htwg.se.soccercardclash.controller.command.CommandFactory
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.*
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.*
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands.*
import de.htwg.se.soccercardclash.controller.command.workflow.*
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.IRevertBoostStrategyFactory
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar

class CommandFactorySpec extends AnyFlatSpec with Matchers {

  val mockGameService = mock(classOf[IGameService])
  val mockActionExecutor = mock(classOf[IActionExecutor])
  val mockPlayerActionManager = mock(classOf[IPlayerActionManager])
  val mockRevertBoostFactory = mock(classOf[IRevertBoostStrategyFactory])

  val factory = new CommandFactory(
    mockGameService,
    mockActionExecutor,
    mockPlayerActionManager,
    mockRevertBoostFactory
  )


  "CommandFactory" should "create SingleAttackActionCommand with correct index" in {
    val cmd = factory.createSingleAttackCommand(2)
    cmd shouldBe a[SingleAttackActionCommand]
  }

  it should "create DoubleAttackActionCommand with correct index" in {
    val cmd = factory.createDoubleAttackCommand(1)
    cmd shouldBe a[DoubleAttackActionCommand]
  }

  it should "create BoostDefenderActionCommand with correct index" in {
    val cmd = factory.createBoostDefenderCommand(0)
    cmd shouldBe a[BoostDefenderActionCommand]
  }

  it should "create BoostGoalkeeperActionCommand" in {
    val cmd = factory.createBoostGoalkeeperCommand()
    cmd shouldBe a[BoostGoalkeeperActionCommand]
  }

  it should "create RegularSwapActionCommand with correct index" in {
    val cmd = factory.createRegularSwapCommand(3)
    cmd shouldBe a[RegularSwapActionCommand]
  }

  it should "create ReverseSwapActionCommand" in {
    val cmd = factory.createReverseSwapCommand()
    cmd shouldBe a[ReverseSwapActionCommand]
  }

  it should "create CreateGameWorkflowCommand with players" in {
    val cmd = factory.createCreateGameCommand("A", "B")
    cmd shouldBe a[CreateGameWorkflowCommand]
  }

  it should "create QuitWorkflowCommand" in {
    val cmd = factory.createQuitCommand()
    cmd shouldBe a[QuitWorkflowCommand]
  }

  it should "create SaveGameWorkflowCommand" in {
    val cmd = factory.createSaveGameCommand()
    cmd shouldBe a[SaveGameWorkflowCommand]
  }

  it should "create LoadGameWorkflowCommand with filename" in {
    val cmd = factory.createLoadGameCommand("game.json")
    cmd shouldBe a[LoadGameWorkflowCommand]
  }
}