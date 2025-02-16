package controller.gameBase

import model.cardComponent.base.Card
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.Player
import model.playingFiledComponent.*
import model.playingFiledComponent.state.gameState.GameState
import model.playingFiledComponent.state.roleState.PlayerRoles
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, AttackStrategy, DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import util.{AttackEvent, BoostEvent, GameEvent, GameObserver, Observable, SwapEvent}

import scala.collection.mutable.ListBuffer

class GameController(val playingField: PlayingField) {

  private val fieldState = playingField.fieldState
  private val attackHandler = playingField.attackHandler
  private val boostManager = playingField.boostManager
  private val swapHandler = playingField.swapHandler
  val getPlayingField: PlayingField = playingField

  // ✅ List of observers
  private val observers: ListBuffer[GameObserver] = ListBuffer()

  /** ✅ Register an observer */
  def addObserver(observer: GameObserver): Unit = {
    observers += observer
  }

  /** ✅ Notify all observers when an event occurs */
  private def notifyObservers(event: GameEvent): Unit = {
    observers.foreach(_.update(event))
  }

  /** 📌 Attack Logic */
  def attack(defenderIndex: Int): Boolean = {
    val success = attackHandler.executeAttack(playingField, defenderIndex)
    if (success) {
      notifyObservers(AttackEvent(getPlayingField.getAttacker, getPlayingField.getDefender)) // ✅ Notify UI
    }
    success
  }

  def doubleAttack(defenderIndex: Int): Boolean = {
    attackHandler.setStrategy(new DoubleAttackStrategy())
    val result = attackHandler.executeAttack(playingField, defenderIndex)
    attackHandler.setStrategy(new SingleAttackStrategy()) // Reset strategy
    if (result) {
      notifyObservers(AttackEvent(getPlayingField.getAttacker, getPlayingField.getDefender)) // ✅ Notify UI
    }
    result
  }

  /** 📌 Swap Logic */
  def setSwapStrategy(strategy: SwapStrategy): Unit = {
    getPlayingField.swapHandler.setSwapStrategy(strategy)
  }

  def circularSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new CircularSwapStrategy())
    getPlayingField.swapHandler.swapAttacker(cardIndex)
    notifyObservers(SwapEvent(getPlayingField.getAttacker)) // ✅ Notify UI
  }

  def handSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new HandSwapStrategy())
    getPlayingField.swapHandler.swapAttacker(cardIndex)
    notifyObservers(SwapEvent(getPlayingField.getAttacker)) // ✅ Notify UI
  }

  /** 📌 Boost Logic */
  def boostDefender(cardIndex: Int): Unit = {
    boostManager.chooseBoostCardDefender(cardIndex)
    notifyObservers(BoostEvent(getPlayingField.getAttacker)) // ✅ Notify UI
  }
}
