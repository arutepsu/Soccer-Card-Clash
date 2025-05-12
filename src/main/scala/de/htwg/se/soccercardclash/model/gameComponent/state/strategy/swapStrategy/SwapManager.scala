package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.util.ObservableEvent

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class SwapManager @Inject()() extends ISwapManager {

  override def swapAttacker(
                             strategy: ISwapStrategy,
                             playingField: IGameState
                           ): (Boolean, IGameState, List[ObservableEvent]) = {
    strategy.swap(playingField)
  }
}


trait ISwapManager {
  def swapAttacker(
                    strategy: ISwapStrategy,
                    playingField: IGameState
                  ): (Boolean, IGameState, List[ObservableEvent])
}
