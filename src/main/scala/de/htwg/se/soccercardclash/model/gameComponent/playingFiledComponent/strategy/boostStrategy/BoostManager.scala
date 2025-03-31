package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{DataManager, IDataManager, IRolesManager, RolesManager}
class BoostManager(playingField: IPlayingField) extends IBoostManager {
  private val revertStrategy: IRevertStrategy = new RevertBoostStrategy(playingField)
  override def getRevertStrategy: IRevertStrategy = revertStrategy
  override def applyBoost(boostStrategy: IBoostStrategy): Boolean = {
    boostStrategy.boost(playingField)
  }
}
trait IBoostManager{
  def getRevertStrategy: IRevertStrategy
  def applyBoost(boostStrategy: IBoostStrategy): Boolean
}