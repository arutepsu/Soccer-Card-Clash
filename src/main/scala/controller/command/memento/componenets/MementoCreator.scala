package controller.command.memento.componenets

import com.google.inject.assistedinject.Assisted
import com.google.inject.{Inject, Singleton}
import controller.command.memento.base.Memento
import model.gameComponent.IGame
import model.playerComponent.playerAction.{CanPerformAction, OutOfActions}

trait IMementoCreator {

  def createMemento(): Memento

}

class MementoCreator @Inject()(@Assisted game: IGame) extends IMementoCreator {

  override def createMemento(): Memento = {
    val playingField = game.getPlayingField

    Memento(
      attacker = playingField.getAttacker,
      defender = playingField.getDefender,
      player1Defenders = playingField.getDataManager.getPlayerDefenders(playingField.getAttacker).map(_.copy()),
      player2Defenders = playingField.getDataManager.getPlayerDefenders(playingField.getDefender).map(_.copy()),
      player1Goalkeeper = playingField.getDataManager.getPlayerGoalkeeper(playingField.getAttacker).map(_.copy()),
      player2Goalkeeper = playingField.getDataManager.getPlayerGoalkeeper(playingField.getDefender).map(_.copy()),
      player1Hand = playingField.getDataManager.getPlayerHand(playingField.getAttacker).map(_.copy()).toList,
      player2Hand = playingField.getDataManager.getPlayerHand(playingField.getDefender).map(_.copy()).toList,
      player1Score = playingField.getScores.getScorePlayer1,
      player2Score = playingField.getScores.getScorePlayer2,

      player1Actions = playingField.getAttacker.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions) => action -> 0
      },

      player2Actions = playingField.getDefender.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions) => action -> 0
      }
    )
  }
}

trait IMementoCreatorFactory {
  def create(game: IGame): IMementoCreator
}


