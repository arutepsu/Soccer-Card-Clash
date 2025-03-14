package model.playingFiledComponent.strategy.boostStrategy.base

import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.*
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.manager.base.DataManager
import model.playingFiledComponent.strategy.boostStrategy.IBoostStrategy

class GoalkeeperBoostStrategy extends IBoostStrategy {

  override def boost(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackersGoalkeeperOpt = data.getPlayerGoalkeeper(roles.attacker)

    attackersGoalkeeperOpt match {
      case Some(goalkeeper) =>
        val boostedGoalkeeper = goalkeeper.boost()
        data.setGoalkeeperForAttacker(boostedGoalkeeper) // Store the boosted version

        val attackerBeforeAction = roles.attacker

        // Ensure the attacker has Boost actions left
        attackerBeforeAction.actionStates.get(PlayerActionPolicies.Boost) match {
          case Some(OutOfActions) =>
            println(s"[DEBUG] ${attackerBeforeAction.name} has no Boost actions left. Boosting is prevented.")
            return false
          case Some(CanPerformAction(remainingUses)) if remainingUses <= 0 =>
            println(s"[DEBUG] ${attackerBeforeAction.name} has no Boost actions left. Boosting is prevented.")
            return false
          case _ => // Continue boosting
        }

        // Perform the action and update the attacker's state
        val attackerAfterAction = attackerBeforeAction.performAction(PlayerActionPolicies.Boost)

        // Debug print for remaining uses of the Boost action
        attackerAfterAction.actionStates.get(PlayerActionPolicies.Boost) match {
          case Some(CanPerformAction(remainingUses)) =>
            println(s"[DEBUG] Remaining Boost uses for ${attackerAfterAction.name}: $remainingUses")
          case Some(OutOfActions) =>
            println(s"[DEBUG] ${attackerAfterAction.name} has no Boost actions left.")
          case _ =>
            println(s"[DEBUG] Unexpected state for Boost action.")
        }

        roles.setRoles(attackerAfterAction, roles.defender) // Update only the attacker

        playingField.notifyObservers()
        println(s"[DEBUG] Observers notified of goalkeeper boost.")
        true
      case None => false
    }
  }
}
