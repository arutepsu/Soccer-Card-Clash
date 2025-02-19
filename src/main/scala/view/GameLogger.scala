package view

import model.playerComponent.base.Player
import util.GameObserver
import util.GameEvent


class GameLogger extends GameObserver {

  override def update(event: GameEvent): Unit = event match {
    case util.AttackEvent(attacker, defender) =>
      println(s"⚔️ ${attacker.name} attacked ${defender.name}!")

    case util.SwapEvent(player) =>
      println(s"🔄 ${player.name} performed a swap!")

    case util.BoostEvent(player) =>
      println(s"⚡ ${player.name} used a boost!")
  }
}

