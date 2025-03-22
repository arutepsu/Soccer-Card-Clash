package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.attackStrategy

import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.attackStrategy.IAttackStrategy


import scala.util.{Failure, Success, Try}

class AttackHandler(val playingField: IPlayingField) extends IAttackManager{
  override def executeAttack(strategy: IAttackStrategy): Boolean = {
    strategy.execute(playingField)
  }
}
trait IAttackManager {
  def executeAttack(strategy: IAttackStrategy): Boolean
}