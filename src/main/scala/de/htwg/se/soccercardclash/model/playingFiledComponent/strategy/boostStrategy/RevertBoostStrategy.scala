package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IDataManager, IRolesManager, RolesManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base.RevertCard
import de.htwg.se.soccercardclash.model.gameComponent.IGame
class RevertBoostStrategy(playingField: IPlayingField) extends IRevertStrategy {
  protected val cardReverter = new RevertCard
  
  override def revertCard(card: ICard): ICard = {
    cardReverter.revertCard(playingField, card)
  }
}
trait IRevertStrategy {
  def revertCard(card: ICard): ICard
}

class RevertCardActionCommand(card: ICard, revertStrategy: IRevertStrategy, game: IGame) {

  def revert(): Unit = {
    val revertedCard = revertStrategy.revertCard(card)
    game.updateGameState()
  }
}
