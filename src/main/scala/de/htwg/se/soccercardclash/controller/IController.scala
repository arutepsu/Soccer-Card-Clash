package de.htwg.se.soccercardclash.controller

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.*

import scala.collection.mutable
trait IController extends Observable {

  def singleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean)
  def doubleAttack(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean)
  def regularSwap(index: Int, ctx: GameContext): (GameContext, Boolean)
  def reverseSwap(ctx: GameContext): (GameContext, Boolean)
  def boostDefender(defenderIndex: Int, ctx: GameContext): (GameContext, Boolean)
  def boostGoalkeeper(ctx: GameContext): (GameContext, Boolean)
  def createGame(player1: String, player2: String): Unit
  def loadGame(fileName: String): Boolean
  def saveGame(ctx: GameContext): Boolean
  def undo(ctx: GameContext): GameContext
  def redo(ctx: GameContext): GameContext
  def quit(): Unit

}
