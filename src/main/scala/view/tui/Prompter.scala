package view.tui

class Prompter {

  /** 🏆 Ask for Player Name */
  def promptPlayerName(): Unit = {
    println("📝 Enter player name:")
  }

  /** 🎯 Ask for Attack */
  def promptAttack(): Unit = {
    println("⚔️ Select a defender to attack (enter position):")
  }

  /** ⚡ Ask for Boost */
  def promptBoost(): Unit = {
    println("🔥 Choose a defender to boost (enter position):")
  }

  /** 🔄 Ask for Swap */
  def promptSwap(): Unit = {
    println("🔄 Choose a card to swap from attacker's hand (enter position):")
  }
  def promptNewGame(): Unit = {
    println("Creating a new game!")
  }
}
