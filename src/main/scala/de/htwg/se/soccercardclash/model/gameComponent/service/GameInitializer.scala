package de.htwg.se.soccercardclash.model.gameComponent.service

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameInitializer
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManagerFactory, IRolesFactory, IScoresFactory}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.playerComponent.strategyAI.IAIStrategy
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
  def createGameStateWithAI(humanName: String, strategy: IAIStrategy): IGameState
}
class GameInitializer @Inject()(
                                 playerFactory: IPlayerFactory,
                                 deckFactory: IDeckFactory,
                                 dataManagerFactory: IDataManagerFactory,
                                 rolesFactory: IRolesFactory,
                                 scoresFactory: IScoresFactory
                               ) extends IGameInitializer {


  override def createGameState(playerName1: String, playerName2: String): IGameState = {
    val (p1, p2) = createPlayers(playerName1, playerName2)
    val deck = deckFactory.createDeck()
    deckFactory.shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    val dataManager = dataManagerFactory.createFromData(
      player1 = p1,
      player1Hand = hand1,
      player2 = p2,
      player2Hand = hand2,
      player1Defenders = List.empty,
      player2Defenders = List.empty,
      player1Goalkeeper = None,
      player2Goalkeeper = None
    )

    val roles = rolesFactory.create(p1, p2)
    val scores = scoresFactory.create(p1, p2)

    GameState(
      dataManager = dataManager,
      roles = roles,
      scores = scores
    )
  }


  override def initializeFromState(state: IGameState): IGameState = {
    val p1 = state.getPlayer1
    val p2 = state.getPlayer2

    val dataManager = dataManagerFactory.createFromData(
      player1 = p1,
      player1Hand = state.getDataManager.getPlayerHand(p1).toList,
      player2 = p2,
      player2Hand = state.getDataManager.getPlayerHand(p2).toList,
      player1Defenders = state.getDataManager.getPlayerDefenders(p1),
      player2Defenders = state.getDataManager.getPlayerDefenders(p2),
      player1Goalkeeper = state.getDataManager.getPlayerGoalkeeper(p1),
      player2Goalkeeper = state.getDataManager.getPlayerGoalkeeper(p2)
    )

    val roles = rolesFactory.create(
      state.getRoles.attacker,
      state.getRoles.defender
    )

    val scores = scoresFactory.create(p1, p2)
      .setScorePlayer1(state.getScores.getScorePlayer1)
      .setScorePlayer2(state.getScores.getScorePlayer2)

    GameState(dataManager, roles, scores)
  }

  override def createGameStateWithAI(humanName: String, strategy: IAIStrategy): IGameState = {
    val aiName = "AI"
    val aiPlayer = playerFactory.createAIPlayer(aiName, strategy)
    val humanPlayer = playerFactory.createPlayer(humanName)

    val deck = deckFactory.createDeck()
    deckFactory.shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    val dataManager = dataManagerFactory.createFromData(
      player1 = humanPlayer,
      player1Hand = hand1,
      player2 = aiPlayer,
      player2Hand = hand2,
      player1Defenders = List.empty,
      player2Defenders = List.empty,
      player1Goalkeeper = None,
      player2Goalkeeper = None
    )

    val roles = rolesFactory.create(humanPlayer, aiPlayer) // Ensure AI is attacker
    val scores = scoresFactory.create(humanPlayer, aiPlayer)

    GameState(dataManager, roles, scores)
  }


  private def createPlayers(playerName1: String, playerName2: String): (IPlayer, IPlayer) =
    (playerFactory.createPlayer(playerName1), playerFactory.createPlayer(playerName2))


}
