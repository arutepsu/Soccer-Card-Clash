package model.playerComponent.playerFactory

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.PlayerRole
import model.playerComponent.base.Player
object PlayerFactory {
  def createPlayer(name: String, cards: List[ICard]): IPlayer = {
    Player(name, cards)
  }
}
