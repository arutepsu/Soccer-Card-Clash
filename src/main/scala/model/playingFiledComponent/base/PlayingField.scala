package model.playingFiledComponent.base

import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{ActionManager, DataManager}
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, AttackStrategy, DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import util.Observable

import scala.collection.mutable
import scala.util.Try

class PlayingField(
                    val player1: IPlayer,
                    val player2: IPlayer
                  ) extends IPlayingField {
  private val dataManager = new DataManager(this, player1, player2)
  private val actionManager = new ActionManager(this)
  private val roles = new RolesManager(this, player1, player2)
  private val scores = new PlayerScores(this, player1, player2)

  override def setPlayingField(): Unit = {
    dataManager.initializeFields()
  }

  override def getAttacker: IPlayer = roles.attacker

  override def getDefender: IPlayer = roles.defender

  override def getDataManager: DataManager = dataManager

  override def getActionManager: ActionManager = actionManager

  override def getRoles: RolesManager = roles

  override def getScores: PlayerScores = scores

  override def toJson: JsObject = super.toJson

  override def toXml: Elem = super.toXml
}

