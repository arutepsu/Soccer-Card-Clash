package de.htwg.se.soccercardclash.controller.command.memento

import de.htwg.se.soccercardclash.controller.command.memento.base.Memento

trait IMementoManager {
  def createMemento(): Memento
  def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit
  def restoreGoalkeeperBoost(memento: Memento): Unit
  def restoreGameState(memento: Memento): Unit
}
