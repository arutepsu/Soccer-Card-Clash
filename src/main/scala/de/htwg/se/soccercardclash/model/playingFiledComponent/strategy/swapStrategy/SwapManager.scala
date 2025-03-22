package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import scala.collection.mutable
class SwapManager(playingField: IPlayingField) extends ISwapManager {
  override def swapAttacker(strategy: ISwapStrategy): Boolean = {
    strategy.swap(playingField)
  }
}
trait ISwapManager{
  def swapAttacker(strategy: ISwapStrategy): Boolean
}

