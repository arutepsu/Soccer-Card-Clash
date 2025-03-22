//package model.gameComponent.state
//import model.gameComponent.*
//import model.playingFiledComponent.*
//import model.playingFiledComponent.manager.*
//import model.playerComponent.IPlayer
//import model.cardComponent.ICard
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.Mockito._
//import org.mockito.ArgumentMatchers._
//import scala.collection.mutable
//import scala.collection.mutable.Queue
//import model.playingFiledComponent.dataStructure.IHandCardsQueueFactory
//import model.gameComponent.factory.IGameInitializer
//import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
//import model.playingFiledComponent.dataStructure.IHandCardsQueue
//class GameStateManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {
//
//  "GameStateManager" should {
//
//    "update and retrieve game state correctly" in {
//      val mockFactory = mock[IGameStateFactory]
//      val mockQueueFactory = mock[IHandCardsQueueFactory]
//
//      val mockGameState = mock[IGameState]
//      val mockInitializer = mock[IGameInitializer]
//      val mockPlayingField = mock[IPlayingField]
//      val mockDataManager = mock[IDataManager]
//      val mockScores = mock[IPlayerScores]
//      val p1 = mock[IPlayer]
//      val p2 = mock[IPlayer]
//      val hand1 = mock[IHandCardsQueue]
//      val hand2 = mock[IHandCardsQueue]
//
//      val card = mock[ICard]
//
//      // Setup return values
//      when(mockInitializer.getPlayingField).thenReturn(mockPlayingField)
//      when(mockInitializer.getPlayer1).thenReturn(p1)
//      when(mockInitializer.getPlayer2).thenReturn(p2)
//
//      when(mockPlayingField.getDataManager).thenReturn(mockDataManager)
//      when(mockPlayingField.getScores).thenReturn(mockScores)
//
//      when(mockDataManager.getPlayerHand(p1)).thenReturn(Queue(card))
//      when(mockDataManager.getPlayerHand(p2)).thenReturn(Queue(card))
//
//      when(mockDataManager.getPlayerDefenders(p1)).thenReturn(List(card))
//      when(mockDataManager.getPlayerDefenders(p2)).thenReturn(List(card))
//      when(mockDataManager.getPlayerGoalkeeper(p1)).thenReturn(Some(card))
//      when(mockDataManager.getPlayerGoalkeeper(p2)).thenReturn(None)
//
//      when(mockScores.getScorePlayer1).thenReturn(5)
//      when(mockScores.getScorePlayer2).thenReturn(2)
//
//      when(mockQueueFactory.create(any[List[ICard]])).thenReturn(hand1, hand2)
//
//      when(mockFactory.create(
//        mockPlayingField,
//        p1,
//        p2,
//        hand1,
//        hand2,
//        List(card),
//        List(card),
//        Some(card),
//        None,
//        5,
//        2
//      )).thenReturn(mockGameState)
//
//      val manager = new GameStateManager(mockFactory, mockQueueFactory)
//
//      manager.updateGameState(mockInitializer)
//
//      manager.getGameState shouldBe mockGameState
//    }
//
//    "reset playing field successfully when not null" in {
//      val mockFactory = mock[IGameStateFactory]
//      val mockQueueFactory = mock[IHandCardsQueueFactory]
//      val mockPlayingField = mock[IPlayingField]
//
//      val manager = new GameStateManager(mockFactory, mockQueueFactory)
//
//      val result = manager.reset(mockPlayingField)
//
//      result shouldBe true
//      verify(mockPlayingField).reset()
//    }
//
//    "not reset if playing field is null" in {
//      val mockFactory = mock[IGameStateFactory]
//      val mockQueueFactory = mock[IHandCardsQueueFactory]
//
//      val manager = new GameStateManager(mockFactory, mockQueueFactory)
//
//      val result = manager.reset(null)
//
//      result shouldBe false
//    }
//  }
//}
