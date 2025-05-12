package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.util.ObservableEvent

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton
class AttackManager @Inject()() extends IAttackManager {
  override def executeAttack(strategy: IAttackStrategy, playingField: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    strategy.execute(playingField)
  }
}

trait IAttackManager {
  def executeAttack(strategy: IAttackStrategy, playingField: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
