package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert

import com.google.inject.Singleton
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.IRevertStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.RevertCard
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
trait IRevertBoostStrategyFactory {
  def create(state: IGameState): IRevertStrategy
}

@Singleton
class RevertBoostStrategyFactory extends IRevertBoostStrategyFactory {
  override def create(state: IGameState): IRevertStrategy = new RevertBoostStrategy(state)
}

class RevertBoostStrategy(state: IGameState) extends IRevertStrategy {
  protected val cardReverter = new RevertCard

  override def revertCard(card: Option[ICard]): Option[ICard] = {
    cardReverter.revertCard(state, card)
  }
}

trait IRevertStrategy {
  def revertCard(card: Option[ICard]): Option[ICard]
}

