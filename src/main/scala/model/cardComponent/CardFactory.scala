//package model.cardComponent
//
//import model.cardComponent.Value.Value
//import model.cardComponent.Suit.Suit
//import scala.util.Random
//import model.cardComponent.Card
//object CardFactory {
//
//  /** ✅ Creates a standard card */
//  def createCard(value: Value, suit: Suit): Card =
//    Card.apply(value, suit) // ✅ Calls the manually defined apply method
//
//  /** ✅ Creates a random card */
//  def createRandomCard(): Card = {
//    val randomValue = Value.allValues(Random.nextInt(Value.allValues.length))
//    val randomSuit = Suit.allSuits(Random.nextInt(Suit.allSuits.length))
//    Card.apply(randomValue, randomSuit)
//  }
//
//  /** ✅ Creates a card from a string representation */
//  def createCardFromString(valueStr: String, suitStr: String): Option[Card] = {
//    for {
//      value <- Value.fromString(valueStr)
//      suit  <- Suit.fromString(suitStr)
//    } yield Card.apply(value, suit)
//  }
//
//  /** ✅ Creates a card from an integer value */
//  def createCardFromInt(valueInt: Int, suit: Suit): Option[Card] = {
//    Value.fromInt(valueInt).map(v => Card.apply(v, suit))
//  }
//}
