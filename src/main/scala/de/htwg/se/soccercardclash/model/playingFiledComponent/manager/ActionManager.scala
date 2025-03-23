package de.htwg.se.soccercardclash.model.playingFiledComponent.manager

import com.google.inject.Inject
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.playingFiledComponent.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IActionManager
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, IAttackStrategy}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base.{DefenderBoostStrategy, GoalkeeperBoostStrategy}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.base.{ReverseSwapStrategy, RegularSwapStrategy}
import play.api.libs.json.*
import play.api.libs.json.util.*

import scala.collection.mutable.ListBuffer
import scala.xml.*

class ActionManager @Inject()(val playingField: IPlayingField, val playerActionService: IPlayerActionManager) extends IActionManager{
  override def getPlayingField: IPlayingField = playingField
  override def getPlayerActionService: IPlayerActionManager = playerActionService
  override def getBoostManager: IBoostManager = boostStrategy
  
  var boostStrategy = new BoostManager(playingField)
  var attackHandler = new AttackHandler(playingField)
  var swapStrategy = new SwapManager(playingField)

  override def singleAttack(defenderIndex: Int): Boolean = {
    attackHandler.executeAttack(new SingleAttackStrategy(defenderIndex))
  }

  override def doubleAttack(defenderIndex: Int, playerActionService: IPlayerActionManager): Boolean = {
    attackHandler.executeAttack(new DoubleAttackStrategy(defenderIndex, playerActionService))
    
  }

  override def reverseSwap(playerActionService: IPlayerActionManager): Boolean = {
    swapStrategy.swapAttacker(new ReverseSwapStrategy(playerActionService))
  }

  override def regularSwap(cardIndex: Int, playerActionService: IPlayerActionManager): Boolean = {
    swapStrategy.swapAttacker(new RegularSwapStrategy(cardIndex, playerActionService))
  }


  override def boostDefender(cardIndex: Int, playerActionService: IPlayerActionManager): Boolean = {
    boostStrategy.applyBoost(new DefenderBoostStrategy(cardIndex, playerActionService))
  }

  override def boostGoalkeeper(playerActionService: IPlayerActionManager): Boolean = {
    boostStrategy.applyBoost(new GoalkeeperBoostStrategy(playerActionService))
  }

  override def reset() : Unit = {

    attackHandler = new AttackHandler(playingField)
    swapStrategy = new SwapManager(playingField)
    boostStrategy = new BoostManager(playingField)

  }
}
trait IActionManager{
  def getPlayingField: IPlayingField
  def getPlayerActionService: IPlayerActionManager
  def getBoostManager: IBoostManager
  def singleAttack(defenderIndex: Int): Boolean
  def doubleAttack(defenderIndex: Int, playerActionService: IPlayerActionManager): Boolean
  def reverseSwap(playerActionService: IPlayerActionManager): Boolean
  def regularSwap(cardIndex: Int, playerActionService: IPlayerActionManager): Boolean
  def boostDefender(cardIndex: Int, playerActionService: IPlayerActionManager): Boolean
  def boostGoalkeeper(playerActionService: IPlayerActionManager): Boolean
  def reset(): Unit
}
