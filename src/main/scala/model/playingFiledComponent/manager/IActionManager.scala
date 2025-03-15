package model.playingFiledComponent.manager
import model.playingFiledComponent.strategy.boostStrategy.IBoostManager
import util.*
import model.playingFiledComponent.IPlayingField
trait IActionManager{
  def getPlayingField: IPlayingField
  def singleAttack(defenderIndex: Int): Boolean
  def doubleAttack(defenderIndex: Int): Boolean
  def circularSwap(cardIndex: Int): Boolean
  def handSwap(cardIndex: Int): Boolean
  def boostDefender(cardIndex: Int): Boolean
  def boostGoalkeeper(): Boolean
  def getBoostManager: IBoostManager
  def reset(): Unit
}
