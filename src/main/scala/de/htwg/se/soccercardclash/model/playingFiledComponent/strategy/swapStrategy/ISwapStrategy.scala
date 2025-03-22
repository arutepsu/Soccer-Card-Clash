package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
trait ISwapStrategy {
  def swap(playingField: IPlayingField): Boolean
}