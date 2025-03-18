package view.gui.actionButtons

import controller.IController
import view.gui.scenes.{AttackerDefendersScene, AttackerHandScene, PlayingFieldScene}
import view.gui.components.uiFactory.GameButtonFactory
import scalafx.scene.control.Button
object ActionButtonFactory {

  def createAttackButton(
                          action: ActionButton[PlayingFieldScene],
                          buttonText: String,
                          buttonWidth: Double,
                          buttonHeight: Double,
                          playingFieldScene: PlayingFieldScene,
                          controller: IController
                        ): Button = {
    GameButtonFactory.createGameButton(
      text = buttonText,
      width = buttonWidth,
      height = buttonHeight
    ) { () =>
      action.execute(controller, playingFieldScene)
      playingFieldScene.updateDisplay()
    }
  }

  def createDoubleAttackButton(
                                action: ActionButton[PlayingFieldScene],
                                buttonText: String,
                                buttonWidth: Double,
                                buttonHeight: Double,
                                playingFieldScene: PlayingFieldScene,
                                controller: IController
                              ): Button = {
    GameButtonFactory.createGameButton(
      text = buttonText,
      width = buttonWidth,
      height = buttonHeight
    ) { () =>
      action.execute(controller, playingFieldScene)
      playingFieldScene.updateDisplay()
    }
  }

  def createRegularSwapButton(
                        action: ActionButton[AttackerHandScene],
                        buttonText: String,
                        buttonWidth: Double,
                        buttonHeight: Double,
                        attackerHandScene: AttackerHandScene,
                        controller: IController
                      ): Button = {
    GameButtonFactory.createGameButton(
      text = buttonText,
      width = buttonWidth,
      height = buttonHeight
    ) { () =>
      action.execute(controller, attackerHandScene)
    }
  }

  def createReverseSwapButton(
                               action: ActionButton[AttackerHandScene],
                               buttonText: String,
                               buttonWidth: Double,
                               buttonHeight: Double,
                               attackerHandScene: AttackerHandScene,
                               controller: IController
                             ): Button = {
    GameButtonFactory.createGameButton(
      text = buttonText,
      width = buttonWidth,
      height = buttonHeight
    ) { () =>
      action.execute(controller, attackerHandScene)
    }
  }

  def createBoostButton(
                         action: ActionButton[AttackerDefendersScene],
                         buttonText: String,
                         buttonWidth: Double,
                         buttonHeight: Double,
                         attackerDefendersScene: AttackerDefendersScene,
                         controller: IController
                      ): Button = {
    GameButtonFactory.createGameButton(
      text = buttonText,
      width = buttonWidth,
      height = buttonHeight
    ) { () =>
      action.execute(controller, attackerDefendersScene)

    }
  }
}
