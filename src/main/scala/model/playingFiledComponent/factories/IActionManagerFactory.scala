package model.playingFiledComponent.factories
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.ActionManager
import play.api.libs.json._

trait IActionManagerFactory {
  def createActionManager(playingField: IPlayingField): ActionManager
//  def loadFromJson(json: JsObject): ActionManager
}
