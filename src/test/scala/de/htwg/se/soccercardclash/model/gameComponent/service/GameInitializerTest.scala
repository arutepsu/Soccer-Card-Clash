package de.htwg.se.soccercardclash.model.gameComponent.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameInitializer
import de.htwg.se.soccercardclash.model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.IDeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles}

import scala.collection.mutable

class GameInitializerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "createGame" should "initialize players and playing field correctly" in {
    val playerFactory = new PlayerFactory() // using the real one

    val playingFieldFactory = mock[IPlayingFieldFactory]
    val deckFactory = mock[IDeckFactory]

    val dummyCard = mock[ICard]
    val deck = scala.collection.mutable.Queue.fill(52)(dummyCard)

    val playingField = mock[IGameState]
    val rolesManager = mock[IRoles]
    val dataManager = mock[IDataManager]
    val actionManager = mock[IActionManager]

    when(deckFactory.createDeck()).thenReturn(deck)
    when(deckFactory.shuffleDeck(deck)).thenAnswer(_ => ())

    // Let the real factory handle player creation
    val player1 = playerFactory.createPlayer("Alice")
    val player2 = playerFactory.createPlayer("Bob")

    when(playingFieldFactory.createPlayingField(player1, player2)).thenReturn(playingField)
    when(playingField.getRoles).thenReturn(rolesManager)
    when(playingField.getDataManager).thenReturn(dataManager)
    when(playingField.getActionManager).thenReturn(actionManager)

    val initializer = new GameInitializer(playerFactory, playingFieldFactory, deckFactory)
    initializer.createGame("Alice", "Bob")

    // Assertions
    initializer.getPlayer1 shouldBe player1
    initializer.getPlayer2 shouldBe player2
    initializer.getPlayingField shouldBe playingField
    initializer.getActionManager shouldBe actionManager

    verify(deckFactory).shuffleDeck(deck)
    verify(rolesManager).setRoles(player1, player2)
    verify(playingField).setPlayingField()

    verify(dataManager).initializePlayerHands(
      argThat((hand: List[ICard]) => hand.size == 26),
      argThat((hand: List[ICard]) => hand.size == 26)
    )
  }

  "initializeFromState" should "set up the game state from a saved state" in {
    val playerFactory = new PlayerFactory() // using the real one

    val playingFieldFactory = mock[IPlayingFieldFactory]
    val deckFactory = mock[IDeckFactory]

    val player1 = playerFactory.createPlayer("Alice")
    val player2 = playerFactory.createPlayer("Bob")

    val player1Card = mock[ICard]
    val player2Card = mock[ICard]
    val player1Hand = new HandCardsQueue(List(player1Card))
    val player2Hand = new HandCardsQueue(List(player2Card))
    val player1Defenders = List(mock[ICard])
    val player2Defenders = List(mock[ICard])
    val player1GK = Some(mock[ICard])
    val player2GK = Some(mock[ICard])

    val state = mock[IGameState]
    val playingField = mock[IGameState]
    val dataManager = mock[IDataManager]

    when(state.player1).thenReturn(player1)
    when(state.player2).thenReturn(player2)
    when(state.player1Hand).thenReturn(player1Hand)
    when(state.player2Hand).thenReturn(player2Hand)
    when(state.player1Defenders).thenReturn(player1Defenders)
    when(state.player2Defenders).thenReturn(player2Defenders)
    when(state.player1Goalkeeper).thenReturn(player1GK)
    when(state.player2Goalkeeper).thenReturn(player2GK)

    when(playingFieldFactory.createPlayingField(player1, player2)).thenReturn(playingField)
    when(playingField.getDataManager).thenReturn(dataManager)

    val initializer = new GameInitializer(playerFactory, playingFieldFactory, deckFactory)

    initializer.initializeFromState(state)

    verify(dataManager).initializePlayerHands(List(player1Card), List(player2Card))
    verify(dataManager).setPlayerDefenders(player1, player1Defenders)
    verify(dataManager).setPlayerDefenders(player2, player2Defenders)
    verify(dataManager).setPlayerGoalkeeper(player1, player1GK)
    verify(dataManager).setPlayerGoalkeeper(player2, player2GK)
    verify(playingField).setPlayingField()

    initializer.getPlayer1 shouldBe player1
    initializer.getPlayer2 shouldBe player2
    initializer.getPlayingField shouldBe playingField
  }
}
