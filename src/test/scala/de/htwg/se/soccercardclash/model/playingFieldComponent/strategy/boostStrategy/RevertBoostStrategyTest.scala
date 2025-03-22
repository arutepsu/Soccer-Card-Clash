package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base.RevertCard
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.{IRevertStrategy, RevertBoostStrategy, RevertCardActionCommand}
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class RevertBoostStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "RevertBoostStrategy" should "delegate revertCard to RevertCard" in {
    val mockPlayingField = mock[IPlayingField]
    val mockCard = mock[ICard]

    val spyReverter = spy(new RevertCard)
    val strategy = new RevertBoostStrategy(mockPlayingField)

    // Inject spy manually (you could also inject with constructor if needed)
    val revertedCard = mock[ICard]
    val strategyWithStub = new RevertBoostStrategy(mockPlayingField) {
      override val cardReverter: RevertCard = new RevertCard {
        override def revertCard(field: IPlayingField, card: ICard): ICard = {
          field shouldBe mockPlayingField
          card shouldBe mockCard
          revertedCard
        }
      }
    }

    val result = strategyWithStub.revertCard(mockCard)

    result shouldBe revertedCard
  }

  "RevertCardActionCommand" should "revert card using strategy and update game state" in {
    val mockCard = mock[ICard]
    val mockRevertedCard = mock[ICard]
    val mockStrategy = mock[IRevertStrategy]
    val mockGame = mock[IGame]

    when(mockStrategy.revertCard(mockCard)).thenReturn(mockRevertedCard)

    val command = new RevertCardActionCommand(mockCard, mockStrategy, mockGame)
    command.revert()

    verify(mockStrategy).revertCard(mockCard)
    verify(mockGame).updateGameState()
  }
}
