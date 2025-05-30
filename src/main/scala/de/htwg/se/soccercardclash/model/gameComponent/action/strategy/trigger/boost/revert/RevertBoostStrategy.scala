package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.IRevertStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.RevertCard
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies

class RevertBoostStrategy(state: IGameState) extends IRevertStrategy {
  protected val cardReverter = new RevertCard

  override def revertCard(card: Option[ICard]): Option[ICard] = {
    cardReverter.revertCard(state, card)
  }
}

trait IRevertStrategy {
  def revertCard(card: Option[ICard]): Option[ICard]
}

