package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{DataManager, IDataManager, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.ObservableEvent

import javax.inject.{Inject, Singleton}

trait IBoostManager {
  def getRevertStrategy(playingField: IGameState): IRevertStrategy

  def applyBoost(boostStrategy: IBoostStrategy, playingField: IGameState): (Boolean, IGameState, List[ObservableEvent])
}

@Singleton
class BoostManager @Inject()() extends IBoostManager {
  override def getRevertStrategy(playingField: IGameState): IRevertStrategy =
    new RevertBoostStrategy(playingField)

  override def applyBoost(boostStrategy: IBoostStrategy, playingField: IGameState): (Boolean, IGameState, List[ObservableEvent]) =
    boostStrategy.boost(playingField)
}