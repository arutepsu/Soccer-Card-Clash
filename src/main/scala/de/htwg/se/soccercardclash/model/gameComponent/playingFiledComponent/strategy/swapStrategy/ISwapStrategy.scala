package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
trait ISwapStrategy {
  def swap(playingField: IPlayingField): Boolean
}