package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent

import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{ActionManager, DataManager, IDataManager, IRolesManager, RolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import de.htwg.se.soccercardclash.util.Observable
import play.api.libs.json._
import scala.xml._
trait IPlayingField extends Observable with Serializable{
  def setPlayingField(): Unit

  def getDataManager: IDataManager

  def getActionManager: IActionManager

  def getRoles: IRolesManager

  def getScores: IPlayerScores

  def reset(): Unit

  def toXml: Elem = {
    <playingField>
      <Attacker>
        {getRoles.attacker.toXml}
      </Attacker>
      <Defender>
        {getRoles.defender.toXml}
      </Defender>
    </playingField>
  }

  def toJson: JsObject = Json.obj(
    "attacker" -> getRoles.attacker.toJson,
    "defender" -> getRoles.defender.toJson,
  )
}