package de.htwg.se.soccercardclash.view.tui

import java.io.File
import scala.io.StdIn
import de.htwg.se.soccercardclash.controller.{Events, IController}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.LoadGameTuiCommand

class Prompter(controller: IController) extends IPrompter {

  def promptPlayersName(): Unit = {
    println(f"👥 Enter player names (format: `player1 player2`):${TuiKeys.CreatePlayers.toString}")
  }

  def promptRegularAttack(): Unit = {
    println("⚔️ Select a defender to attack (enter position):")
  }

  def promptDoubleAttack(): Unit = {
    println("⚔️ Select a defender to attack (enter position):")
  }

  def promptBoost(): Unit = {
    println("🔥 Choose a defender to boost (enter position):")
  }

  def promptSwap(): Unit = {
    println("🔄 Choose a card to swap from attacker's hand (enter position):")
  }
  def promptNewGame(): Unit = {
    println("Creating a new game!")
  }

  def promptPlayingField(): Unit = {
    println("Game started!:")
  }

  def promptShowAttackersField(): Unit = {
    val playingField = controller.getCurrentGame.getPlayingField
    val attacker = playingField.getAttacker

    println("\n===================================")
    println(f"${attacker.name} field cards after boost: ")
    println(f"${playingField.getDataManager.getPlayerField(attacker)}")
    println("===================================")
  }

  def promptShowAttackersHand(): Unit = {
    val playingField = controller.getCurrentGame.getPlayingField
    val attacker = playingField.getAttacker

    println("\n===================================")
    println(f"${attacker.name} field cards after swap: ")
    println(f"${playingField.getDataManager.getPlayerHand(attacker)}")
    println("===================================")
  }
  
  def promptShowDefendersField() : Unit = {
    val playingField = controller.getCurrentGame.getPlayingField
    val defender = playingField.getDefender

    println("\n===================================")
    println(f"${defender.name} field cards after attack: ")
    println(f"${playingField.getDataManager.getPlayerField(defender)}")
    println("===================================")
  }

  def promptShowDefendersHand(): Unit = {
    val playingField = controller.getCurrentGame.getPlayingField
    val defender = playingField.getDefender

    println("\n===================================")
    println(f"${defender.name} field cards after switch: ")
    println(f"${playingField.getDataManager.getPlayerHand(defender)}")
    println("===================================")
  }
  
  
  def printGameState(): Unit = {
    val playingField = controller.getCurrentGame.getPlayingField
    val attacker = playingField.getAttacker
    val defender = playingField.getDefender

    println("\n===================================")
    println("🏆 **CURRENT GAME STATE**")
    println("===================================")

    println(f"⚔️ Attacker: ${attacker.name}")
    println(f"🛡️ Defender: ${defender.name}")
    println("-----------------------------------")

    val attackerHand = playingField.getDataManager.getPlayerHand(attacker)
    println(s"🎴 ${attacker.name}'s Hand: " +
      (if (attackerHand.nonEmpty) attackerHand.mkString(", ") else "No cards left!")
    )

    val defenderField = playingField.getDataManager.getPlayerDefenders(defender)
    println(s"🏟️ ${defender.name}'s Defenders: " +
      (if (defenderField.nonEmpty) defenderField.mkString(", ") else "No defenders!")
    )

    println("===================================")
  }
  def promptExit(): Unit = {
    println("👋 Goodbye!")
  }
  def promptMainMenu(): Unit = {
    println("=========================================")
    println("      Welcome to Soccer Card Clash!      ")
    println("=========================================")
    println("\nMain Menu:")
    println(":start - Create New Game")
    println(":load - Load Game")
    println(":exit - Exit")

  }
  def promptCreatePlayers() : Unit ={
    println("Creating Players....")
  }

  def promptLoadGame(factory: ITuiCommandFactory): Unit = {
    val selectedFile = promptUserForFile()

    if (selectedFile.nonEmpty) {
      val loadGameCommand = factory.createLoadGameTuiCommand(selectedFile)
      loadGameCommand.execute()
    } else {
      println("❌ No valid game selected. Aborting load.")
    }
  }
  def showAvailableGames(): Unit = {
    val saveDirectory = new File("games")

    if (!saveDirectory.exists() || !saveDirectory.isDirectory) {
      println("❌ ERROR: The save directory 'games/' does not exist.")
      return
    }

    val savedGames = saveDirectory.listFiles()
      .filter(file => file.getName.endsWith(".xml") || file.getName.endsWith(".json"))
      .map(_.getName)

    if (savedGames.isEmpty) {
      println("⚠️ No saved games found in 'games/'.")
      return
    }

    println("\n📂 Available saved games:")
    savedGames.zipWithIndex.foreach { case (fileName, index) =>
      println(s"  ${index + 1}. $fileName")
    }

    println("\n📝 Type 'select <number>' to load a game.")
  }

  def loadSelectedGame(index: Int, factory: ITuiCommandFactory): Unit = {
    val saveDirectory = new File("games")
    val savedGames = saveDirectory.listFiles()
      .filter(file => file.getName.endsWith(".xml") || file.getName.endsWith(".json"))
      .map(_.getName)

    if (index >= 0 && index < savedGames.length) {
      val selectedFile = savedGames(index)
      val loadGameCommand = factory.createLoadGameTuiCommand(selectedFile)
      loadGameCommand.execute()
    } else {
      println("❌ Invalid selection.")
    }
  }

  def promptSaveGame(): Unit = {
    println("✅ Game saved successfully!")
  }

  def promptUserForFile(): String = {
    controller.notifyObservers(Events.LoadGame)
    val saveDirectory = new File("games")

    if (!saveDirectory.exists() || !saveDirectory.isDirectory) {
      println("❌ ERROR: The save directory 'games/' does not exist.")
      return ""
    }

    val savedGames = saveDirectory.listFiles()
      .filter(file => file.getName.endsWith(".xml") || file.getName.endsWith(".json"))
      .map(_.getName)

    if (savedGames.isEmpty) {
      println("⚠️ No saved games found in 'games/'.")
      return ""
    }

    println("\n📂 Available saved games:")
    savedGames.zipWithIndex.foreach { case (fileName, index) =>
      println(s"  ${index + 1}. $fileName")
    }

    print("\n✏️ Enter the number of the game you want to load: ")
    val choice = StdIn.readLine().trim

    if (choice.forall(_.isDigit)) {
      val index = choice.toInt - 1
      if (index >= 0 && index < savedGames.length) {
        val selectedFile = savedGames(index)
        println(s"✅ Game '$selectedFile' selected.")
        return s"games/$selectedFile"
      }
    }

    println("❌ Invalid selection. Returning empty filename.")
    ""
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
  def promptShowAttackersField(): Unit
  def promptShowAttackersHand(): Unit
  def promptShowDefendersField(): Unit
  def promptShowDefendersHand(): Unit
  def printGameState(): Unit
  def promptExit(): Unit
  def promptMainMenu(): Unit
  def promptCreatePlayers(): Unit
  def promptLoadGame(factory: ITuiCommandFactory): Unit
  def showAvailableGames(): Unit
  def loadSelectedGame(index: Int, factory: ITuiCommandFactory): Unit
  def promptSaveGame(): Unit
  def promptUserForFile(): String
}
