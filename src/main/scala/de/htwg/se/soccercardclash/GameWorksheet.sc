import com.google.inject.Guice
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.module.SoccerCardClashModule

val injector = Guice.createInjector(new SoccerCardClashModule())
val game = injector.getInstance(classOf[IGame])
game.createGame("Player1", "Player2")

val field = game.getPlayingField
val dataManager = field.getDataManager
val attackManager = field.getActionManager
val playerActionService = attackManager.getPlayerActionService

val attacker = field.getAttacker
val defender = field.getDefender

def printAttackersDefenderField(phase: String): Unit = {
  val defenders = dataManager.getPlayerDefenders(attacker).map(_.toString)
  val goalkeeper = Option(dataManager.getPlayerGoalkeeper(attacker)).map(_.toString).getOrElse("None")
  println(s"\n[$phase] ${attacker.name}'s Defender Field:")
  println(s"Defenders: ${defenders.mkString(", ")}")
  println(s"Goalkeeper: $goalkeeper")
}

def printAttackersHand(phase: String): Unit = {
  val handCards = dataManager.getPlayerHand(attacker).getCards.map(_.toString)
  println(s"\n[$phase] ${attacker.name}'s Hand:")
  println(s"Hand Cards: ${handCards.mkString(", ")}")
}

def printDefendersField(phase: String): Unit = {
  val defenders = dataManager.getPlayerDefenders(defender).map(_.toString)
  val goalkeeper = Option(dataManager.getPlayerGoalkeeper(defender)).map(_.toString).getOrElse("None")
  println(s"\n[$phase] ${defender.name}'s Defender Field:")
  println(s"Defenders: ${defenders.mkString(", ")}")
  println(s"Goalkeeper: $goalkeeper")
}

val boostIndex = 1

println(s"\nAttacker: ${attacker.name}")
println(s"Defender: ${defender.name}")

printAttackersDefenderField("Before boost")
println(s"\n${attacker.name} boosts defender card at index $boostIndex")
attackManager.boostDefender(boostIndex, playerActionService)
printAttackersDefenderField("After boost")

printAttackersHand("Before reverse swap")
println(s"\n${attacker.name} performs a reverse swap")
attackManager.reverseSwap(playerActionService)
printAttackersHand("After reverse swap")

val attackIndex = 0

printAttackersHand("Before attack")
printDefendersField("Before attack")

println(s"\n${attacker.name} attacks with card at index $attackIndex")
attackManager.singleAttack(attackIndex)

printAttackersHand("After attack")
printDefendersField("After attack")
