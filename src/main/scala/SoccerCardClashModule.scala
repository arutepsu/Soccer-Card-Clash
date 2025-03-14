import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides, Singleton, TypeLiteral}
import controller.IController
import controller.base.Controller
import controller.command.factory.*
import model.cardComponent.ICard
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.factory.*
import model.fileIOComponent.*
import model.fileIOComponent.base.FileIO
import model.fileIOComponent.jSONComponent.JsonComponent
import model.fileIOComponent.xmlComponent.XmlComponent
import model.gameComponent.IGame
import model.gameComponent.base.Game
import model.gameComponent.factory.*
import model.playerComponent.IPlayer
import model.playerComponent.base.Player
import model.playerComponent.factory.*
import model.playerComponent.playerRole.*
import model.playingFiledComponent.*
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.dataStructure.{HandCardsQueueDeserializer, IHandCardsQueueFactory, HandCardsQueueFactory}
import model.playingFiledComponent.factory.*
import model.playingFiledComponent.manager.*
import model.playingFiledComponent.manager.base.*
import model.playingFiledComponent.strategy.attackStrategy.*
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.refillStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.base.{PlayerScores, StandardScoring}
import model.playingFiledComponent.strategy.swapStrategy.*
import util.{Deserializer, Observable}

class SoccerCardClashModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
    bind(classOf[Observable]).to(classOf[Controller]).in(classOf[Singleton])

    bind(classOf[IGame]).to(classOf[Game])
    
    bind(classOf[IPlayerFactory]).to(classOf[PlayerFactory])
    bind(classOf[IPlayingFieldFactory]).to(classOf[PlayingFieldFactory])
    bind(classOf[ICommandFactory]).to(classOf[CommandFactory])
    bind(classOf[IActionManagerFactory]).to(classOf[ActionManagerFactory])
    bind(classOf[IDeckFactory]).to(classOf[DeckFactory])
    bind(classOf[ICardFactory]).to(classOf[CardFactory])
    bind(classOf[IHandCardsQueueFactory]).to(classOf[HandCardsQueueFactory])

    bind(classOf[IGameStateFactory]).to(classOf[GameStateFactory]).asEagerSingleton()
    bind(classOf[IFileIO]).to(classOf[FileIO]).asEagerSingleton()
    
    bind(classOf[IPlayingField]).to(classOf[PlayingField])
    bind(classOf[IPlayingFieldManager]).to(classOf[PlayingFieldManager])
//    bind(classOf[IPlayerHandManager]).to(classOf[PlayerHandManager])
    bind(classOf[IPlayerFieldManager]).to(classOf[PlayerFieldManager])
    
    bind(classOf[IDataManager]).to(classOf[DataManager])
    bind(classOf[IActionManager]).to(classOf[ActionManager])
    bind(classOf[IRolesManager]).to(classOf[RolesManager])
    bind(classOf[IPlayerScores]).to(classOf[PlayerScores])
    bind(classOf[IScoringStrategy]).to(classOf[StandardScoring])
    
    bind(classOf[CardDeserializer])
      .toConstructor(classOf[CardDeserializer].getConstructor(classOf[ICardFactory]))

    bind(classOf[PlayerDeserializer])
      .toConstructor(classOf[PlayerDeserializer].getConstructor(classOf[IPlayerFactory], classOf[CardDeserializer]))

    bind(classOf[PlayingFieldDeserializer])
      .toConstructor(classOf[PlayingFieldDeserializer].getConstructor(classOf[IPlayingFieldFactory], classOf[PlayerDeserializer]))

    bind(classOf[HandCardsQueueDeserializer])
      .toConstructor(classOf[HandCardsQueueDeserializer].getConstructor(classOf[CardDeserializer], classOf[IHandCardsQueueFactory]))
    bind(classOf[IPlayerHandManager])
      .toConstructor(classOf[PlayerHandManager].getConstructor(classOf[IHandCardsQueueFactory]))

    bind(classOf[GameDeserializer])
      .toConstructor(classOf[GameDeserializer].getConstructor(
        classOf[IGameStateFactory],
        classOf[PlayingFieldDeserializer],
        classOf[PlayerDeserializer],
        classOf[HandCardsQueueDeserializer],
        classOf[IHandCardsQueueFactory],
        classOf[CardDeserializer],
      ))
    
    bind(classOf[JsonComponent])
      .toConstructor(classOf[JsonComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])

    bind(classOf[XmlComponent])
      .toConstructor(classOf[XmlComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])
    bind(classOf[FileIO])
      .toConstructor(classOf[FileIO].getConstructor(classOf[JsonComponent], classOf[XmlComponent]))
      .in(classOf[Singleton])
  }

  @Provides
  def providePlayer(playerFactory: IPlayerFactory): IPlayer = {
    val defaultPlayerName = "Player1"
    val defaultCards: List[ICard] = List()
    playerFactory.createPlayer(defaultPlayerName, defaultCards)
  }

  @Provides
  def provideCard(cardFactory: ICardFactory): ICard = {
    val defaultValue = Value.Two
    val defaultSuit = Suit.Clubs
    cardFactory.createCard(defaultValue, defaultSuit)
  }

}