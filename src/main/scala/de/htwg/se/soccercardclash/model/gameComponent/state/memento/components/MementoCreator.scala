package de.htwg.se.soccercardclash.model.gameComponent.state.memento.components

import com.google.inject.assistedinject.Assisted
import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.*
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions}

trait IMementoCreator {

  def createMemento(): Memento

}

class MementoCreator @Inject()(@Assisted game: IGame) extends IMementoCreator {

  override def createMemento(): Memento = {
    val playingField = game.getPlayingField

    Memento(
      attacker = playingField.getRoles.attacker,
      defender = playingField.getRoles.defender,
      player1Defenders = playingField.getDataManager.getPlayerDefenders(playingField.getRoles.attacker).map(_.copy()),
      player2Defenders = playingField.getDataManager.getPlayerDefenders(playingField.getRoles.defender).map(_.copy()),
      player1Goalkeeper = playingField.getDataManager.getPlayerGoalkeeper(playingField.getRoles.attacker).map(_.copy()),
      player2Goalkeeper = playingField.getDataManager.getPlayerGoalkeeper(playingField.getRoles.defender).map(_.copy()),
      player1Hand = playingField.getDataManager.getPlayerHand(playingField.getRoles.attacker).map(_.copy()).toList,
      player2Hand = playingField.getDataManager.getPlayerHand(playingField.getRoles.defender).map(_.copy()).toList,
      player1Score = playingField.getScores.getScorePlayer1,
      player2Score = playingField.getScores.getScorePlayer2,

      player1Actions = playingField.getRoles.attacker.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions) => action -> 0
      },

      player2Actions = playingField.getRoles.defender.actionStates.map {
        case (action, CanPerformAction(remainingUses)) => action -> remainingUses
        case (action, OutOfActions) => action -> 0
      }
    )
  }
}

trait IMementoCreatorFactory {
  def create(game: IGame): IMementoCreator
}


