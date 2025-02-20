package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.IPlayingField

trait AttackStrategy {
  def execute(playingField: IPlayingField, defenderIndex: Int): Boolean
}