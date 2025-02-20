package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.base.PlayingField

trait AttackStrategy {
  def execute(playingField: PlayingField, defenderIndex: Int): Boolean
}