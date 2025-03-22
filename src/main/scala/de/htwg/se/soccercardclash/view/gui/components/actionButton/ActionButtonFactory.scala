package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import scalafx.scene.control.Button
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.scenes.{AttackerDefendersScene, AttackerHandScene, PlayingFieldScene}
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
