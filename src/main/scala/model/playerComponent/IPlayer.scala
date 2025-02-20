package model.playerComponent
import model.cardComponent.ICard
import model.playerComponent.playerRole.PlayerRole
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
trait IPlayer {
  def name: String
  def cards: List[ICard]
  def actionStates: Map[PlayerActionPolicies, PlayerActionState]
  def getCards: List[ICard]
  def setName(newName: String): IPlayer
  def performAction(action: PlayerActionPolicies): IPlayer
  def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer
  def setHandCards(newCards: List[ICard]): IPlayer
  def setActionStates(newActionStates: Map[PlayerActionPolicies, PlayerActionState]): IPlayer

}
