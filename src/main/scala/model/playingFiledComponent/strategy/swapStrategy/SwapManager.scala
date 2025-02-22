package model.playingFiledComponent.strategy.swapStrategy

import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure.HandCardsQueue
import scala.collection.mutable
class SwapManager(playingField: IPlayingField) extends ISwapManager {
  override def swapAttacker(strategy: ISwapStrategy): Unit = {
    strategy.swap(playingField)
  }
}
trait ISwapManager{
  def swapAttacker(strategy: ISwapStrategy): Unit
}

