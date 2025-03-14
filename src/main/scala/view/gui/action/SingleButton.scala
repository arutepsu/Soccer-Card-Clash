package view.gui.action

import controller.IController
import view.gui.scenes.PlayingFieldScene
import view.gui.components.sceneBar.GameStatusBar
import view.gui.components.sceneBar.GameStatusMessages
case class SingleButton() extends ActionButton[PlayingFieldScene] {
  override def execute(
                        controller: IController,
                        playingFieldScene: PlayingFieldScene,
                        ): Unit = {

    val defenderFieldBar =
      if (playingFieldScene.playingField.getDefender == playingFieldScene.player1)
        playingFieldScene.player1FieldBar
      else
        playingFieldScene.player2FieldBar



    println(s"🎯 Defender Field Bar: ${defenderFieldBar.selectedDefenderIndex}")

    val defenderCards = playingFieldScene.playingField.getDataManager.getPlayerDefenders(playingFieldScene.playingField.getDefender)

    println("🛠️ Debug: Attacker and Defender Information")
    println(s"🟢 Attacker: ${playingFieldScene.attacker.name}")
    println(s"🔴 Defender: ${playingFieldScene.defender.name}")
    println(s"🎴 Defender's Cards: $defenderCards")

    if (defenderCards.nonEmpty) {
      println(s"📌 Checking selected defender index from: $defenderFieldBar")
      println(s"🔍 Available defenders: $defenderCards")

      defenderFieldBar.selectedDefenderIndex match {
        case Some(defenderIndex) =>
          println(s"🔥 Attacking defender at index: $defenderIndex")
          println(s"⚔️ Defender Card: ${defenderCards(defenderIndex)}")
          controller.executeSingleAttackCommand(defenderIndex)
          playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.ATTACK_INITIATED, playingFieldScene.playingField.getAttacker.name, playingFieldScene.playingField.getDefender.name)
          println("✅ Attack executed, resetting selection.")
          defenderFieldBar.resetSelectedDefender()

        case None =>
          println("❌ No defender selected! Cannot attack.")
          playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.NO_DEFENDER_SELECTED)
      }
    } else {
      println("⚽ All defenders are gone! Attacking the goalkeeper!")
      controller.executeSingleAttackCommand(0)
      playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.ATTACK_INITIATED, playingFieldScene.playingField.getAttacker.name, playingFieldScene.playingField.getDefender.name)
    }
  }
}
