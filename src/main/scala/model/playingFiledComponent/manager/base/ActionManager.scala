package model.playingFiledComponent.manager.base

import com.google.inject.Inject
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.*
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, AttackStrategy, DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import play.api.libs.json.*
import play.api.libs.json.util.*

import scala.collection.mutable.ListBuffer
import scala.xml.*

class ActionManager @Inject()(val playingField: IPlayingField) extends IActionManager{

  val bManager = new BoostManager(playingField, playingField.getRoles, playingField.getDataManager)
  val getPlayingField: IPlayingField = playingField
  var attackHandler = new AttackHandler(new SingleAttackStrategy())
  var swapHandler = new SwapHandler(playingField, playingField.getRoles)
  override def boostManager : BoostManager = bManager
  
  override def attack(defenderIndex: Int): Boolean = {
    attackHandler.setStrategy(new SingleAttackStrategy())
    val success = attackHandler.executeAttack(playingField, defenderIndex)
    success
  }

  override def doubleAttack(defenderIndex: Int): Boolean = {
    attackHandler.setStrategy(new DoubleAttackStrategy())
    val success = attackHandler.executeAttack(playingField, defenderIndex)
    attackHandler.setStrategy(new SingleAttackStrategy())
    success
  }

  override def circularSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new CircularSwapStrategy())
    swapHandler.swapAttacker(cardIndex)
  }

  override def handSwap(cardIndex: Int): Unit = {
    setSwapStrategy(new HandSwapStrategy())
    swapHandler.swapAttacker(cardIndex)
  }

  private def setSwapStrategy(strategy: SwapStrategy): Unit = {
    swapHandler.setSwapStrategy(strategy)
  }

  override def boostDefender(cardIndex: Int): Unit = {
    bManager.chooseBoostCardDefender(cardIndex)
  }

}