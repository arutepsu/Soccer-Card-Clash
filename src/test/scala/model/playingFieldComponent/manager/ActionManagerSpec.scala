package model.playingFieldComponent.manager

import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{ActionManager, IActionManager}
import model.playingFiledComponent.strategy.boostStrategy.IBoostManager
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ActionManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ActionManager" should {

    "initialize and return the correct playing field" in {
      val mockField = mock[IPlayingField]
      val manager = new ActionManager(mockField)

      manager.getPlayingField shouldBe mockField
    }

    "reset all internal strategies correctly" in {
      val mockField = mock[IPlayingField]
      val manager = new ActionManager(mockField)

      val oldBoostManager = manager.getBoostManager

      manager.reset()

      val newBoostManager = manager.getBoostManager

      oldBoostManager should not be theSameInstanceAs(newBoostManager)
    }

  }
}
