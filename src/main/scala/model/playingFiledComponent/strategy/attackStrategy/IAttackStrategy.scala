package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.IPlayingField

trait IAttackStrategy {
  def execute(playingField: IPlayingField): Boolean
}
