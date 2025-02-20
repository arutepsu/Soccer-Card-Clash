package model.playingFiledComponent.factories
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.ActionManager
import play.api.libs.json._
import com.google.inject.Singleton
import com.google.inject.{Inject, Singleton}

@Singleton
class ActionManagerFactory @Inject()() extends IActionManagerFactory {
  override def createActionManager(playingField: IPlayingField): ActionManager = {
    new ActionManager(playingField)
  }

//  override def loadFromJson(json: JsObject): ActionManager = {
//    
//  }
}
