//package model.gameComponent.state
//
//import model.gameComponent.factory.IGameInitializer
//import model.cardComponent.ICard
//import model.playerComponent.IPlayer
//import model.playingFiledComponent.IPlayingField
//import model.playingFiledComponent.dataStructure.{IHandCardsQueue, IHandCardsQueueFactory}
//import model.playingFiledComponent.manager.IDataManager
//import model.playingFiledComponent.manager.IActionManager
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.Mockito._
//import org.mockito.ArgumentMatchers._
//import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
//
//class GameStateManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {
//
//  "GameStateManager" should {
//
//    val mockFactory = mock[IGameStateFactory]
//    val mockHandQueueFactory = mock[IHandCardsQueueFactory]
//    val mockPlayingField = mock[IPlayingField]
//    val mockDataManager = mock[IDataManager]
//    val mockPlayer1 = mock[IPlayer]
//    val mockPlayer2 = mock[IPlayer]
//    val mockHand1 = mock[IHandCardsQueue]
//    val mockHand2 = mock[IHandCardsQueue]
//    val mockCard = mock[ICard]
//    val mockScoreManager = mock[IPlayerScores]
//    val mockGameInitializer = mock[IGameInitializer]
//    val mockGameState = mock[IGameState]
//
//    val stateManager = new GameStateManager(mockFactory, mockHandQueueFactory)
//
//    "update game state correctly" in {
//      when(mockGameInitializer.getPlayingField).thenReturn(mockPlayingField)
//      when(mockGameInitializer.getPlayer1).thenReturn(mockPlayer1)
//      when(mockGameInitializer.getPlayer2).thenReturn(mockPlayer2)
//      when(mockPlayingField.getDataManager).thenReturn(mockDataManager)
//      when(mockDataManager.getPlayerHand(mockPlayer1)).thenReturn(Seq(mockCard))
//      when(mockDataManager.getPlayerHand(mockPlayer2)).thenReturn(Seq(mockCard))
//      when(mockHandQueueFactory.create(any())).thenReturn(mockHand1, mockHand2)
//      when(mockDataManager.getPlayerDefenders(mockPlayer1)).thenReturn(List(mockCard))
//      when(mockDataManager.getPlayerDefenders(mockPlayer2)).thenReturn(List(mockCard))
//      when(mockDataManager.getPlayerGoalkeeper(mockPlayer1)).thenReturn(Some(mockCard))
//      when(mockDataManager.getPlayerGoalkeeper(mockPlayer2)).thenReturn(Some(mockCard))
//      when(mockPlayingField.getScores).thenReturn(mockScoreManager)
//      when(mockScoreManager.getScorePlayer1).thenReturn(3)
//      when(mockScoreManager.getScorePlayer2).thenReturn(4)
//      when(mockFactory.create(any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
//        .thenReturn(mockGameState)
//
//      stateManager.updateGameState(mockGameInitializer)
//      stateManager.getGameState shouldBe mockGameState
//    }
//
//    "reset playing field if not null" in {
//      when(mockPlayingField.reset()).thenReturn(())
//
//      val result = stateManager.reset(mockPlayingField)
//      result shouldBe true
//      verify(mockPlayingField).reset()
//    }
//
//    "return false when playing field is null during reset" in {
//      stateManager.reset(null) shouldBe false
//    }
//  }
//}
