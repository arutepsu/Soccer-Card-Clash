package model.playingFiledComponent.manager
import model.playingFiledComponent.strategy.boostStrategy.BoostManager
import util.*

trait IActionManager{
  def attack(defenderIndex: Int): Boolean
  def doubleAttack(defenderIndex: Int): Boolean
  def circularSwap(cardIndex: Int): Unit 
  def handSwap(cardIndex: Int): Unit
  def boostDefender(cardIndex: Int): Unit 
  def boostManager : BoostManager
}
