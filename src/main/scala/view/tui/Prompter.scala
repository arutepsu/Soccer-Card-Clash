package view.tui

import controller.IController

class Prompter(controller: IController) {
  
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
    val playingField = controller.getPlayingField
    val attacker = playingField.getAttacker

    println("\n===================================")
    println(f"${attacker.name} field cards after boost: ")
    println(f"${playingField.getDataManager.getPlayerField(attacker)}")
    println("===================================")
  }
  
  def promptShowAttackersHand(): Unit = {
    val playingField = controller.getPlayingField
    val attacker = playingField.getAttacker

    println("\n===================================")
    println(f"${attacker.name} field cards after swap: ")
    println(f"${playingField.getDataManager.getPlayerHand(attacker)}")
    println("===================================")
  }
  
  def promptShowDefendersField() : Unit = {
    val playingField = controller.getPlayingField
    val defender = playingField.getDefender

    println("\n===================================")
    println(f"${defender.name} field cards after attack: ")
    println(f"${playingField.getDataManager.getPlayerField(defender)}")
    println("===================================")
  }

  def promptShowDefendersHand(): Unit = {
    val playingField = controller.getPlayingField
    val defender = playingField.getDefender

    println("\n===================================")
    println(f"${defender.name} field cards after switch: ")
    println(f"${playingField.getDataManager.getPlayerHand(defender)}")
    println("===================================")
  }
  
  
  def printGameState(): Unit = {
    val playingField = controller.getPlayingField
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
  
  def promptLoadGame(): Unit = {
    println("✅ Game loaded successfully!")
  }

  def promptSaveGame(): Unit = {
    println("✅ Game saved successfully!")
  }
}
