package model.playingFiledComponent.manager.base
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure._
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.*
import model.playingFiledComponent.strategy.refillStrategy.base.StandardRefillStrategy

class PlayerFieldManager extends IPlayerFieldManager {
  private var playerFields: Map[IPlayer, List[ICard]] = Map().withDefaultValue(List())
  private var goalkeepers: Map[IPlayer, Option[ICard]] = Map().withDefaultValue(None)
  private var defenders: Map[IPlayer, List[ICard]] = Map().withDefaultValue(List())

  override def getPlayerField(player: IPlayer): List[ICard] = playerFields(player)

  override def setPlayerField(player: IPlayer, newField: List[ICard]): Unit = {
    playerFields = playerFields.updated(player, newField)
  }

  override def getPlayerGoalkeeper(player: IPlayer): Option[ICard] = goalkeepers(player)

  override def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit = {
    goalkeepers = goalkeepers.updated(player, goalkeeper)
  }

  override def getPlayerDefenders(player: IPlayer): List[ICard] = {
    val playerDefenders = defenders(player) // Retrieve the defenders
    println(s"QQQQQQDEBUG: getPlayerDefenders called for ${player.name}, Defenders: $playerDefenders")
    playerDefenders
  }


  override def setPlayerDefenders(player: IPlayer, newDefenderField: List[ICard]): Unit = {
    defenders = defenders.updated(player, newDefenderField)
  }

  override def setGoalkeeperForAttacker(playingField: IPlayingField, card: ICard): Unit = {
    val attacker = playingField.getAttacker
    goalkeepers = goalkeepers.updated(attacker, Some(card))
    playingField.notifyObservers()
  }
  
  override def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit = {
    defenders = defenders.updated(currentDefender, defenders(currentDefender).filterNot(_ == defenderCard))
  }

  override def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit = {
    goalkeepers = goalkeepers.updated(currentDefender, None)
  }

  override def allDefendersBeaten(currentDefender: IPlayer): Boolean =
    getPlayerDefenders(currentDefender).isEmpty

  override def getDefenderCard(player: IPlayer, index: Int): ICard = {
    val playerDefenders = defenders(player)
    if (index < 0 || index >= playerDefenders.size)
      throw new IndexOutOfBoundsException("Invalid defender index")
    playerDefenders(index)
  }

  override def clearAll(): Unit = {
    playerFields = Map().withDefaultValue(List())
    goalkeepers = Map().withDefaultValue(None)
    defenders = Map().withDefaultValue(List())
    println("🔄 PlayerFieldManager cleared!")
  }

}
trait IPlayerFieldManager {
  def getPlayerField(player: IPlayer): List[ICard]

  def setPlayerField(player: IPlayer, newField: List[ICard]): Unit

  def getPlayerGoalkeeper(player: IPlayer): Option[ICard]

  def setPlayerGoalkeeper(player: IPlayer, goalkeeper: Option[ICard]): Unit

  def getPlayerDefenders(player: IPlayer): List[ICard]

  def setPlayerDefenders(player: IPlayer, newDefenderField: List[ICard]): Unit

  def setGoalkeeperForAttacker(playingField: IPlayingField, card: ICard): Unit

  def removeDefenderCard(currentDefender: IPlayer, defenderCard: ICard): Unit

  def removeDefenderGoalkeeper(currentDefender: IPlayer): Unit

  def allDefendersBeaten(currentDefender: IPlayer): Boolean

  def getDefenderCard(player: IPlayer, index: Int): ICard
  def clearAll(): Unit
}