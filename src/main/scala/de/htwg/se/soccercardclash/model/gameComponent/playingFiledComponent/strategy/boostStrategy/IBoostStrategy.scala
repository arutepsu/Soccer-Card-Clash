package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
trait IBoostStrategy {
  def boost(playingField: IPlayingField): Boolean
}
