package de.htwg.se.soccercardclash.model.gameComponent.factory

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.{DoubleAttackActionCommand, SingleAttackActionCommand}
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.{BoostDefenderActionCommand, BoostGoalkeeperActionCommand}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.{DeckFactory, IDeckFactory}
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.factory.IGameInitializer
import de.htwg.se.soccercardclash.model.gameComponent.state.{GameState, GameStateFactory, IGameState, IGameStateFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.base.PlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.IDeckFactory
import scala.collection.mutable

class GameInitializerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "createGame" should "initialize players and playing field correctly" in {
    val playerFactory = mock[IPlayerFactory]
    val playingFieldFactory = mock[IPlayingFieldFactory]
    val deckFactory = mock[IDeckFactory]

    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]
    val playingField = mock[IPlayingField]
    val rolesManager = mock[IRolesManager]
    val dataManager = mock[IDataManager]
    val actionManager = mock[IActionManager]
    val dummyCard = mock[ICard]
    val dummyHand = List.fill(26)(dummyCard)

    // Return a real deck so .dequeue() works
    val deck = scala.collection.mutable.Queue.fill(52)(dummyCard)

    when(deckFactory.createDeck()).thenReturn(deck)

    when(playerFactory.createPlayer(any(), any())).thenReturn(player1, player2)
    when(player1.getCards).thenReturn(dummyHand)
    when(player2.getCards).thenReturn(dummyHand)

    when(playingFieldFactory.createPlayingField(player1, player2)).thenReturn(playingField)
    when(playingField.getRoles).thenReturn(rolesManager)
    when(playingField.getDataManager).thenReturn(dataManager)
    when(playingField.getActionManager).thenReturn(actionManager)


    val initializer = new GameInitializer(playerFactory, playingFieldFactory, deckFactory)

    initializer.createGame("Alice", "Bob")

    initializer.getPlayer1 shouldBe player1
    initializer.getPlayer2 shouldBe player2
    initializer.getPlayingField shouldBe playingField
    initializer.getActionManager shouldBe actionManager

    verify(rolesManager).setRoles(player1, player2)
    verify(dataManager).initializePlayerHands(dummyHand, dummyHand)
    verify(playingField).setPlayingField()
  }

  "initializeFromState" should "set up the game state from a saved state" in {
    val playerFactory = mock[IPlayerFactory]
    val playingFieldFactory = mock[IPlayingFieldFactory]
    val deckFactory = mock[IDeckFactory]

    val state = mock[IGameState]
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    val player1Hand = new HandCardsQueue(List(mock[ICard]))
    val player2Hand = new HandCardsQueue(List(mock[ICard]))
    val player1Defenders = List(mock[ICard])
    val player2Defenders = List(mock[ICard])
    val player1GK = Some(mock[ICard])
    val player2GK = Some(mock[ICard])

    val playingField = mock[IPlayingField]
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

    verify(dataManager).initializePlayerHands(player1Hand.toList, player2Hand.toList)
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
