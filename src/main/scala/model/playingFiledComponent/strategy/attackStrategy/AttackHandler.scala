package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.strategy.attackStrategy.AttackStrategy

import scala.util.{Failure, Success, Try}

class AttackHandler(var strategy: AttackStrategy) {
  def setStrategy(newStrategy: AttackStrategy): Unit = {
    this.strategy = newStrategy
  }

  def executeAttack(playingField: PlayingField, defenderIndex: Int): Boolean = {
    strategy.execute(playingField, defenderIndex)
  }
}