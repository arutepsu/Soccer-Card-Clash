//package model.gameComponent.factory
//
//import model.cardComponent.ICard
//import model.cardComponent.factory.IDeckFactory
//import model.playerComponent.IPlayer
//import model.playerComponent.factory.IPlayerFactory
//import model.playingFiledComponent.IPlayingField
//import model.playingFiledComponent.dataStructure.IHandCardsQueue
//import model.playingFiledComponent.factory.IPlayingFieldFactory
//import model.playingFiledComponent.manager.{IDataManager, IActionManager}
//import model.playerComponent.playerRole.IRolesManager
//import model.gameComponent.state.IGameState
//import org.mockito.Mockito._
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.ArgumentMatchers.{any, anyInt}
//
//import scala.collection.mutable
//
//class GameInitializerSpec extends AnyWordSpec with Matchers with MockitoSugar {
//
//  "GameInitializer" should {
//
//    val mockPlayerFactory = mock[IPlayerFactory]
//    val mockPlayingFieldFactory = mock[IPlayingFieldFactory]
//    val mockDeckFactory = mock[IDeckFactory]
//
//    val mockPlayer1 = mock[IPlayer]
//    val mockPlayer2 = mock[IPlayer]
//    val mockCard = mock[ICard]
//    val mockPlayingField = mock[IPlayingField]
//    val mockDataManager = mock[IDataManager]
//    val mockRoles = mock[IRolesManager]
//    val mockActionManager = mock[IActionManager]
//    val mockState = mock[IGameState]
//
//    val initializer = new GameInitializer(mockPlayerFactory, mockPlayingFieldFactory, mockDeckFactory)
//
//    val mockDeck = mutable.Queue.fill(52)(mockCard)
//
//    when(mockDeckFactory.createDeck()).thenReturn(mockDeck.clone())
//    when(mockPlayerFactory.createPlayer(any[String], any[List[ICard]])).thenReturn(mockPlayer1, mockPlayer2)
//    when(mockPlayingFieldFactory.createPlayingField(mockPlayer1, mockPlayer2)).thenReturn(mockPlayingField)
//    when(mockPlayingField.getDataManager).thenReturn(mockDataManager)
//    when(mockPlayingField.getRoles).thenReturn(mockRoles)
//    when(mockPlayingField.getActionManager).thenReturn(mockActionManager)
//
//    "create game and set up players and field" in {
//      // Stub `getCards` so `.toList` doesn't throw NPE
//      val mockCards = List(mock[ICard], mock[ICard])
//      when(mockPlayer1.getCards).thenReturn(mockCards)
//      when(mockPlayer2.getCards).thenReturn(mockCards)
//
//      initializer.createGame("Alice", "Bob")
//
//      verify(mockPlayerFactory).createPlayer("Alice", any[List[ICard]])
//      verify(mockPlayerFactory).createPlayer("Bob", any[List[ICard]])
//      verify(mockPlayingFieldFactory).createPlayingField(mockPlayer1, mockPlayer2)
//      verify(mockDataManager).initializePlayerHands(any[List[ICard]], any[List[ICard]])
//      verify(mockRoles).setRoles(mockPlayer1, mockPlayer2)
//      verify(mockPlayingField).setPlayingField()
//    }
//
//
//    "return initialized components" in {
//      initializer.createGame("Alice", "Bob")
//      initializer.getPlayer1 shouldBe mockPlayer1
//      initializer.getPlayer2 shouldBe mockPlayer2
//      initializer.getPlayingField shouldBe mockPlayingField
//      initializer.getActionManager shouldBe mockActionManager
//    }
//
//    "initialize from saved state" in {
//      when(mockState.player1).thenReturn(mockPlayer1)
//      when(mockState.player2).thenReturn(mockPlayer2)
//      when(mockState.player1Hand).thenReturn(mock[IHandCardsQueue])
//      when(mockState.player2Hand).thenReturn(mock[IHandCardsQueue])
//      when(mockState.player1Hand.getCards).thenReturn(List(mockCard))
//      when(mockState.player2Hand.getCards).thenReturn(List(mockCard))
//      when(mockState.player1Defenders).thenReturn(List(mockCard))
//      when(mockState.player2Defenders).thenReturn(List(mockCard))
//      when(mockState.player1Goalkeeper).thenReturn(Some(mockCard))
//      when(mockState.player2Goalkeeper).thenReturn(Some(mockCard))
//
//      initializer.initializeFromState(mockState)
//
//      verify(mockDataManager).initializePlayerHands(List(mockCard), List(mockCard))
//      verify(mockDataManager).setPlayerDefenders(mockPlayer1, List(mockCard))
//      verify(mockDataManager).setPlayerDefenders(mockPlayer2, List(mockCard))
//      verify(mockDataManager).setPlayerGoalkeeper(mockPlayer1, Some(mockCard))
//      verify(mockDataManager).setPlayerGoalkeeper(mockPlayer2, Some(mockCard))
//      verify(mockPlayingField).setPlayingField()
//    }
//
//    "select defender position correctly" in {
//      when(mockPlayingField.getDefender).thenReturn(mockPlayer2)
//      when(mockDataManager.allDefendersBeaten(mockPlayer2)).thenReturn(true)
//      initializer.createGame("Alice", "Bob")
//      initializer.selectDefenderPosition() shouldBe -1
//
//      when(mockDataManager.allDefendersBeaten(mockPlayer2)).thenReturn(false)
//      initializer.selectDefenderPosition() shouldBe -2
//    }
//  }
//}