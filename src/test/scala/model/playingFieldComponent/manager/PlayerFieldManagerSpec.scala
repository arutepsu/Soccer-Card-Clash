package model.playingFieldComponent.manager

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.PlayerFieldManager
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import util.Observable

class PlayerFieldManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Fix: Real class that mixes both interfaces
  class ObservablePlayingField extends Observable with IPlayingField {
    override def getAttacker: IPlayer = ???
    override def getDefender: IPlayer = ???
    override def getDataManager = ???
    override def getActionManager = ???
    override def getRoles = ???
    override def getScores = ???
    override def setPlayingField(): Unit = {}
    override def reset(): Unit = {}
  }

  "A PlayerFieldManager" should {

    "set and get player field (via getPlayerDefenders)" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      val cards = List(mock[ICard], mock[ICard])
      manager.setPlayerField(player, cards)
      manager.getPlayerDefenders(player) shouldBe cards
    }

    "set and get player goalkeeper" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      val gk = Some(mock[ICard])
      manager.setPlayerGoalkeeper(player, gk)
      manager.getPlayerGoalkeeper(player) shouldBe gk
    }

    "set goalkeeper for attacker and notify observers" in {
      val manager = new PlayerFieldManager
      val card = mock[ICard]
      val attacker = mock[IPlayer]
      val field = spy(new ObservablePlayingField)
      doReturn(attacker).when(field).getAttacker

      manager.setGoalkeeperForAttacker(field, card)
      manager.getPlayerGoalkeeper(attacker) shouldBe Some(card)
      verify(field).notifyObservers()
    }

    "set and get defenders" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      val defenders = List(mock[ICard], mock[ICard])
      manager.setPlayerField(player, defenders)
      manager.getPlayerDefenders(player) shouldBe defenders
    }

    "remove a defender card correctly" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      val card1 = mock[ICard]
      val card2 = mock[ICard]
      manager.setPlayerField(player, List(card1, card2))
      manager.removeDefenderCard(player, card1)
      manager.getPlayerDefenders(player) shouldBe List(card2)
    }

    "remove goalkeeper correctly" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      manager.setPlayerGoalkeeper(player, Some(mock[ICard]))
      manager.removeDefenderGoalkeeper(player)
      manager.getPlayerGoalkeeper(player) shouldBe None
    }

    "determine all defenders beaten" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      manager.setPlayerField(player, List())
      manager.allDefendersBeaten(player) shouldBe true
    }

    "return correct defender by index" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      val card = mock[ICard]
      manager.setPlayerField(player, List(card))
      manager.getDefenderCard(player, 0) shouldBe card
    }

    "throw IndexOutOfBoundsException for invalid index" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      an[IndexOutOfBoundsException] should be thrownBy manager.getDefenderCard(player, 99)
    }

    "clear all fields and goalkeepers" in {
      val manager = new PlayerFieldManager
      val player = mock[IPlayer]
      val card = mock[ICard]
      manager.setPlayerField(player, List(card))
      manager.setPlayerGoalkeeper(player, Some(card))
      manager.clearAll()
      manager.getPlayerDefenders(player) shouldBe empty
      manager.getPlayerGoalkeeper(player) shouldBe None
    }
  }
}
