import com.google.inject.AbstractModule
import model.gameComponent.base.Game
import model.gameComponent.IGame
import model.playerComponent.factories.{IPlayerFactory, PlayerFactory}
import model.playingFiledComponent.factories.{IPlayingFieldFactory, PlayingFieldFactory}
import model.playingFiledComponent.factories.{IActionManagerFactory, ActionManagerFactory}
import controller.IController
import controller.base.Controller

class SoccerCardClashModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IGame]).to(classOf[Game])
    bind(classOf[IController]).to(classOf[Controller])

    bind(classOf[IPlayerFactory]).to(classOf[PlayerFactory])
    bind(classOf[IPlayingFieldFactory]).to(classOf[PlayingFieldFactory])
    bind(classOf[IActionManagerFactory]).to(classOf[ActionManagerFactory])
  }
}
