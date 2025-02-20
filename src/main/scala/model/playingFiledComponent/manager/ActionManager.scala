package model.playingFiledComponent.manager
import play.api.libs.json._
import scala.xml._
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.*
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, AttackStrategy, DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import util.*

import scala.collection.mutable.ListBuffer

class ActionManager(val playingField: IPlayingField) extends Serializable{

  val boostManager = new BoostManager(playingField, playingField.getRoles, playingField.getDataManager)
  val getPlayingField: IPlayingField = playingField
  var attackHandler = new AttackHandler(new SingleAttackStrategy())
  var swapHandler = new SwapHandler(playingField, playingField.getRoles)

  def attack(defenderIndex: Int): Boolean = {
    attackHandler.setStrategy(new SingleAttackStrategy())
    val success = attackHandler.executeAttack(playingField, defenderIndex)
    success
  }

  def doubleAttack(defenderIndex: Int): Boolean = {
    attackHandler.setStrategy(new DoubleAttackStrategy())
    val success = attackHandler.executeAttack(playingField, defenderIndex)
    attackHandler.setStrategy(new SingleAttackStrategy())
    success
  }

  def circularSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new CircularSwapStrategy())
    swapHandler.swapAttacker(cardIndex)
  }

  def handSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new HandSwapStrategy())
    swapHandler.swapAttacker(cardIndex)
  }

  private def setSwapStrategy(strategy: SwapStrategy): Unit = {
    swapHandler.setSwapStrategy(strategy)
  }

  def boostDefender(cardIndex: Int): Unit = {
    boostManager.chooseBoostCardDefender(cardIndex)
  }

}