package module

import com.google.inject.{Guice, Injector}
import model.cardComponent.ICard
import model.cardComponent.base.components._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.cardComponent.factory.*

class CardModuleTest extends AnyFlatSpec with Matchers {

  "CardModule" should "bind ICardFactory to CardFactory" in {
    // Create a Guice injector with the CardModule
    val injector: Injector = Guice.createInjector(new CardModule)

    // Retrieve ICardFactory from the injector
    val cardFactory = injector.getInstance(classOf[ICardFactory])

    // Verify that the instance returned is of type CardFactory
    cardFactory shouldBe a[CardFactory]
  }

  it should "provide a Card using the ICardFactory" in {
    // Create a Guice injector with the CardModule
    val injector: Injector = Guice.createInjector(new CardModule)

    // Retrieve ICard from the injector
    val card = injector.getInstance(classOf[ICard])

    // Verify that the card is created with the correct values
    card.value shouldBe Value.Two
    card.suit shouldBe Suit.Clubs
  }

  it should "bind IDeckFactory to DeckFactory" in {
    // Create a Guice injector with the CardModule
    val injector: Injector = Guice.createInjector(new CardModule)

    // Retrieve IDeckFactory from the injector
    val deckFactory = injector.getInstance(classOf[IDeckFactory])

    // Verify that the instance returned is of type DeckFactory
    deckFactory shouldBe a[DeckFactory]
  }

  it should "bind CardDeserializer to the constructor of CardDeserializer" in {
    // Create a Guice injector with the CardModule
    val injector: Injector = Guice.createInjector(new CardModule)

    // Retrieve CardDeserializer from the injector
    val cardDeserializer = injector.getInstance(classOf[CardDeserializer])

    // Verify that the CardDeserializer has been injected with an ICardFactory
    cardDeserializer shouldBe a[CardDeserializer]
  }
}
