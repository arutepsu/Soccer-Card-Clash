package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IActionManager, IDataManager, IRolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.base.RevertCard
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.util.{Observable, ObservableEvent}

class RevertCardTest extends AnyFlatSpec with Matchers with MockitoSugar {

  class ObservableMockPlayingField(roles: IRolesManager, dataManager: IDataManager) extends Observable with IPlayingField {
    override def getRoles: IRolesManager = roles
    override def getDataManager: IDataManager = dataManager
    override def getScores: IPlayerScores = mock[IPlayerScores]
    override def getActionManager: IActionManager = mock[IActionManager]
    override def reset(): Unit = {}
    override def setPlayingField(): Unit = {}
    override def notifyObservers(e: ObservableEvent): Unit = super.notifyObservers(e)
  }

  "RevertCard" should "revert a BoostedCard and update it in both fields" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]

    val boostedCard = mock[BoostedCard]
    val revertedCard = mock[RegularCard]

    when(boostedCard.revertBoost()).thenReturn(revertedCard)

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)

    when(mockData.getPlayerDefenders(mockAttacker)).thenReturn(List(boostedCard))
    when(mockData.getPlayerDefenders(mockDefender)).thenReturn(List(boostedCard))

    var notified: Option[ObservableEvent] = None
    val field = new ObservableMockPlayingField(mockRoles, mockData) {
      override def notifyObservers(e: ObservableEvent): Unit = {
        notified = Some(e)
      }
    }

    val result = new RevertCard().revertCard(field, boostedCard)

    result shouldBe revertedCard
    verify(mockData).setPlayerDefenders(mockAttacker, List(revertedCard))
    verify(mockData).setPlayerDefenders(mockDefender, List(revertedCard))
    notified.isDefined shouldBe true
  }

  it should "return the same card if it is not boosted" in {
    val mockData = mock[IDataManager]
    val mockRoles = mock[IRolesManager]
    val mockAttacker = mock[IPlayer]
    val mockDefender = mock[IPlayer]
    val regularCard = mock[RegularCard]

    when(mockRoles.attacker).thenReturn(mockAttacker)
    when(mockRoles.defender).thenReturn(mockDefender)

    when(mockData.getPlayerDefenders(mockAttacker)).thenReturn(List(regularCard))
    when(mockData.getPlayerDefenders(mockDefender)).thenReturn(List(regularCard))

    var notified: Option[ObservableEvent] = None
    val field = new ObservableMockPlayingField(mockRoles, mockData) {
      override def notifyObservers(e: ObservableEvent): Unit = {
        notified = Some(e)
      }
    }

    val result = new RevertCard().revertCard(field, regularCard)

    result shouldBe regularCard
    verify(mockData).setPlayerDefenders(mockAttacker, List(regularCard))
    verify(mockData).setPlayerDefenders(mockDefender, List(regularCard))
    notified.isDefined shouldBe true
  }
}
