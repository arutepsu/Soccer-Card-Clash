package de.htwg.se.soccercardclash.view.tui

import java.io.File
import scala.io.StdIn
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.LoadGameTuiCommand

class Prompter(controller: IController, gameContextHolder: IGameContextHolder) extends IPrompter {

  def promptPlayersName(): Unit = {
    println(f"Enter player names (format: `player1 player2`):${TuiKeys.CreatePlayers.toString}")
  }

  def promptRegularAttack(): Unit = {
    println("⚔Select a defender to attack (enter position):")
  }

  def promptDoubleAttack(): Unit = {
    println("Select a defender to attack (enter position):")
  }

  def promptBoost(): Unit = {
    println("Choose a defender to boost (enter position):")
  }

  def promptSwap(): Unit = {
    println("Choose a card to swap from attacker's hand (enter position):")
  }
  def promptNewGame(): Unit = {
    println("Creating a new game!")
  }

  def promptPlayingField(): Unit = {
    println("Game started!:")
  }

  def promptShowAttackersHand(): Unit = {
    val playingField = gameContextHolder.get.state
    val attacker = playingField.getRoles.attacker
    val hand = playingField.getGameCards.getPlayerHand(attacker).toList

    println("\n===================================")
    println(s"${attacker.name}'s hand cards: ")
    println(if (hand.nonEmpty) hand.mkString(", ") else "No cards left!")
    println("===================================")
  }


  def promptShowDefendersField(player: IPlayer): Unit = {

    val field = gameContextHolder.get.state
    println("\n===================================")
    println(f"${player.name}'s defender cards: ")
    println(f"${field.getGameCards.getPlayerDefenders(player)}")
    println("===================================")

  }

  def promptShowGoalkeeper(player: IPlayer): Unit = {

    val field = gameContextHolder.get.state
    println("\n===================================")
    println(f"${player.name}'s goalkeeper Card: ")
    println(f"${field.getGameCards.getPlayerGoalkeeper(player)}")
    println("===================================")

  }

  def printGameState(): Unit = {
    val playingField = gameContextHolder.get.state
    val attacker = playingField.getRoles.attacker
    val defender = playingField.getRoles.defender

    println("\n===================================")
    println("**CURRENT GAME STATE**")
    println("===================================")

    println(s"Attacker: ${attacker.name}")
    println(s"Defender: ${defender.name}")
    println("-----------------------------------")

    val attackerHand = playingField.getGameCards.getPlayerHand(attacker).toList
    println(s"${attacker.name}'s Hand: " +
      (if (attackerHand.nonEmpty) attackerHand.mkString(", ") else "No cards left!")
    )

    val defenderField = playingField.getGameCards.getPlayerDefenders(defender)
    println(s"${defender.name}'s Defenders: " +
      (if (defenderField.nonEmpty) defenderField.mkString(", ") else "No defenders!")
    )

    println("===================================")
  }

  def promptExit(): Unit = {
    println("Goodbye!")
  }
  def promptMainMenu(): Unit = {
    println(":'######:::'#######:::'######:::'######::'########:'########::::::'######:::::'###::::'########::'########::::::'######::'##::::::::::'###:::::'######::'##::::'##:")
    println("'##... ##:'##.... ##:'##... ##:'##... ##: ##.....:: ##.... ##::::'##... ##:::'## ##::: ##.... ##: ##.... ##::::'##... ##: ##:::::::::'## ##:::'##... ##: ##:::: ##:")
    println(" ##:::..:: ##:::: ##: ##:::..:: ##:::..:: ##::::::: ##:::: ##:::: ##:::..:::'##:. ##:: ##:::: ##: ##:::: ##:::: ##:::..:: ##::::::::'##:. ##:: ##:::..:: ##:::: ##:")
    println(". ######:: ##:::: ##: ##::::::: ##::::::: ######::: ########::::: ##:::::::'##:::. ##: ########:: ##:::: ##:::: ##::::::: ##:::::::'##:::. ##:. ######:: #########:")
    println(":..... ##: ##:::: ##: ##::::::: ##::::::: ##...:::: ##.. ##:::::: ##::::::: #########: ##.. ##::: ##:::: ##:::: ##::::::: ##::::::: #########::..... ##: ##.... ##:")
    println("'##::: ##: ##:::: ##: ##::: ##: ##::: ##: ##::::::: ##::. ##::::: ##::: ##: ##.... ##: ##::. ##:: ##:::: ##:::: ##::: ##: ##::::::: ##.... ##:'##::: ##: ##:::: ##:")
    println(". ######::. #######::. ######::. ######:: ########: ##:::. ##::::. ######:: ##:::: ##: ##:::. ##: ########:::::. ######:: ########: ##:::: ##:. ######:: ##:::: ##:")
    println("=========================================")
    println("      Welcome to Soccer Card Clash!      ")
    println("=========================================")
    println("\nMain Menu:")
    println(":startAI - SinglePlayer")
    println(":startM - MultiPlayer")
    println(":load - Load Game")
    println(":exit - Exit")

  }
  def promptCreatePlayers() : Unit ={
    println("Creating Players....")
  }

  def showAvailableGames(): Unit = {
    val saveDirectory = new File("games")

    if (!saveDirectory.exists() || !saveDirectory.isDirectory) {
      println("The save directory 'games/' does not exist.")
      return
    }

    val savedGames = saveDirectory.listFiles()
      .filter(file => file.getName.endsWith(".xml") || file.getName.endsWith(".json"))
      .map(_.getName)

    if (savedGames.isEmpty) {
      println("No saved games found in 'games/'.")
      return
    }

    println("\nAvailable saved games:")
    savedGames.zipWithIndex.foreach { case (fileName, index) =>
      println(s"  ${index + 1}. $fileName")
    }

    println("\nType 'select <number>' to load a game.")
  }

  def promptSaveGame(): Unit = {
    println("Game saved successfully!")
  }

  def promptUndo(): Unit = {
    println("Undo")
  }

  def promptRedo(): Unit = {
    println("Redo")
  }
}
trait IPrompter {
  def promptPlayersName(): Unit
  def promptRegularAttack(): Unit
  def promptDoubleAttack(): Unit
  def promptBoost(): Unit
  def promptSwap(): Unit
  def promptNewGame(): Unit
  def promptPlayingField(): Unit
  def promptShowAttackersHand(): Unit
  def promptShowDefendersField(player: IPlayer): Unit
  def promptShowGoalkeeper(player: IPlayer): Unit
  def printGameState(): Unit
  def promptExit(): Unit
  def promptMainMenu(): Unit
  def promptCreatePlayers(): Unit
  def showAvailableGames(): Unit
  def promptSaveGame(): Unit
  def promptUndo(): Unit
  def promptRedo(): Unit
}
