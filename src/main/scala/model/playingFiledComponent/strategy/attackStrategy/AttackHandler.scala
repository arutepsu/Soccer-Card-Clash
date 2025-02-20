package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.AttackStrategy

import scala.util.{Failure, Success, Try}

class AttackHandler(var strategy: AttackStrategy) {
  def setStrategy(newStrategy: AttackStrategy): Unit = {
    this.strategy = newStrategy
  }

  def executeAttack(playingField: IPlayingField, defenderIndex: Int): Boolean = {
    strategy.execute(playingField, defenderIndex)
  }
}