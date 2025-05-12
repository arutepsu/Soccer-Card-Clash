package de.htwg.se.soccercardclash.view.gui.components.actionButton

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
case class SingleButton() extends ActionButton[PlayingFieldScene] {
  override def execute(
                        controller: IController,
                        playingFieldScene: PlayingFieldScene
                      ): Unit = {

    val contextHolder = playingFieldScene.contextHolder
    val ctx = contextHolder.get

    val playingField = ctx.state
    val attacker = playingField.getRoles.attacker
    val defender = playingField.getRoles.defender

    val defenderCards = playingField.getDataManager.getPlayerDefenders(defender)

    val targetIndex = playingFieldScene.currentDefenderFieldBar
      .flatMap(_.selectedDefenderIndex)
      .orElse(if (defenderCards.isEmpty) Some(0) else None)

    targetIndex match {
      case Some(index) =>
        println(s"⚡ Performing Single Attack on index: $index")
        val (newCtx, success) = controller.singleAttack(index, ctx)
        if (success) {
          contextHolder.set(newCtx)
          playingFieldScene.gameStatusBar.updateStatus(
            GameStatusMessages.ATTACK_INITIATED,
            newCtx.state.getRoles.attacker.name,
            newCtx.state.getRoles.defender.name
          )
          playingFieldScene.currentDefenderFieldBar.foreach(_.resetSelectedDefender())
        }
      case None =>
        println("⚠️ No defender selected for attack!")
        playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
    }
  }
}
