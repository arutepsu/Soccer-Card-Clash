package de.htwg.se.soccercardclash.module

import com.google.inject.*
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameStartupDataHolder
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scalafx.stage.Stage

class SceneModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {
  "SceneModule" should {
    val mockStage = mock[Stage]

    val injector = Guice.createInjector(
      new SceneModule(mockStage),
      new TestControllerModule
    )

    "bind Stage to the provided instance" in {
      val stage = injector.getInstance(classOf[Stage])
      stage shouldBe mockStage
    }

    "provide SceneManager instance with all dependencies resolved" in {
      val sceneManager = injector.getInstance(classOf[SceneManager])
      sceneManager shouldBe a[SceneManager]
    }

    "provide singleton GameStartupDataHolder" in {
      val holder1 = injector.getInstance(classOf[GameStartupDataHolder])
      val holder2 = injector.getInstance(classOf[GameStartupDataHolder])
      holder1 shouldBe theSameInstanceAs(holder2)
    }
  }
}
