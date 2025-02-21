package model.playingFiledComponent.factories
import model.playingFiledComponent.IPlayingField
import play.api.libs.json._
import com.google.inject.Singleton
import com.google.inject.{Inject, Singleton}
import model.playingFiledComponent.manager.base.ActionManager

@Singleton
class ActionManagerFactory @Inject()() extends IActionManagerFactory {
  override def createActionManager(playingField: IPlayingField): ActionManager = {
    new ActionManager(playingField)
  }
}
trait IActionManagerFactory {
  def createActionManager(playingField: IPlayingField): ActionManager
}
