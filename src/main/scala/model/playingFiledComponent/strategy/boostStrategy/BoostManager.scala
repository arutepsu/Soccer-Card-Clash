package model.playingFiledComponent.strategy.boostStrategy

import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.manager.base.DataManager
class BoostManager(playingField: IPlayingField) extends IBoostManager {
  private val revertStrategy: IRevertStrategy = new RevertBoostStrategy(playingField)
  override def getRevertStrategy: IRevertStrategy = revertStrategy
  override def applyBoost(boostStrategy: IBoostStrategy): Unit = {
    boostStrategy.boost(playingField)
  }
}
trait IBoostManager{
  def getRevertStrategy: IRevertStrategy
  def applyBoost(boostStrategy: IBoostStrategy): Unit
}