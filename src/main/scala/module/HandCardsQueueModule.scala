package module
import com.google.inject.AbstractModule
import model.playingFiledComponent.dataStructure.*
import model.cardComponent.factory.*

class HandCardsQueueModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IHandCardsQueueFactory]).to(classOf[HandCardsQueueFactory])

    bind(classOf[HandCardsQueueDeserializer])
      .toConstructor(classOf[HandCardsQueueDeserializer]
        .getConstructor(classOf[CardDeserializer], classOf[IHandCardsQueueFactory]))
  }
}
