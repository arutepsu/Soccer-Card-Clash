package module
import model.playerComponent.factory.*
import model.cardComponent.factory.*
import model.playingFiledComponent.manager.*
import model.playingFiledComponent.dataStructure.*
import model.playerComponent.IPlayer
import com.google.inject.{AbstractModule, Provides}

class PlayerModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IPlayerFactory]).to(classOf[PlayerFactory])
    bind(classOf[IPlayerFieldManager]).to(classOf[PlayerFieldManager])

    bind(classOf[PlayerDeserializer])
      .toConstructor(classOf[PlayerDeserializer]
        .getConstructor(classOf[IPlayerFactory], classOf[CardDeserializer]))

    bind(classOf[IPlayerHandManager])
      .toConstructor(classOf[PlayerHandManager]
        .getConstructor(classOf[IHandCardsQueueFactory]))
  }

  @Provides
  def providePlayer(factory: IPlayerFactory): IPlayer =
    factory.createPlayer("Player1", List.empty)
}
