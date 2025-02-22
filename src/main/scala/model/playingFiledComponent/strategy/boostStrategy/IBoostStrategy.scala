package model.playingFiledComponent.strategy.boostStrategy
import model.playingFiledComponent.IPlayingField
trait IBoostStrategy {
  def boost(playingField: IPlayingField): Unit
}
