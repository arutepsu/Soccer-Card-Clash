package view.components.tui


import controller.Controller

object tui {

  def displayGameState(controller: Controller): String = {
    val pf = controller.getPlayingField
    val attacker = pf.getAttacker
    val defender = pf.getDefender
    val attackingCard = pf.getAttackingCard
    val attackerHand = pf.getHand(attacker).mkString(", ")
    val defenderHand = pf.getHand(defender).mkString(", ")
    val defenderField = pf.playerDefenders(defender).mkString(", ")

    val defenderGoalkeeper = pf.getGoalkeeper(defender)
    val attackerHandCount = pf.getHand(attacker).size
    val defenderHandCount = pf.getHand(defender).size

    s"""Attacker: ${attacker.name} (Cards in hand: $attackerHandCount)
       |Attacker's Hand: $attackerHand
       |Defender's Hand: $defenderHand
       |Attacking Card: $attackingCard
       |
       |Defender: ${defender.name} (Cards in hand: $defenderHandCount)
       |Defender's Field: $defenderField
       |Defender's Goalkeeper: $defenderGoalkeeper
       |""".stripMargin
  }
}