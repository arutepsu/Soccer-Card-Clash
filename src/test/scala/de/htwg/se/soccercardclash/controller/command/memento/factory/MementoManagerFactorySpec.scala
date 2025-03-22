package de.htwg.se.soccercardclash.controller.command.memento.factory

import de.htwg.se.soccercardclash.controller.command.memento.base.*
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.controller.command.memento.componenets.*
class MementoManagerFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "MementoManagerFactory" should {

    "create a MementoManager with correct creator and restorer" in {
      val mockGame = mock[IGame]
      val mockCreatorFactory = mock[IMementoCreatorFactory]
      val mockRestorerFactory = mock[IMementoRestorerFactory]

      val mockCreator = mock[IMementoCreator]
      val mockRestorer = mock[IMementoRestorer]

      // Stub the factories
      when(mockCreatorFactory.create(mockGame)).thenReturn(mockCreator)
      when(mockRestorerFactory.create(mockGame)).thenReturn(mockRestorer)

      val factory = new MementoManagerFactory(mockCreatorFactory, mockRestorerFactory)
      val manager = factory.create(mockGame)

      manager shouldBe a[MementoManager]

      verify(mockCreatorFactory).create(mockGame)
      verify(mockRestorerFactory).create(mockGame)
    }
  }
}
