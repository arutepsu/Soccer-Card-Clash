package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IDataManager, IPlayerFieldManager, IPlayerHandManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.DataManager
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.collection.mutable

class DataManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "DataManager" should {

    "delegate getPlayerHand to handManager" in {
      val mockField = mock[IPlayingField]
      val mockHandManager = mock[IPlayerHandManager]
      val mockFieldManager = mock[IPlayerFieldManager]
      val mockPlayer = mock[IPlayer]
      val mockHand = mock[IHandCardsQueue]

      when(mockField.getAttacker).thenReturn(mockPlayer)
      when(mockHandManager.getPlayerHand(mockPlayer)).thenReturn(mockHand)

      val dataManager = new DataManager(mockField, mockHandManager, mockFieldManager)

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

      when(mockField.getAttacker).thenReturn(mockPlayer1)
      when(mockField.getDefender).thenReturn(mockPlayer2)
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
  }
}
