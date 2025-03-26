package de.htwg.se.soccercardclash.model.gameComponent.state.memento.base

import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class MementoManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A MementoManager" should {

    "create a memento using the mementoCreator" in {
      val mockGame = mock[IGame]
      val mockCreator = mock[IMementoCreator]
      val mockRestorer = mock[IMementoRestorer]
      val memento = mock[Memento]

      when(mockCreator.createMemento()).thenReturn(memento)

      val manager = new MementoManager(mockGame, mockCreator, mockRestorer)

      manager.createMemento() should be (memento)
      verify(mockCreator).createMemento()
    }

    "restore game state using the mementoRestorer" in {
      val mockGame = mock[IGame]
      val mockCreator = mock[IMementoCreator]
      val mockRestorer = mock[IMementoRestorer]
      val memento = mock[Memento]

      val manager = new MementoManager(mockGame, mockCreator, mockRestorer)

      manager.restoreGameState(memento)
      verify(mockRestorer).restoreGameState(memento)
    }

    "restore boosts using the mementoRestorer" in {
      val mockGame = mock[IGame]
      val mockCreator = mock[IMementoCreator]
      val mockRestorer = mock[IMementoRestorer]
      val memento = mock[Memento]
      val index = 2

      val manager = new MementoManager(mockGame, mockCreator, mockRestorer)

      manager.restoreBoosts(memento, index)
      verify(mockRestorer).restoreBoosts(memento, index)
    }

    "restore goalkeeper boost using the mementoRestorer" in {
      val mockGame = mock[IGame]
      val mockCreator = mock[IMementoCreator]
      val mockRestorer = mock[IMementoRestorer]
      val memento = mock[Memento]

      val manager = new MementoManager(mockGame, mockCreator, mockRestorer)

      manager.restoreGoalkeeperBoost(memento)
      verify(mockRestorer).restoreGoalkeeperBoost(memento)
    }
  }
}
