package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
trait IBoostStrategy {
  def boost(playingField: IPlayingField): Boolean
}
