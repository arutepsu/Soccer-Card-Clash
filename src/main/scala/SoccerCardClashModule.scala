import com.google.inject.{AbstractModule, Provides, Singleton}
import model.gameComponent.base.Game
import com.google.inject.name.Names
import model.gameComponent.IGame
import model.playerComponent.factory.{IPlayerFactory, PlayerFactory}
import model.playingFiledComponent.factory.{IPlayingFieldFactory, PlayingFieldFactory}
import model.playingFiledComponent.factory.{ActionManagerFactory, IActionManagerFactory}
import controller.IController
import controller.base.Controller
import controller.command.factory.*
import model.playingFiledComponent.strategy.swapStrategy.*
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.attackStrategy.*
import model.playingFiledComponent.strategy.refillStrategy.*
import model.playingFiledComponent.strategy.swapStrategy.*
import model.cardComponent.factory.*
import model.playerComponent.IPlayer
import model.playerComponent.base.Player
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import model.playerComponent.playerRole.*
import model.playingFiledComponent.manager.*
import model.playingFiledComponent.factory.*
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playerComponent.factory.*
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.{AbstractModule, Provides, Singleton}
import model.playingFiledComponent.*
import com.google.inject.{AbstractModule, Singleton}
import com.google.inject.assistedinject.FactoryModuleBuilder
import model.playingFiledComponent.strategy.scoringStrategy.*
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.google.inject.assistedinject.FactoryModuleBuilder
import model.cardComponent.{CardDeserializer, ICard}
import model.cardComponent.base.components.{Suit, Value}
import model.playingFiledComponent.manager.base.*
import model.gameComponent.factory.*
import model.playingFiledComponent.strategy.scoringStrategy.base.{PlayerScores, StandardScoring}
import model.fileIOComponent.*
import model.fileIOComponent.jSONComponent.JsonComponent
import model.fileIOComponent.xmlComponent.XmlComponent
import model.playingFiledComponent.dataStructure.HandCardsQueueDeserializer
import util.{Deserializer, Observable}
import com.google.inject.TypeLiteral

class SoccerCardClashModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
    bind(classOf[Observable]).to(classOf[Controller]).in(classOf[Singleton])

    bind(classOf[IGame]).to(classOf[Game])

    // ✅ Bind factories
    bind(classOf[IPlayerFactory]).to(classOf[PlayerFactory])
    bind(classOf[IPlayingFieldFactory]).to(classOf[PlayingFieldFactory])
    bind(classOf[ICommandFactory]).to(classOf[CommandFactory])
    bind(classOf[IActionManagerFactory]).to(classOf[ActionManagerFactory])
    bind(classOf[IDeckFactory]).to(classOf[DeckFactory])
    bind(classOf[ICardFactory]).to(classOf[CardFactory])


    // ✅ Bind game state and file handling
    bind(classOf[IGameStateFactory]).to(classOf[GameStateFactory]).asEagerSingleton()
    bind(classOf[IFileIO]).to(classOf[FileIO]).asEagerSingleton()

    // ✅ Bind playing field-related components
    bind(classOf[IPlayingField]).to(classOf[PlayingField])
    bind(classOf[IPlayingFieldManager]).to(classOf[PlayingFieldManager])
    bind(classOf[IPlayerHandManager]).to(classOf[PlayerHandManager])
    bind(classOf[IPlayerFieldManager]).to(classOf[PlayerFieldManager])

    // ✅ Bind strategies and managers
    bind(classOf[IDataManager]).to(classOf[DataManager])
    bind(classOf[IActionManager]).to(classOf[ActionManager])
    bind(classOf[IRolesManager]).to(classOf[RolesManager])
    bind(classOf[IPlayerScores]).to(classOf[PlayerScores])
    bind(classOf[IScoringStrategy]).to(classOf[StandardScoring])

    // ✅ Bind deserializers (Ensure Singleton for consistency)
    bind(classOf[CardDeserializer])
      .toConstructor(classOf[CardDeserializer].getConstructor(classOf[ICardFactory]))

    bind(classOf[PlayerDeserializer])
      .toConstructor(classOf[PlayerDeserializer].getConstructor(classOf[IPlayerFactory], classOf[CardDeserializer]))

    bind(classOf[PlayingFieldDeserializer])
      .toConstructor(classOf[PlayingFieldDeserializer].getConstructor(classOf[IPlayingFieldFactory], classOf[PlayerDeserializer]))

    bind(classOf[HandCardsQueueDeserializer])
      .toConstructor(classOf[HandCardsQueueDeserializer].getConstructor(classOf[CardDeserializer]))

    bind(classOf[GameDeserializer])
      .toConstructor(classOf[GameDeserializer].getConstructor(
        classOf[IGameStateFactory],
        classOf[PlayingFieldDeserializer],
        classOf[PlayerDeserializer],
        classOf[HandCardsQueueDeserializer],
        classOf[CardDeserializer]
      ))


    // ✅ Bind JSON & XML Components
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
    val defaultCards: List[ICard] = List() // Replace with actual card retrieval logic if needed
    playerFactory.createPlayer(defaultPlayerName, defaultCards)
  }

  @Provides
  def provideCard(cardFactory: ICardFactory): ICard = {
    val defaultValue = Value.Two // Replace with actual value retrieval logic if needed
    val defaultSuit = Suit.Clubs // Replace with actual suit retrieval logic if needed
    cardFactory.createCard(defaultValue, defaultSuit) // FIXED TYPO
  }

}