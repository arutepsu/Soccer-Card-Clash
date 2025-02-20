package model.playingFiledComponent.base

import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.base.Player
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.*
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, AttackStrategy, DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import util.*

import scala.collection.mutable.ListBuffer

class ActionHandler(val playingField: PlayingField) {

  private val fieldState = playingField.dataManager
  private val attackHandler = playingField.attackHandler
  private val boostManager = playingField.boostManager
  private val swapHandler = playingField.swapHandler
  val getPlayingField: PlayingField = playingField

  def attack(defenderIndex: Int): Boolean = {
    val success = attackHandler.executeAttack(playingField, defenderIndex)
    success
  }

  def doubleAttack(defenderIndex: Int): Boolean = {
    attackHandler.setStrategy(new DoubleAttackStrategy())
    val success = attackHandler.executeAttack(playingField, defenderIndex)
    attackHandler.setStrategy(new SingleAttackStrategy())
    success
  }
  
  private def setSwapStrategy(strategy: SwapStrategy): Unit = {
    getPlayingField.swapHandler.setSwapStrategy(strategy)
  }

  def circularSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new CircularSwapStrategy())
    getPlayingField.swapHandler.swapAttacker(cardIndex)
  }

  def handSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new HandSwapStrategy())
    getPlayingField.swapHandler.swapAttacker(cardIndex)
  }
  
  def boostDefender(cardIndex: Int): Unit = {
    boostManager.chooseBoostCardDefender(cardIndex)
  }
}