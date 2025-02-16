package view.components

import model.cardComponent.cardBaseImplementation.{Card, Suit, Value}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.Pos
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import view.components.cardComponents.{FieldCard, GoalkeeperCard}

class PlayerFieldCards(player: Player, playingField: PlayingField) extends VBox {
//
//  alignment = Pos.CENTER
//  spacing = 10
//
//  /** Retrieves player's defenders, goalkeeper */
//  private def getDefenderCards: List[Card] = playingField.playerDefenders(player)
//  private def getGoalkeeperCard: Option[Card] = playingField.playerGoalkeeper(player)
//
//  /** Updates the field display dynamically */
//  def updateField(): Unit = {
//    children.clear()
//    children.addAll(createDefenderRow(), createGoalkeeperRow())
//  }
//
//  /** Creates ImageViews for Defender Cards */
//  private def createDefenderRow(): HBox = {
//    val defenderCards = getDefenderCards match {
//      case Nil => List.fill(3)(FieldCard(flipped = true, card = Card(Value.Two, Suit.Clubs))) // Default placeholders
//      case cards => cards.map(card => FieldCard(flipped = true, card = card))
//    }
//
//    new HBox {
//      alignment = Pos.CENTER
//      spacing = 10
//      children = defenderCards
//    }
//  }
//
//  /** Creates an ImageView for the Goalkeeper */
//  private def createGoalkeeperRow(): HBox = {
//    val goalkeeperCard = getGoalkeeperCard match {
//      case Some(card) => card
//      case None =>
//        // ⚠️ If there's no goalkeeper, automatically assign the highest card
//        val highestCard = getDefenderCards.maxByOption(card => card.valueToInt(card.value))
//        highestCard.foreach(card => playingField.setPlayerGoalkeeper(player, Some(card))) // ✅ Set the new goalkeeper
//        highestCard.getOrElse(Card(Value.Ace, Suit.Spades)) // Fallback goalkeeper if no cards exist
//    }
//
//    new HBox {
//      alignment = Pos.CENTER
//      spacing = 10
//      children = Seq(GoalkeeperCard(flipped = true, card = goalkeeperCard))
//    }
//  }
}

