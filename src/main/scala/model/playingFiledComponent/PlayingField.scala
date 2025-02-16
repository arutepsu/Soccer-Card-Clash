package model.playingFiledComponent
import model.cardComponent.base.Card
import model.cardComponent.cardFactory.DeckFactory
import model.playerComponent.Player
import model.playingFiledComponent.state.gameState.GameState
import model.playingFiledComponent.state.roleState.PlayerRoles
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, AttackStrategy, DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import model.playingFiledComponent.strategy.swapStrategy.*
import util.Observable

import scala.collection.mutable
import scala.util.Try

class PlayingField(
                    val player1: Player,
                    val player2: Player
                  ) extends Observable {

  val fieldState = new FieldState(this, player1, player2)

  val roles = new PlayerRoles(player1, player2, this)
  val scores = new PlayerScores(player1, player2, this)
  val boostManager = new BoostManager(this, roles, fieldState)
  var attackHandler = new AttackHandler(new SingleAttackStrategy())
  var swapHandler = new SwapHandler(this, roles)
  def setPlayingField(): Unit = fieldState.initializeFields()
  def getAttacker: Player = roles.attacker
  def getDefender: Player = roles.defender
  
}
