//package controller.command.memento.components
//
//import controller.Events
//import controller.command.memento.base.Memento
//import model.cardComponent.ICard
//import model.cardComponent.base.types.BoostedCard
//import model.gameComponent.IGame
//import model.playingFiledComponent.IPlayingField
//import model.playingFiledComponent.manager.{IActionManager, IDataManager, IRolesManager}
//import model.playingFiledComponent.strategy.boostStrategy.{IBoostManager, IRevertStrategy}
//import model.playerComponent.IPlayer
//import model.playerComponent.playerAction.*
//import org.mockito.Mockito.*
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.ArgumentMatchers.any
//import controller.command.memento.componenets.MementoRestorer
//class MementoRestorerSpec extends AnyWordSpec with Matchers with MockitoSugar {
//
//  "MementoRestorer" should {
//
//    "restore a boosted card and update roles and action state" in {
//      val mockGame = mock[IGame]
//      val mockField = mock[IPlayingField]
//      val mockDataManager = mock[IDataManager]
//      val mockRoles = mock[IRolesManager]
//      val mockActionManager = mock[IActionManager]
//      val mockBoostManager = mock[IBoostManager]
//      val mockRevertStrategy = mock[IRevertStrategy]
//
//      val attacker = mock[IPlayer]
//      val defender = mock[IPlayer]
//
//      val boostedCard = mock[BoostedCard]
//      val revertedCard = mock[ICard]
//
//      when(mockGame.getPlayingField).thenReturn(mockField)
//      when(mockGame.getActionManager).thenReturn(mockActionManager)
//      when(mockActionManager.getBoostManager).thenReturn(mockBoostManager)
//      when(mockBoostManager.getRevertStrategy).thenReturn(mockRevertStrategy)
//
//      // Set up defenders
//      val defenders = List(boostedCard)
//      when(mockField.getDataManager).thenReturn(mockDataManager)
//      when(mockDataManager.getPlayerDefenders(attacker)).thenReturn(defenders)
//      when(mockRevertStrategy.revertCard(boostedCard)).thenReturn(revertedCard)
//
//      // Memento with boost count
//      val memento = Memento(
//        attacker = attacker,
//        defender = defender,
//        player1Defenders = List(revertedCard),
//        player2Defenders = List(),
//        player1Goalkeeper = None,
//        player2Goalkeeper = None,
//        player1Hand = List(),
//        player2Hand = List(),
//        player1Score = 0,
//        player2Score = 0,
//        player1Actions = Map(PlayerActionPolicies.Boost -> 2),
//        player2Actions = Map()
//      )
//
//      // Stub attacker's current action states
//      val attackerActions = Map(PlayerActionPolicies.Boost -> OutOfActions)
//      when(attacker.actionStates).thenReturn(attackerActions)
//
//      // Updated attacker with new actions
//      val updatedAttacker = mock[IPlayer]
//      when(attacker.setActionStates(any[Map[PlayerActionPolicies, PlayerActionState]])).thenReturn(updatedAttacker)
//
//      when(mockField.getRoles).thenReturn(mockRoles)
//      when(mockRoles.defender).thenReturn(defender)
//
//      val restorer = new MementoRestorer(mockGame)
//
//      restorer.restoreBoosts(memento, 0)
//
//      // Verify defender was reverted
//      verify(mockDataManager).setPlayerDefenders(attacker, List(revertedCard))
//
//      // Verify action state updated
//      verify(attacker).setActionStates(Map(PlayerActionPolicies.Boost -> CanPerformAction(2)))
//      verify(mockRoles).setRoles(updatedAttacker, defender)
//
//      // Verify notification triggered
//      verify(mockField).notifyObservers(Events.Reverted)
//    }
//
//    "throw exception if card at index is not BoostedCard" in {
//      val mockGame = mock[IGame]
//      val mockField = mock[IPlayingField]
//      val mockDataManager = mock[IDataManager]
//      val mockActionManager = mock[IActionManager]
//      val mockBoostManager = mock[IBoostManager]
//      val mockRevertStrategy = mock[IRevertStrategy]
//
//      val attacker = mock[IPlayer]
//      val regularCard = mock[ICard]
//
//      when(mockGame.getPlayingField).thenReturn(mockField)
//      when(mockGame.getActionManager).thenReturn(mockActionManager)
//      when(mockActionManager.getBoostManager).thenReturn(mockBoostManager)
//      when(mockBoostManager.getRevertStrategy).thenReturn(mockRevertStrategy)
//      when(mockField.getDataManager).thenReturn(mockDataManager)
//      when(mockField.getRoles).thenReturn(mock[IRolesManager])
//      when(mockDataManager.getPlayerDefenders(attacker)).thenReturn(List(regularCard))
//
//      val memento = Memento(
//        attacker = attacker,
//        defender = mock[IPlayer],
//        player1Defenders = List(),
//        player2Defenders = List(),
//        player1Goalkeeper = None,
//        player2Goalkeeper = None,
//        player1Hand = List(),
//        player2Hand = List(),
//        player1Score = 0,
//        player2Score = 0,
//        player1Actions = Map(),
//        player2Actions = Map()
//      )
//
//      val restorer = new MementoRestorer(mockGame)
//
//      assertThrows[RuntimeException] {
//        restorer.restoreBoosts(memento, 0)
//      }
//    }
//
//    "do nothing if lastBoostedIndex is -1" in {
//      val mockGame = mock[IGame]
//      val restorer = new MementoRestorer(mockGame)
//
//      noException should be thrownBy {
//        restorer.restoreBoosts(mock[Memento], -1)
//      }
//    }
//  }
//}