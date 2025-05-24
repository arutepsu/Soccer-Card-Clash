package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, Roles}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.base.RevertCard
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies

class RevertBoostStrategy(playingField: IGameState) extends IRevertStrategy {
  protected val cardReverter = new RevertCard

  override def revertCard(card: Option[ICard]): Option[ICard] = {
    cardReverter.revertCard(playingField, card)
  }
}

trait IRevertStrategy {
  def revertCard(card: Option[ICard]): Option[ICard]
}

class RevertCardActionCommand(card: Option[ICard], revertStrategy: IRevertStrategy, gameState: IGameState) {

  def revert(): Option[ICard] = {
    val revertedCard = revertStrategy.revertCard(card)
    revertedCard
  }
}
