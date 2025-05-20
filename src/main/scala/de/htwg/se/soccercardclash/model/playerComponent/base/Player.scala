package de.htwg.se.soccercardclash.model.playerComponent.base
import de.htwg.se.soccercardclash.util.AIAction
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playerComponent.base.{AI, PlayerType}
import play.api.libs.json.*

import scala.xml.*

case class Player(
                   name: String,
                   actionStates: Map[PlayerActionPolicies, PlayerActionState] =
                   PlayerActionPolicies.values.map(action => action -> CanPerformAction(action.maxUses)).toMap,
                   playerType: PlayerType
                 ) extends IPlayer {

  override def toString: String = s"Player: $name"

  override def setName(newName: String): IPlayer = this.copy(name = newName)

  override def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer = {
    this.copy(actionStates = actionStates.updated(action, newState))
  }

  override def setActionStates(newActionStates: Map[PlayerActionPolicies, PlayerActionState]): IPlayer = {
    this.copy(actionStates = newActionStates)
  }
  override def getActionStates: Map[PlayerActionPolicies, PlayerActionState] = actionStates

  override def equals(obj: Any): Boolean = obj match {
    case other: Player => this.name == other.name
    case _ => false
  }

  override def hashCode(): Int = name.hashCode

  def isAI: Boolean = playerType match {
    case AI(_) => true
    case _ => false
  }
  def decideAction(ctx: GameContext): Option[AIAction] = playerType match {
    case AI(strategy) => Some(strategy.decideAction(ctx, this))
    case _ => None
  }

}

object Player {
  def withDefaultActions(name: String, playerType: PlayerType): Player =
    Player(
      name = name,
      playerType = playerType,
      actionStates = PlayerActionPolicies.values.map { policy =>
        policy -> CanPerformAction(policy.maxUses)
      }.toMap
    )
  
  def withCustomActions(name: String, playerType: PlayerType, limits: Map[PlayerActionPolicies, Int]): Player =
    Player(
      name = name,
      playerType = playerType,
      actionStates = PlayerActionPolicies.values.map { policy =>
        val uses = limits.getOrElse(policy, policy.maxUses)
        policy -> (if (uses > 0) CanPerformAction(uses) else OutOfActions)
      }.toMap
    )
}
