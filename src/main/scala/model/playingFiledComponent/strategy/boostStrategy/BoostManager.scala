package model.playingFiledComponent.strategy.boostStrategy

import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{DataManager, IDataManager, IRolesManager, RolesManager}
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