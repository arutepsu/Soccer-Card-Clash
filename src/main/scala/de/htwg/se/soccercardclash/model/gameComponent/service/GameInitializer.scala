package de.htwg.se.soccercardclash.model.gameComponent.service

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameInitializer
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCardsFactory, IRolesFactory, IScoresFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.util.UndoManager
import play.api.libs.json.*

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}
import scala.xml.*

trait IGameInitializer {
  def createGameState(playerName1: String, playerName2: String): IGameState

  def initializeFromState(state: IGameState): IGameState

  def createGameStateWithAI(humanName: String, aiPlayer: IPlayer): IGameState
}

class GameInitializer @Inject()(
                                 playerFactory: IPlayerFactory,
                                 deckFactory: IDeckFactory,
                                 gameCardsFactory: IGameCardsFactory,
                                 rolesFactory: IRolesFactory,
                                 scoresFactory: IScoresFactory
                               ) extends IGameInitializer {


  override def createGameState(playerName1: String, playerName2: String): IGameState = {
    val (p1, p2) = createPlayers(playerName1, playerName2)
    val deck = deckFactory.createDeck()
    deckFactory.shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    val gameCards = gameCardsFactory.createFromData(
      attacker = p1,
      attackerHand = hand1,
      defender = p2,
      defenderHand = hand2,
      attackerDefenders = List.empty,
      defenderDefenders = List.empty,
      attackerGoalkeeper = None,
      defenderGoalkeeper = None
    )

    val roles = rolesFactory.create(p1, p2)
    val scores = scoresFactory.create(Seq(p1, p2))

    GameState(
      gameCards = gameCards,
      roles = roles,
      scores = scores
    )
  }

  private def createPlayers(playerName1: String, playerName2: String): (IPlayer, IPlayer) =
    (playerFactory.createPlayer(playerName1), playerFactory.createPlayer(playerName2))

  override def initializeFromState(state: IGameState): IGameState = {
    val p1 = state.getRoles.attacker
    val p2 = state.getRoles.defender

    val gameCards = gameCardsFactory.createFromData(
      attacker = p1,
      attackerHand = state.getGameCards.getPlayerHand(p1).toList,
      defender = p2,
      defenderHand = state.getGameCards.getPlayerHand(p2).toList,
      attackerDefenders = state.getGameCards.getPlayerDefenders(p1),
      defenderDefenders = state.getGameCards.getPlayerDefenders(p2),
      attackerGoalkeeper = state.getGameCards.getPlayerGoalkeeper(p1),
      defenderGoalkeeper = state.getGameCards.getPlayerGoalkeeper(p2)
    )

    val roles = rolesFactory.create(
      state.getRoles.attacker,
      state.getRoles.defender
    )

    val scores = scoresFactory.createWithScores(Map(
      p1 -> state.getScores.getScore(p1),
      p2 -> state.getScores.getScore(p2)
    ))

    GameState(gameCards, roles, scores)
  }

  override def createGameStateWithAI(humanName: String, aiPlayer: IPlayer): IGameState = {
    val humanPlayer = playerFactory.createPlayer(humanName)

    val deck = deckFactory.createDeck()
    deckFactory.shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    val gameCards = gameCardsFactory.createFromData(
      attacker = humanPlayer,
      attackerHand = hand1,
      defender = aiPlayer,
      defenderHand = hand2,
      attackerDefenders = List.empty,
      defenderDefenders = List.empty,
      attackerGoalkeeper = None,
      defenderGoalkeeper = None
    )

    val roles = rolesFactory.create(humanPlayer, aiPlayer)
    val scores = scoresFactory.create(Seq(humanPlayer, aiPlayer))

    GameState(gameCards, roles, scores)
  }


}
