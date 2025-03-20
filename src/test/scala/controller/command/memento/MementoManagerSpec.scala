package controller.command.memento

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import controller.Events
import controller.command.memento.IMementoManager
import controller.command.memento.base.MementoManager
import controller.command.memento.base.Memento
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.gameComponent.IGame
import model.playerComponent.playerAction.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IActionManager
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.never
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.strategy.boostStrategy.IBoostManager
import model.playingFiledComponent.strategy.boostStrategy.IRevertStrategy
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import scala.collection.mutable
import model.playingFiledComponent.dataStructure.IHandCardsQueue
import model.playingFiledComponent.manager.IDataManager


//class MementoManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {
//  // Mocks
//  private val mockGame: IGame = mock[IGame]
//  private val mockPlayingField: IPlayingField = mock[IPlayingField]
//  private val mockActionManager: IActionManager = mock[IActionManager]
//  private val mockBoostManager: IBoostManager = mock[IBoostManager]
//  private val mockRevertStrategy: IRevertStrategy = mock[IRevertStrategy]
//  private val mockAttacker: IPlayer = mock[IPlayer]
//  private val mockDefender: IPlayer = mock[IPlayer]
//  private val mockDefenders: List[ICard] = List(mock[ICard], mock[ICard])
//  private val mockGoalkeeper: Option[ICard] = Some(mock[ICard])
//  private val mockScores = mock[IPlayerScores]
//  private val mockDataManager: IDataManager = mock[IDataManager]
//
//  // ✅ FIX: Use a real mutable.Queue for IHandCardsQueue instead of a Mockito mock
//  private val mockPlayer1Hand: IHandCardsQueue = new mutable.Queue[ICard]() with IHandCardsQueue
//  private val mockPlayer2Hand: IHandCardsQueue = new mutable.Queue[ICard]() with IHandCardsQueue
//
//  // ✅ Ensure `getDataManager()` is stubbed BEFORE calling its methods
//  when(mockGame.getPlayingField).thenReturn(mockPlayingField)
//  when(mockPlayingField.getDataManager).thenReturn(mockDataManager)
//
//  // ✅ Now stub methods on mockDataManager
//  when(mockDataManager.getPlayerDefenders(mockAttacker)).thenReturn(mockDefenders)
//  when(mockDataManager.getPlayerDefenders(mockDefender)).thenReturn(mockDefenders)
//  when(mockDataManager.getPlayerGoalkeeper(mockAttacker)).thenReturn(mockGoalkeeper)
//  when(mockDataManager.getPlayerGoalkeeper(mockDefender)).thenReturn(mockGoalkeeper)
//  when(mockDataManager.getPlayerHand(mockAttacker)).thenReturn(mockPlayer1Hand) // ✅ Fixed
//  when(mockDataManager.getPlayerHand(mockDefender)).thenReturn(mockPlayer2Hand) // ✅ Fixed
//
//  // Other stubs
//  when(mockPlayingField.getAttacker).thenReturn(mockAttacker)
//  when(mockPlayingField.getDefender).thenReturn(mockDefender)
//  when(mockPlayingField.getScores).thenReturn(mockScores)
//  when(mockScores.getScorePlayer1).thenReturn(10)
//  when(mockScores.getScorePlayer2).thenReturn(8)
//  when(mockAttacker.actionStates).thenReturn(Map(PlayerActionPolicies.Boost -> CanPerformAction(2)))
//  when(mockDefender.actionStates).thenReturn(Map(PlayerActionPolicies.Boost -> OutOfActions))
//
//  // ✅ Create MementoManager AFTER mocks are properly initialized
//  private val mementoManager: IMementoManager = new MementoManager(mockGame)
//
//  "MementoManager" should {
//
//    "create a valid memento" in {
//      // Add some test cards to the hand queue
//      mockPlayer1Hand.enqueue(mock[ICard], mock[ICard])
//      mockPlayer2Hand.enqueue(mock[ICard], mock[ICard])
//
//      val memento = mementoManager.createMemento()
//
//      memento.attacker shouldBe mockAttacker
//      memento.defender shouldBe mockDefender
//      memento.player1Defenders should have size 2
//      memento.player2Defenders should have size 2
//      memento.player1Goalkeeper shouldBe mockGoalkeeper
//      memento.player2Goalkeeper shouldBe mockGoalkeeper
//      memento.player1Hand should have size 2
//      memento.player2Hand should have size 2
//      memento.player1Score shouldBe 10
//      memento.player2Score shouldBe 8
//      memento.player1Actions should contain key PlayerActionPolicies.Boost
//    }
//
//    "restore boosts correctly" in {
//      val memento = mementoManager.createMemento()
//      when(mockPlayingField.getActionManager).thenReturn(mockActionManager)
//      when(mockActionManager.getBoostManager).thenReturn(mockBoostManager)
//      when(mockBoostManager.getRevertStrategy).thenReturn(mockRevertStrategy)
//
//      mementoManager.restoreBoosts(memento, 1)
//
//      verify(mockPlayingField.getRoles).setRoles(any[IPlayer], any[IPlayer])
//      verify(mockPlayingField).notifyObservers(Events.Reverted)
//    }
//
//    "not restore boost if last boosted index is invalid" in {
//      val memento = mementoManager.createMemento()
//
//      mementoManager.restoreBoosts(memento, -1)
//
//      verify(mockPlayingField.getRoles, never()).setRoles(any[IPlayer], any[IPlayer])
//      verify(mockPlayingField, never()).notifyObservers(Events.Reverted)
//    }
//
//    "restore goalkeeper boost correctly" in {
//      val memento = mementoManager.createMemento()
//      when(mockPlayingField.getActionManager).thenReturn(mockActionManager)
//      when(mockActionManager.getBoostManager).thenReturn(mockBoostManager)
//      when(mockBoostManager.getRevertStrategy).thenReturn(mockRevertStrategy)
//
//      mementoManager.restoreGoalkeeperBoost(memento)
//
//      verify(mockPlayingField.getRoles).setRoles(any[IPlayer], any[IPlayer])
//      verify(mockPlayingField).notifyObservers(Events.Reverted)
//    }
//
//    "restore game state correctly" in {
//      val memento = mementoManager.createMemento()
//
//      mementoManager.restoreGameState(memento)
//
//      verify(mockPlayingField.getRoles).setRoles(mockAttacker, mockDefender)
//      verify(mockPlayingField.getDataManager).setPlayerDefenders(mockAttacker, any[List[ICard]])
//      verify(mockPlayingField.getDataManager).setPlayerDefenders(mockDefender, any[List[ICard]])
//      verify(mockPlayingField.getDataManager).setPlayerGoalkeeper(mockAttacker, any[Option[ICard]])
//      verify(mockPlayingField.getDataManager).setPlayerGoalkeeper(mockDefender, any[Option[ICard]])
//      verify(mockPlayingField.getScores).setScorePlayer1(10)
//      verify(mockPlayingField.getScores).setScorePlayer2(8)
//      verify(mockPlayingField).notifyObservers()
//    }
//  }
//}
//
//
