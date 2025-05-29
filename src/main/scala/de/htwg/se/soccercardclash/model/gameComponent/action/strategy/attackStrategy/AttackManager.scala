package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.IAttackStrategy
import de.htwg.se.soccercardclash.util.ObservableEvent

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton
class AttackManager @Inject()() extends IAttackManager {
  override def executeAttack(strategy: IAttackStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    strategy.execute(state)
  }
}

trait IAttackManager {
  def executeAttack(strategy: IAttackStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
