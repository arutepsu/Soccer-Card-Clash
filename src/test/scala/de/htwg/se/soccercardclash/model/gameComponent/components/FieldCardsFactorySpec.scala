package de.htwg.se.soccercardclash.model.gameComponent.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.components.{FieldCards, FieldCardsFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class FieldCardsFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "FieldCardsFactory" should {
    "create FieldCards with given data" in {
      val player = mock[IPlayer]
      val card = mock[ICard]

      val defenders = Map(player -> List(Some(card), None, None))
      val goalkeepers = Map(player -> Some(card))
      val playerFields = Map(player -> List(Some(card)))

      val factory = new FieldCardsFactory()
      val result = factory.create(playerFields, goalkeepers, defenders)

      result.getPlayerDefenders(player) should contain theSameElementsAs defenders(player)
      result.getPlayerGoalkeeper(player) shouldBe Some(card)
    }
  }

  "A FieldCards instance" should {

    "return None for a goalkeeper if none was set" in {
      val player = mock[IPlayer]
      val fieldCards = FieldCards()

      fieldCards.goalkeepers(player) shouldBe None
    }

    "return empty list for defenders if none were set" in {
      val player = mock[IPlayer]
      val fieldCards = FieldCards()

      fieldCards.defenders(player) shouldBe empty
    }

    "store and retrieve a goalkeeper" in {
      val player = mock[IPlayer]
      val card = mock[ICard]
      val fieldCards = FieldCards(goalkeepers = Map(player -> Some(card)))

      fieldCards.goalkeepers(player) shouldBe Some(card)
    }

    "store and retrieve defenders" in {
      val player = mock[IPlayer]
      val card1 = mock[ICard]
      val card2 = mock[ICard]

      val defenderList = List(Some(card1), None, Some(card2))
      val fieldCards = FieldCards(defenders = Map(player -> defenderList))

      fieldCards.defenders(player) shouldBe defenderList
    }

  }

  "FieldCards" should {
    val player = mock[IPlayer]
    val card1 = mock[ICard]
    val card2 = mock[ICard]

    val initial = FieldCards(
      defenders = Map(player -> List(Some(card1), Some(card2), None)),
      goalkeepers = Map(player -> Some(card1))
    )

    "return updated defenders when newPlayerDefenders is called" in {
      val updated = initial.newPlayerDefenders(player, List(None, None, None))
      updated.getPlayerDefenders(player) shouldBe List(None, None, None)
    }

    "return updated goalkeeper when newPlayerGoalkeeper is called" in {
      val updated = initial.newPlayerGoalkeeper(player, Some(card2))
      updated.getPlayerGoalkeeper(player) shouldBe Some(card2)
    }

    "return updated attacker goalkeeper when newGoalkeeperForAttacker is called" in {
      val updated = initial.newGoalkeeperForAttacker(player, card2)
      updated.getPlayerGoalkeeper(player) shouldBe Some(card2)
    }

    "remove a matching defender card when removeDefenderCard is called" in {
      val updated = initial.removeDefenderCard(player, Some(card1))
      updated.getPlayerDefenders(player) shouldBe List(None, Some(card2), None)
    }

    "remove the goalkeeper for the defender when removeDefenderGoalkeeper is called" in {
      val updated = initial.removeDefenderGoalkeeper(player)
      updated.getPlayerGoalkeeper(player) shouldBe None
    }

    "check if all defenders are beaten" in {
      val emptyDefenders = initial.newPlayerDefenders(player, List(None, None, None))
      emptyDefenders.allDefendersBeaten(player) shouldBe true

      val mixedDefenders = initial.newPlayerDefenders(player, List(None, Some(card1), None))
      mixedDefenders.allDefendersBeaten(player) shouldBe false
    }

    "get defender card by index" in {
      initial.getDefenderCard(player, 0) shouldBe Some(card1)
      initial.getDefenderCard(player, 2) shouldBe None
      initial.getDefenderCard(player, -1) shouldBe None
      initial.getDefenderCard(player, 99) shouldBe None
    }
  }
}
