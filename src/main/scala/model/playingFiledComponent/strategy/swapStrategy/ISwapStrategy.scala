package model.playingFiledComponent.strategy.swapStrategy
import model.playingFiledComponent.IPlayingField
trait ISwapStrategy {
  def swap(playingField: IPlayingField): Boolean
}