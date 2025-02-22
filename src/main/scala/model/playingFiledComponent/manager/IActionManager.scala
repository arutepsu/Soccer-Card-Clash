package model.playingFiledComponent.manager
import model.playingFiledComponent.strategy.boostStrategy.IBoostManager
import util.*
import model.playingFiledComponent.IPlayingField
trait IActionManager{
  def getPlayingField: IPlayingField
  def attack(defenderIndex: Int): Boolean
  def doubleAttack(defenderIndex: Int): Boolean
  def circularSwap(cardIndex: Int): Unit 
  def handSwap(cardIndex: Int): Unit
  def boostDefender(cardIndex: Int): Unit
  def boostGoalkeeper(): Unit
  def getBoostManager: IBoostManager
}
