package de.htwg.se.soccercardclash.model.gameComponent.state.memento

import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento

trait IMementoManager {
  def createMemento(): Memento
  def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit
  def restoreGoalkeeperBoost(memento: Memento): Unit
  def restoreGameState(memento: Memento): Unit
}
