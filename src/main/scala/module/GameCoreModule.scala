package module
import model.gameComponent.factory.*
import model.gameComponent.state.*
import model.gameComponent.io.*
import model.gameComponent.*
import model.gameComponent.base.Game
import model.playingFiledComponent.dataStructure.IHandCardsQueueFactory
import model.fileIOComponent.IFileIO
import model.cardComponent.factory.IDeckFactory
import model.playingFiledComponent.factory.*
import model.playerComponent.factory.IPlayerFactory
import com.google.inject.AbstractModule

class GameCoreModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IGameInitializer])
      .toConstructor(classOf[GameInitializer]
        .getConstructor(classOf[IPlayerFactory], classOf[IPlayingFieldFactory], classOf[IDeckFactory]))

    bind(classOf[IGameStateFactory])
      .toConstructor(classOf[GameStateFactory]
        .getConstructor(classOf[IHandCardsQueueFactory]))
      .asEagerSingleton()

    bind(classOf[IGameStateManager])
      .toConstructor(classOf[GameStateManager]
        .getConstructor(classOf[IGameStateFactory], classOf[IHandCardsQueueFactory]))

    bind(classOf[IGamePersistence])
      .toConstructor(classOf[GamePersistence]
        .getConstructor(classOf[IFileIO]))

    bind(classOf[IGame])
      .toConstructor(classOf[Game].getConstructor(
        classOf[IGameInitializer],
        classOf[IGameStateManager],
        classOf[IGamePersistence],
      ))
  }
}

