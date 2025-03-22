package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField

trait IAttackStrategy {
  def execute(playingField: IPlayingField): Boolean
}
