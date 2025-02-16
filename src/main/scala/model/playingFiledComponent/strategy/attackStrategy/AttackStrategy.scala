package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.PlayingField

trait AttackStrategy {
  def execute(playingField: PlayingField, defenderIndex: Int): Boolean
}
