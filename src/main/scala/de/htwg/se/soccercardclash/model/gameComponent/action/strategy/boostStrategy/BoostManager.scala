package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{GameCards, IGameCards, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.ObservableEvent

import javax.inject.{Inject, Singleton}

trait IBoostManager {
  def getRevertStrategy(state: IGameState): IRevertStrategy

  def applyBoost(boostStrategy: IBoostStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}

@Singleton
class BoostManager @Inject()() extends IBoostManager {
  override def getRevertStrategy(state: IGameState): IRevertStrategy =
    new RevertBoostStrategy(state)

  override def applyBoost(boostStrategy: IBoostStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent]) =
    boostStrategy.boost(state)
}