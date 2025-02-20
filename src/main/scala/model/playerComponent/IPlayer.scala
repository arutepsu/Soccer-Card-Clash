package model.playerComponent
import model.cardComponent.ICard
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import play.api.libs.json._
import scala.xml._
trait IPlayer extends Serializable {
  def name: String
  def cards: List[ICard]
  def actionStates: Map[PlayerActionPolicies, PlayerActionState]
  def getCards: List[ICard]
  def setName(newName: String): IPlayer
  def performAction(action: PlayerActionPolicies): IPlayer
  def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer
  def setHandCards(newCards: List[ICard]): IPlayer
  def setActionStates(newActionStates: Map[PlayerActionPolicies, PlayerActionState]): IPlayer

  override def toJson: JsObject = Json.obj(
    "name" -> name,
    "cards" -> JsArray(cards.map(_.toJson)),
    "actionStates" -> JsObject(actionStates.map { case (action, state) =>
      action.toString -> JsString(state.toString)
    })
  )

  override def toXml: Elem =
    <Player>
      <Name>
        {name}
      </Name>
      <Cards>
        {cards.map(_.toXml)}
      </Cards>
      <ActionStates>
        {actionStates.map { case (action, state) =>
        <Action>
          <Name>
            {action.toString}
          </Name>
          <State>
            {state.toString}
          </State>
        </Action>
      }}
      </ActionStates>
    </Player>
}
