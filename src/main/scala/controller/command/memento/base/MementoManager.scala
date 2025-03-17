package controller.command.memento.base

import controller.Events
import controller.command.memento.IMementoManager
import controller.command.memento.base.Memento
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.gameComponent.IGame
import model.playerComponent.playerAction.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IActionManager

class MementoManager(private val game: IGame) extends IMementoManager {

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

    val attackerHand = pf.getDataManager.getPlayerHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand.map(_.copy()))

    val defenderHand = pf.getDataManager.getPlayerHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand.map(_.copy()))

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