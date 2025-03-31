package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.base

import com.google.inject.{Inject, Provider, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.IPlayingFieldManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IRolesManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.{AttackHandler, IAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.*
import de.htwg.se.soccercardclash.util.Observable
import play.api.libs.json.*

import scala.compiletime.summonInline

class PlayingField @Inject()(
                              private var player1: IPlayer,
                              private var player2: IPlayer
                            )(using manager: IPlayingFieldManagerFactory) extends IPlayingField {

  private val dataManager: IDataManager = 
    summon[IPlayingFieldManagerFactory].createDataManager(this, player1, player2)
  private val actionManager: IActionManager =
    summon[IPlayingFieldManagerFactory].createActionManager(this)
  private val roles: IRolesManager =
    summon[IPlayingFieldManagerFactory].createRolesManager(this, player1, player2)
  private val scores: IPlayerScores = 
    summon[IPlayingFieldManagerFactory].createScoresManager(this, player1, player2)

  override def setPlayingField(): Unit = {
    dataManager.initializeFields()
  }
  override def getDataManager: IDataManager = dataManager

  override def getActionManager: IActionManager = actionManager

  override def getRoles: IRolesManager = roles

  override def getScores: IPlayerScores = scores

  override def reset(): Unit = {
    
    player1 = null.asInstanceOf[IPlayer]
    player2 = null.asInstanceOf[IPlayer]
    
    dataManager.clearAll()
    actionManager.reset()
    roles.reset()
    scores.reset()
    
    notifyObservers()
    
  }

}
