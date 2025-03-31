package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField

trait IAttackStrategy {
  def execute(playingField: IPlayingField): Boolean
}
