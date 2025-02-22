package model.playingFiledComponent.strategy.attackStrategy

import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.IAttackStrategy

import scala.util.{Failure, Success, Try}

class AttackHandler(val playingField: IPlayingField) extends IAttackManager{
  override def executeAttack(strategy: IAttackStrategy): Boolean = {
    strategy.execute(playingField)
  }
}
trait IAttackManager {
  def executeAttack(strategy: IAttackStrategy): Boolean
}