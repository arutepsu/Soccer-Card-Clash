package module
import model.fileIOComponent.jSONComponent.JsonComponent
import model.fileIOComponent.xmlComponent.XmlComponent
import model.gameComponent.factory.*
import model.gameComponent.state.IGameStateFactory
import model.playingFiledComponent.factory.PlayingFieldDeserializer
import model.playerComponent.factory.PlayerDeserializer
import model.playingFiledComponent.dataStructure.*
import model.cardComponent.factory.CardDeserializer
import com.google.inject.{AbstractModule, Singleton}

class SerializationModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[GameDeserializer])
      .toConstructor(classOf[GameDeserializer].getConstructor(
        classOf[IGameStateFactory],
        classOf[PlayingFieldDeserializer],
        classOf[PlayerDeserializer],
        classOf[HandCardsQueueDeserializer],
        classOf[IHandCardsQueueFactory],
        classOf[CardDeserializer]
      ))

    bind(classOf[JsonComponent])
      .toConstructor(classOf[JsonComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])

    bind(classOf[XmlComponent])
      .toConstructor(classOf[XmlComponent].getConstructor(classOf[GameDeserializer]))
      .in(classOf[Singleton])
  }
}
