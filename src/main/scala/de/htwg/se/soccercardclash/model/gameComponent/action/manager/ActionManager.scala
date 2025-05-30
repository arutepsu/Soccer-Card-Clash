package de.htwg.se.soccercardclash.model.gameComponent.action.manager

import com.google.inject.Inject
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.{AttackManager, IAttackManager, IAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.base.{DefenderBoostStrategy, GoalkeeperBoostStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.base.{RegularSwapStrategy, ReverseSwapStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.state.*
import de.htwg.se.soccercardclash.model.gameComponent.action.IActionHandler
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.util.ObservableEvent
import play.api.libs.json.*
import play.api.libs.json.util.*

import scala.collection.mutable.ListBuffer
import scala.xml.*

//trait IActionManagerFactory {
//  def create(): IActionManager
//}
//
//class ActionManagerFactory @Inject()(
//                                      playerActionService: IPlayerActionManager,
//                                      attackHandler: IAttackManager,
//                                      boostManager: IBoostManager,
//                                      swapManager: ISwapManager
//                                    ) extends IActionManagerFactory {
//
//  override def create(): IActionManager =
//    new ActionManager(playerActionService, attackHandler, boostManager, swapManager)
//}
trait IActionManagerFactory {
  def create(): IActionManager
}

class ActionManagerFactory @Inject()(
                                      handlerChain: IActionHandler
                                    ) extends IActionManagerFactory {

  override def create(): IActionManager =
    new ActionManager(handlerChain)
}
//@Singleton
class ActionManager @Inject() (
                                handlerChain: IActionHandler
                              ) extends IActionManager {

  override def execute(strategy: IActionStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    handlerChain.handle(strategy, state).getOrElse {
      (false, state, List.empty)
    }
  }
}
trait IActionManager {
  def execute(strategy: IActionStrategy, state: IGameState): (Boolean, IGameState, List[ObservableEvent])
}
//class ActionManager @Inject()(
//                               playerActionService: IPlayerActionManager,
//                               attackHandler: IAttackManager,
//                               boostManager: IBoostManager,
//                               swapManager: ISwapManager
//                             ) extends IActionManager {
//
//  override def getPlayerActionService: IPlayerActionManager = playerActionService
//
//  override def getBoostManager: IBoostManager = boostManager
//
//  override def singleAttack(playingField: IGameState, defenderIndex: Int): (Boolean, IGameState, List[ObservableEvent]) = {
//    attackHandler.executeAttack(new SingleAttackStrategy(defenderIndex, boostManager), playingField)
//  }
//
//  override def doubleAttack(playingField: IGameState, defenderIndex: Int): (Boolean, IGameState, List[ObservableEvent]) = {
//    attackHandler.executeAttack(new DoubleAttackStrategy(defenderIndex, playerActionService, boostManager), playingField)
//  }
//
//  override def reverseSwap(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
//    swapManager.swapAttacker(new ReverseSwapStrategy(playerActionService), playingField)
//  }
//
//  override def regularSwap(playingField: IGameState, cardIndex: Int): (Boolean, IGameState, List[ObservableEvent]) = {
//    swapManager.swapAttacker(new RegularSwapStrategy(cardIndex, playerActionService), playingField)
//  }
//
//  override def boostDefender(playingField: IGameState, cardIndex: Int): (Boolean, IGameState, List[ObservableEvent]) = {
//    boostManager.applyBoost(
//      new DefenderBoostStrategy(cardIndex, playerActionService),
//      playingField
//    )
//  }
//
//  override def boostGoalkeeper(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
//    boostManager.applyBoost(new GoalkeeperBoostStrategy(playerActionService), playingField)
//  }
//}
//
//trait IActionManager {
//  def getPlayerActionService: IPlayerActionManager
//
//  def getBoostManager: IBoostManager
//
//  def singleAttack(playingField: IGameState, defenderIndex: Int): (Boolean, IGameState, List[ObservableEvent])
//
//  def doubleAttack(playingField: IGameState, defenderIndex: Int): (Boolean, IGameState, List[ObservableEvent])
//
//  def reverseSwap(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent])
//
//  def regularSwap(playingField: IGameState, cardIndex: Int): (Boolean, IGameState, List[ObservableEvent])
//
//  def boostDefender(playingField: IGameState, cardIndex: Int): (Boolean, IGameState, List[ObservableEvent])
//
//  def boostGoalkeeper(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent])
//
//}
