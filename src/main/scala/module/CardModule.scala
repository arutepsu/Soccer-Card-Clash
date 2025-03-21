package module
import com.google.inject.{AbstractModule, Provides}
import model.cardComponent.factory.*
import model.cardComponent.ICard
import model.cardComponent.base.components.*

class CardModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ICardFactory]).to(classOf[CardFactory])
    bind(classOf[IDeckFactory]).to(classOf[DeckFactory])

    bind(classOf[CardDeserializer])
      .toConstructor(classOf[CardDeserializer]
        .getConstructor(classOf[ICardFactory]))
  }

  @Provides
  def provideCard(factory: ICardFactory): ICard =
    factory.createCard(Value.Two, Suit.Clubs)
}
