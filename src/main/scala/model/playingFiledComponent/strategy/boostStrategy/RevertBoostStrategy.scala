package model.playingFiledComponent.strategy.boostStrategy
import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{IDataManager, IRolesManager, RolesManager}
import model.playingFiledComponent.strategy.boostStrategy.base.RevertCard
import model.gameComponent.IGame
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
