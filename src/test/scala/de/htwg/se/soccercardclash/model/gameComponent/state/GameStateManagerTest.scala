package de.htwg.se.soccercardclash.model.gameComponent.state

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*

import scala.collection.mutable.Queue
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands.RegularSwapActionCommand
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.io.IGamePersistence
import de.htwg.se.soccercardclash.model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.base.PlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.factory.IGameInitializer
import de.htwg.se.soccercardclash.model.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores

class GameStateManagerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "updateGameState" should "create and store game state using initializer and data manager" in {
    val gameStateFactory = mock[IGameStateFactory]
    val handCardsQueueFactory = mock[IHandCardsQueueFactory]
    val manager = new GameStateManager(gameStateFactory, handCardsQueueFactory)

    val gameInitializer = mock[IGameInitializer]
    val playingField = mock[IPlayingField]
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]
    val dataManager = mock[IDataManager]
    val scores = mock[IPlayerScores]
    val dummyState = mock[IGameState]

    val card = mock[ICard]
    val handQueue1 = new HandCardsQueue(List(card))
    val handQueue2 = new HandCardsQueue(List(card))
    val defenders1 = List(card)
    val defenders2 = List(card)
    val goalkeeper1 = Some(card)
    val goalkeeper2 = Some(card)

    // Mock gameInitializer -> field + players
    when(gameInitializer.getPlayingField).thenReturn(playingField)
    when(gameInitializer.getPlayer1).thenReturn(player1)
    when(gameInitializer.getPlayer2).thenReturn(player2)

    // Mock field -> data + scores
    when(playingField.getDataManager).thenReturn(dataManager)
    when(playingField.getScores).thenReturn(scores)
    when(scores.getScorePlayer1).thenReturn(3)
    when(scores.getScorePlayer2).thenReturn(4)

    // Mock data manager -> hands, defenders, goalkeepers
    val queue1 = new HandCardsQueue(List(card))
    val queue2 = new HandCardsQueue(List(card))

    when(dataManager.getPlayerHand(player1)).thenReturn(queue1)
    when(dataManager.getPlayerHand(player2)).thenReturn(queue2)

    when(dataManager.getPlayerDefenders(player1)).thenReturn(defenders1)
    when(dataManager.getPlayerDefenders(player2)).thenReturn(defenders2)
    when(dataManager.getPlayerGoalkeeper(player1)).thenReturn(goalkeeper1)
    when(dataManager.getPlayerGoalkeeper(player2)).thenReturn(goalkeeper2)

    // Mock hand card creation
    when(handCardsQueueFactory.create(any[List[ICard]])).thenReturn(handQueue1, handQueue2)

    // Mock game state creation
    when(gameStateFactory.create(
      playingField,
      player1,
      player2,
      handQueue1,
      handQueue2,
      defenders1,
      defenders2,
      goalkeeper1,
      goalkeeper2,
      3,
      4
    )).thenReturn(dummyState)

    // Execute
    manager.updateGameState(gameInitializer)

    // Verify
    manager.getGameState shouldBe dummyState
    verify(gameStateFactory).create(
      playingField,
      player1,
      player2,
      handQueue1,
      handQueue2,
      defenders1,
      defenders2,
      goalkeeper1,
      goalkeeper2,
      3,
      4
    )
  }

  "reset" should "call reset on the playing field and return true" in {
    val gameStateFactory = mock[IGameStateFactory]
    val handCardsQueueFactory = mock[IHandCardsQueueFactory]
    val manager = new GameStateManager(gameStateFactory, handCardsQueueFactory)

    val field = mock[IPlayingField]
    val result = manager.reset(field)

    result shouldBe true
    verify(field).reset()
  }

  it should "return false if field is null" in {
    val manager = new GameStateManager(mock[IGameStateFactory], mock[IHandCardsQueueFactory])
    manager.reset(null) shouldBe false
  }
}
