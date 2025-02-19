package model.playerComponent
import model.cardComponent.ICard
import model.cardComponent.base.Card
import model.playerComponent.playerRole.PlayerRole
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
trait IPlayer {
  def name: String
  def role: PlayerRole
  def cards: List[ICard]
  def actionStates: Map[PlayerActionPolicies, PlayerActionState]

  def getCards: List[ICard]
  def setName(newName: String): IPlayer
  def setRole(newRole: PlayerRole): IPlayer
  def performAction(action: PlayerActionPolicies): IPlayer
  def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer
  def setHandCards(newCards: List[ICard]): IPlayer
  def copy(
            name: String = this.name,
            role: PlayerRole = this.role,
            cards: List[ICard] = this.cards,
            actionStates: Map[PlayerActionPolicies, PlayerActionState] = this.actionStates
          ): IPlayer
}
