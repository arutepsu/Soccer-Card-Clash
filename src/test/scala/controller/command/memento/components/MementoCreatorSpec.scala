//package controller.command.memento.components
//
//import controller.command.memento.base.Memento
//import controller.command.memento.componenets.MementoCreator
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
//import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
//import model.playingFiledComponent.dataStructure.IHandCardsQueue
//
//class MementoCreatorSpec extends AnyWordSpec with Matchers with MockitoSugar {
//
//  "MementoCreator" should {
//
//    "create a Memento with the correct game state" in {
//      val mockGame = mock[IGame]
//      val mockField = mock[IPlayingField]
//      val mockDataManager = mock[IDataManager]
//      val mockScores = mock[IPlayerScores]
//
//      val attacker = mock[IPlayer]
//      val defender = mock[IPlayer]
//
//      val card1 = mock[ICard]
//      val card2 = mock[ICard]
//      val handCard = mock[ICard]
//      val gkCard = mock[ICard]
//
//      // Mock IHandCardsQueue
//      val attackerHand = mock[IHandCardsQueue]
//      val defenderHand = mock[IHandCardsQueue]
//
//      // Card copies
//      when(card1.copy()).thenReturn(card1)
//      when(card2.copy()).thenReturn(card2)
//      when(handCard.copy()).thenReturn(handCard)
//      when(gkCard.copy()).thenReturn(gkCard)
//
//      // Hand queue toList behavior
//      when(attackerHand.toList).thenReturn(List(handCard))
//      when(defenderHand.toList).thenReturn(Nil)
//
//      // Set up players
//      when(mockField.getAttacker).thenReturn(attacker)
//      when(mockField.getDefender).thenReturn(defender)
//
//      // Set up data manager
//      when(mockField.getDataManager).thenReturn(mockDataManager)
//      when(mockDataManager.getPlayerDefenders(attacker)).thenReturn(List(card1))
//      when(mockDataManager.getPlayerDefenders(defender)).thenReturn(List(card2))
//      when(mockDataManager.getPlayerGoalkeeper(attacker)).thenReturn(Some(gkCard))
//      when(mockDataManager.getPlayerGoalkeeper(defender)).thenReturn(None)
//      when(mockDataManager.getPlayerHand(attacker)).thenReturn(attackerHand)
//      when(mockDataManager.getPlayerHand(defender)).thenReturn(defenderHand)
//
//      // Set up scores
//      when(mockField.getScores).thenReturn(mockScores)
//      when(mockScores.getScorePlayer1).thenReturn(5)
//      when(mockScores.getScorePlayer2).thenReturn(3)
//
//      // Action states
//      val attackerActions = Map(
//        PlayerActionPolicies.DoubleAttack -> CanPerformAction(2),
//        PlayerActionPolicies.Boost -> OutOfActions
//      )
//      val defenderActions = Map(
//        PlayerActionPolicies.Swap -> CanPerformAction(1)
//      )
//
//      when(attacker.actionStates).thenReturn(attackerActions)
//      when(defender.actionStates).thenReturn(defenderActions)
//
//      // Game provides the field
//      when(mockGame.getPlayingField).thenReturn(mockField)
//
//      // Act
//      val creator = new MementoCreator(mockGame)
//      val memento = creator.createMemento()
//
//      // Assert
//      memento.attacker shouldBe attacker
//      memento.defender shouldBe defender
//      memento.player1Defenders should contain only card1
//      memento.player2Defenders should contain only card2
//      memento.player1Goalkeeper should contain(gkCard)
//      memento.player2Goalkeeper shouldBe None
//      memento.player1Hand should contain only handCard
//      memento.player2Hand shouldBe empty
//      memento.player1Score shouldBe 5
//      memento.player2Score shouldBe 3
//      memento.player1Actions shouldBe Map(
//        PlayerActionPolicies.DoubleAttack -> 2,
//        PlayerActionPolicies.Boost -> 0
//      )
//      memento.player2Actions shouldBe Map(
//        PlayerActionPolicies.Swap -> 1
//      )
//    }
//  }
//}