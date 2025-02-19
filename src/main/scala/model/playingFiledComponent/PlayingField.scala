package model.playingFiledComponent
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.RolesManager
import model.playerComponent.base.Player
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, AttackStrategy, DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import util.Observable

import scala.collection.mutable
import scala.util.Try

class PlayingField(
                    val player1: IPlayer,
                    val player2: IPlayer
                  ) extends Observable {

  val fieldState = new FieldState(this, player1, player2)

  val roles = new RolesManager(player1, player2, this)
  val scores = new PlayerScores(player1, player2, this)
  val boostManager = new BoostManager(this, roles, fieldState)

  var attackHandler = new AttackHandler(new SingleAttackStrategy())
  var swapHandler = new SwapHandler(this, roles)

  def setPlayingField(): Unit = {
    fieldState.initializeFields()
    println(s"🏟️ Playing field initialized! Attacker: ${roles.attacker.name}, Defender: ${roles.defender.name}")
  }
  def getAttacker: IPlayer = roles.attacker
  def getDefender: IPlayer = roles.defender
}

