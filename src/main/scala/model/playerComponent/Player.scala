package model.playerComponent
import model.cardComponent.Card
import scala.collection.mutable

case class Player(
                   name: String,
                   cards: List[Card],
                   usedBoostCard: Option[Boolean] = Some(false),
                   usedExecuteAttack: Option[Boolean] = Some(false)
                 ) {
  override def toString: String = s"Player: $name, Cards: ${cards.mkString(", ")}"
  
  def getCards: List[Card] = cards
  
  def setName(newName: String): Player = {
    this.copy(name = newName)
  }

  def useBoostCard(): Player = {
    if (usedBoostCard.contains(true)) {
      println(s"$name has already used BoostCard!")
      this
    } else {
      println(s"$name is using BoostCard!")
      this.copy(usedBoostCard = Some(true))
    }
  }

  def useExecuteAttack(): Player = {
    if (usedExecuteAttack.contains(true)) {
      println(s"$name has already used ExecuteAttack!")
      this
    } else {
      println(s"$name is using ExecuteAttack!")
      this.copy(usedExecuteAttack = Some(true))
    }
  }
}
