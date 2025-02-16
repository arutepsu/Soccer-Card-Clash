package model.playingFiledComponent.strategy.boostStrategy
import model.cardComponent.ICard
trait BoostStrategy {
  def boost(card: ICard): Unit
}
