package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IDataManager, IPlayerFieldManager, IPlayerHandManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.DataManager
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IRolesManager
import scala.collection.mutable

class DataManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "DataManager" should {

    "delegate getPlayerHand to handManager" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val mockPlayer = mock[IPlayer]
      val mockHand = mock[IHandCardsQueue]

      val mockRoles = mock[IRolesManager]
      when(mockRoles.attacker).thenReturn(mockPlayer)
      when(mockField.getRoles).thenReturn(mockRoles)

      when(mockHandManager.getPlayerHand(mockPlayer)).thenReturn(mockHand)

      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)

      // Ensure we're querying the exact mockPlayer
      dataManager.getPlayerHand(mockPlayer) shouldBe mockHand
      verify(mockHandManager).getPlayerHand(mockPlayer)
    }


    "delegate getPlayerDefenders to fieldManager" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val mockPlayer = mock[IPlayer]
      val cards = List(mock[ICard], mock[ICard])

      when(mockFieldManager.getPlayerDefenders(mockPlayer)).thenReturn(cards)

      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)

      dataManager.getPlayerDefenders(mockPlayer) shouldBe cards
    }

    "call initializeFields with refill strategy" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val mockPlayer1 = mock[IPlayer]
      val mockPlayer2 = mock[IPlayer]
      val mockQueue1 = mock[IHandCardsQueue]
      val mockQueue2 = mock[IHandCardsQueue]
      val cards1 = mutable.Queue[ICard]()
      val cards2 = mutable.Queue[ICard]()

      val mockRoles = mock[IRolesManager]
      when(mockRoles.attacker).thenReturn(mockPlayer1)
      when(mockRoles.defender).thenReturn(mockPlayer2)
      when(mockField.getRoles).thenReturn(mockRoles)

      when(mockHandManager.getPlayerHand(mockPlayer1)).thenReturn(mockQueue1)
      when(mockHandManager.getPlayerHand(mockPlayer2)).thenReturn(mockQueue2)
      when(mockQueue1.getCards).thenReturn(cards1)
      when(mockQueue2.getCards).thenReturn(cards2)

      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)
      val mockRefillStrategy = mock[IRefillStrategy]
      dataManager.setRefillStrategy(mockRefillStrategy)

      dataManager.initializeFields()

      verify(mockRefillStrategy).refillField(dataManager, mockPlayer1, cards1)
      verify(mockRefillStrategy).refillField(dataManager, mockPlayer2, cards2)
    }
    "initialize player hands through handManager" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val p1 = mock[IPlayer]
      val p2 = mock[IPlayer]
      val cards1 = List(mock[ICard])
      val cards2 = List(mock[ICard])

      val mockRoles = mock[IRolesManager]
      when(mockRoles.attacker).thenReturn(p1)
      when(mockRoles.defender).thenReturn(p2)
      when(mockField.getRoles).thenReturn(mockRoles)


      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)

      dataManager.initializePlayerHands(cards1, cards2)

      verify(mockHandManager).initializePlayerHands(p1, cards1, p2, cards2)
    }
    "return attacking card from handManager" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val attacker = mock[IPlayer]
      val card = mock[ICard]

      val mockRoles = mock[IRolesManager]
      when(mockRoles.attacker).thenReturn(attacker)
      when(mockField.getRoles).thenReturn(mockRoles)

      // ✅ Missing piece: what getAttackingCard returns
      when(mockHandManager.getAttackingCard(attacker)).thenReturn(card)

      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)

      dataManager.getAttackingCard shouldBe card
    }

    "return defender card from handManager" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val defender = mock[IPlayer]
      val card = mock[ICard]

      val mockRoles = mock[IRolesManager]
      when(mockRoles.defender).thenReturn(defender)
      when(mockField.getRoles).thenReturn(mockRoles)

      // ✅ Missing: return card from hand manager
      when(mockHandManager.getDefenderCard(defender)).thenReturn(card)

      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)

      dataManager.getDefenderCard shouldBe card
    }

    "set player hand via handManager" in {
      val player = mock[IPlayer]
      val hand = mock[IHandCardsQueue]
      val handManager = mock[IPlayerHandManager]
      val dataManager = new DataManager(mock[IPlayingField], handManager, mock[IPlayerFieldManager])

      dataManager.setPlayerHand(player, hand)
      verify(handManager).setPlayerHand(player, hand) // ✅ this is correct
    }

    "remove defender card via fieldManager" in {
      val player = mock[IPlayer]
      val card = mock[ICard]

      val fieldManager = mock[IPlayerFieldManager] // ✅ Track the mock
      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)

      dataManager.removeDefenderCard(player, card)

      verify(fieldManager).removeDefenderCard(player, card) // ✅ Verify the mock directly
    }

    "check if all defenders are beaten via fieldManager" in {
      val player = mock[IPlayer]
      val fieldManager = mock[IPlayerFieldManager]
      when(fieldManager.allDefendersBeaten(player)).thenReturn(true)

      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)
      dataManager.allDefendersBeaten(player) shouldBe true
    }
    "refill defender field using refillStrategy" in {
      val player = mock[IPlayer]
      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], mock[IPlayerFieldManager])
      val refillStrategy = mock[IRefillStrategy]

      dataManager.setRefillStrategy(refillStrategy)
      dataManager.refillDefenderField(player)

      verify(refillStrategy).refillDefenderField(dataManager, player)
    }
    "set goalkeeper for attacker" in {
      val field = mock[IPlayingField]
      val fieldManager = mock[IPlayerFieldManager]
      val card = mock[ICard]

      val dataManager = new DataManager(field, mock[IPlayerHandManager], fieldManager)
      dataManager.setGoalkeeperForAttacker(card)

      verify(fieldManager).setGoalkeeperForAttacker(field, card)
    }
    "clear all via both managers" in {
      val handManager = mock[IPlayerHandManager]
      val fieldManager = mock[IPlayerFieldManager]

      val dataManager = new DataManager(mock[IPlayingField], handManager, fieldManager)
      dataManager.clearAll()

      verify(handManager).clearAll()
      verify(fieldManager).clearAll()
    }
    "return defender card at index via fieldManager" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val player = mock[IPlayer]
      val card = mock[ICard]

      when(mockFieldManager.getDefenderCard(player, 1)).thenReturn(card)

      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)

      dataManager.getDefenderCard(player, 1) shouldBe card
    }
    "return player field via fieldManager" in {
      val fieldManager = mock[IPlayerFieldManager]
      val player = mock[IPlayer]
      val field = List(mock[ICard], mock[ICard])

      when(fieldManager.getPlayerField(player)).thenReturn(field)

      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)
      dataManager.getPlayerField(player) shouldBe field
    }
    "set player field via fieldManager" in {
      val fieldManager = mock[IPlayerFieldManager]
      val player = mock[IPlayer]
      val newField = List(mock[ICard])

      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)
      dataManager.setPlayerField(player, newField)

      verify(fieldManager).setPlayerField(player, newField)
    }
    "return player goalkeeper via fieldManager" in {
      val fieldManager = mock[IPlayerFieldManager]
      val player = mock[IPlayer]
      val goalkeeper = Some(mock[ICard])

      when(fieldManager.getPlayerGoalkeeper(player)).thenReturn(goalkeeper)

      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)
      dataManager.getPlayerGoalkeeper(player) shouldBe goalkeeper
    }
    "set player goalkeeper via fieldManager" in {
      val fieldManager = mock[IPlayerFieldManager]
      val player = mock[IPlayer]
      val goalkeeper = Some(mock[ICard])

      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)
      dataManager.setPlayerGoalkeeper(player, goalkeeper)

      verify(fieldManager).setPlayerGoalkeeper(player, goalkeeper)
    }
    "set player defenders via fieldManager (sets field)" in {
      val fieldManager = mock[IPlayerFieldManager]
      val player = mock[IPlayer]
      val defenders = List(mock[ICard], mock[ICard])

      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)
      dataManager.setPlayerDefenders(player, defenders)

      verify(fieldManager).setPlayerField(player, defenders)
    }
    "remove defender goalkeeper via fieldManager" in {
      val fieldManager = mock[IPlayerFieldManager]
      val player = mock[IPlayer]

      val dataManager = new DataManager(mock[IPlayingField], mock[IPlayerHandManager], fieldManager)
      dataManager.removeDefenderGoalkeeper(player)

      verify(fieldManager).removeDefenderGoalkeeper(player)
    }
    "return player1 (attacker) via playingField" in {
      val field = mock[IPlayingField]
      val attacker = mock[IPlayer]

      val mockRoles = mock[IRolesManager]
      when(mockRoles.attacker).thenReturn(attacker)
      when(field.getRoles).thenReturn(mockRoles)

      val dataManager = new DataManager(field, mock[IPlayerHandManager], mock[IPlayerFieldManager])
      dataManager.getPlayer1 shouldBe attacker
    }

    "return player2 (defender) via playingField" in {
      val field = mock[IPlayingField]
      val defender = mock[IPlayer]

      val mockRoles = mock[IRolesManager]
      when(mockRoles.defender).thenReturn(defender)
      when(field.getRoles).thenReturn(mockRoles)

      val dataManager = new DataManager(field, mock[IPlayerHandManager], mock[IPlayerFieldManager])
      dataManager.getPlayer2 shouldBe defender
    }
    "return the playing field" in {
      val field = mock[IPlayingField]
      val dataManager = new DataManager(field, mock[IPlayerHandManager], mock[IPlayerFieldManager])
      dataManager.getPlayingField shouldBe field
    }

  }
}
