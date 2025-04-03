package de.htwg.se.soccercardclash.model.gameComponent.state.memento.components

import com.google.inject.assistedinject.{Assisted, AssistedInject}
import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.*
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.util.Events


trait IMementoRestorer {

  def restoreGameState(memento: Memento): Unit
  def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit
  def restoreGoalkeeperBoost(memento: Memento): Unit

}


class MementoRestorer @AssistedInject()(@Assisted game: IGame, handCardsQueueFactory: IHandCardsQueueFactory) extends IMementoRestorer {

  override def restoreBoosts(memento: Memento, lastBoostedIndex: Int): Unit = {
    val playingField = game.getPlayingField
    val revertStrategy = game.getActionManager.getBoostManager.getRevertStrategy

    if (lastBoostedIndex != -1) {
      val attacker = memento.attacker
      var defenders = playingField.getDataManager.getPlayerDefenders(attacker)

      if (lastBoostedIndex >= 0 && lastBoostedIndex < defenders.size) {
        defenders(lastBoostedIndex) match {
          case boosted: BoostedCard =>
            val revertedCard = revertStrategy.revertCard(boosted)
            defenders = defenders.updated(lastBoostedIndex, revertedCard)
            playingField.getDataManager.setPlayerDefenders(attacker, defenders)

            val restoredBoostCount = memento.player1Actions.getOrElse(PlayerActionPolicies.Boost, 0)
            val updatedAttacker = attacker.setActionStates(
              attacker.actionStates.updated(
                PlayerActionPolicies.Boost,
                if (restoredBoostCount > 0) CanPerformAction(restoredBoostCount) else OutOfActions
              )
            )

            playingField.getRoles.setRoles(updatedAttacker, playingField.getRoles.defender)

            playingField.notifyObservers(Events.Reverted)

          case _ =>
            throw new RuntimeException("Error while reverting boost")
        }
      }
    }
  }


  override def restoreGoalkeeperBoost(memento: Memento): Unit = {
    val playingField = game.getPlayingField
    val revertStrategy = game.getActionManager.getBoostManager.getRevertStrategy

    val attacker = memento.attacker
    val goalkeeperOpt = memento.player1Goalkeeper

    goalkeeperOpt match {
      case Some(goalkeeper) =>

        goalkeeper match {
          case boostedGoalkeeper: RegularCard =>
            val revertedGoalkeeper = revertStrategy.revertCard(boostedGoalkeeper)
            playingField.getDataManager.setPlayerGoalkeeper(attacker, Some(revertedGoalkeeper))

            val restoredBoostCount = memento.player1Actions.getOrElse(PlayerActionPolicies.Boost, 0)
            val updatedAttacker = attacker.setActionStates(
              attacker.actionStates.updated(
                PlayerActionPolicies.Boost,
                if (restoredBoostCount > 0) CanPerformAction(restoredBoostCount) else OutOfActions
              )
            )

            playingField.getRoles.setRoles(updatedAttacker, playingField.getRoles.defender)

            playingField.notifyObservers(Events.Reverted)

          case _ =>
            throw new RuntimeException("Error while reverting boost")
        }

      case None =>
    }
  }


  override def restoreGameState(memento: Memento): Unit = {
    val pf = game.getPlayingField

    pf.getRoles.setRoles(memento.attacker, memento.defender)
    pf.getDataManager.setPlayerDefenders(memento.attacker, memento.player1Defenders.map(_.copy()))
    pf.getDataManager.setPlayerDefenders(memento.defender, memento.player2Defenders.map(_.copy()))
    pf.getDataManager.setPlayerGoalkeeper(memento.attacker, memento.player1Goalkeeper.map(_.copy()))
    pf.getDataManager.setPlayerGoalkeeper(memento.defender, memento.player2Goalkeeper.map(_.copy()))

    val copiedPlayer1Hand = handCardsQueueFactory.create(memento.player1Hand.map(_.copy()))
    val copiedPlayer2Hand = handCardsQueueFactory.create(memento.player2Hand.map(_.copy()))

    pf.getDataManager.setPlayerHand(memento.attacker, copiedPlayer1Hand)
    pf.getDataManager.setPlayerHand(memento.defender, copiedPlayer2Hand)


    pf.getScores.setScorePlayer1(memento.player1Score)
    pf.getScores.setScorePlayer2(memento.player2Score)

    val restoredPlayer1 = memento.attacker.setActionStates(
      memento.player1Actions.map { case (action, remainingUses) =>
        val validUses = math.min(remainingUses, action.maxUses)
        action -> (if (validUses > 0) CanPerformAction(validUses) else OutOfActions)
      }
    )

    val restoredPlayer2 = memento.defender.setActionStates(
      memento.player2Actions.map { case (action, remainingUses) =>
        val validUses = math.min(remainingUses, action.maxUses)
        action -> (if (validUses > 0) CanPerformAction(validUses) else OutOfActions)
      }
    )

    pf.getRoles.setRoles(restoredPlayer1, restoredPlayer2)

    pf.notifyObservers()
  }
}

trait IMementoRestorerFactory {
  def create(game: IGame): IMementoRestorer
}
