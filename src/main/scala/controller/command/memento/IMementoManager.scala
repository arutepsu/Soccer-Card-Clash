package controller.command.memento

import controller.command.memento.base.Memento

trait IMementoManager {
  def createMemento(): Memento
  def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit
  def restoreGameState(memento: Memento): Unit
}
