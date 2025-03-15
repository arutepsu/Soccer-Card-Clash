package model.playingFiledComponent.manager.base

import com.google.inject.Inject
import model.cardComponent.factory.DeckFactory
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.*
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, IAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.boostStrategy.base.{DefenderBoostStrategy, GoalkeeperBoostStrategy}
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import play.api.libs.json.*
import play.api.libs.json.util.*

import scala.collection.mutable.ListBuffer
import scala.xml.*

class ActionManager @Inject()(val playingField: IPlayingField) extends IActionManager{
  def getPlayingField: IPlayingField = playingField

  var boostStrategy = new BoostManager(playingField)
  var attackHandler = new AttackHandler(playingField)
  var swapStrategy = new SwapManager(playingField)
  def getBoostManager: IBoostManager = boostStrategy
  override def singleAttack(defenderIndex: Int): Boolean = {
    attackHandler.executeAttack(new SingleAttackStrategy(defenderIndex))
  }

  override def doubleAttack(defenderIndex: Int): Boolean = {
    attackHandler.executeAttack(new DoubleAttackStrategy(defenderIndex))
    
  }


  override def circularSwap(cardIndex: Int): Boolean = {
    swapStrategy.swapAttacker(new CircularSwapStrategy(cardIndex))
  }

  override def handSwap(cardIndex: Int): Boolean = {
    swapStrategy.swapAttacker(new HandSwapStrategy(cardIndex))
  }


  override def boostDefender(cardIndex: Int): Boolean = {
    boostStrategy.applyBoost(new DefenderBoostStrategy(cardIndex))
  }

  override def boostGoalkeeper(): Boolean = {
    boostStrategy.applyBoost(new GoalkeeperBoostStrategy())
  }

  override def reset() : Unit = {

    attackHandler = new AttackHandler(playingField)
    swapStrategy = new SwapManager(playingField)
    boostStrategy = new BoostManager(playingField)

  }
}