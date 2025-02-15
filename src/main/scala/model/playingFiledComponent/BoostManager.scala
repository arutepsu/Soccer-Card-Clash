package model.playingFiledComponent
import model.cardComponent.Card
class BoostManager(
                    playingField: PlayingField,
                    roles: PlayerRoles,
                    fieldState: FieldState
                  ) {
  def chooseBoostCardDefender(index: Int): Unit = {
    val attackersDefenderField = fieldState.getDefenders(roles.attacker)
    if (index < 0 || index >= attackersDefenderField.size) {
      println("Invalid defender index for boosting.")
      return
    }

    val originalCard = attackersDefenderField(index)
    println(s"Original Card Before Boosting: $originalCard")

    if (originalCard.wasBoosted) {
      println(s"⚠️ Boost prevented! ${originalCard} has already been boosted once.")
      return
    }

    val boostValue = originalCard.getBoostingPolicies
    originalCard.setAdditionalValue(boostValue)

    println(s"Boosted Defender Card: ${attackersDefenderField(index)} (Boosted by: $boostValue)")
    playingField.notifyObservers()
  }

  def chooseBoostCardGoalkeeper(): Unit = {
    val attackersGoalkeeperOpt = fieldState.getGoalkeeper(roles.attacker)

    attackersGoalkeeperOpt match {
      case Some(goalkeeper) =>
        //        if (attackersGoalkeeper.wasBoosted) { // ✅ Prevent multiple boosts
        //          println(s"⚠️ Boost prevented! ${attackersGoalkeeper} has already been boosted once.")
        //          return
        //        }
        val boostValue = goalkeeper.getBoostingPolicies
        goalkeeper.setAdditionalValue(boostValue)
        fieldState.setGoalkeeperForAttacker(goalkeeper)
        println(s"Boosted Goalkeeper Card: $goalkeeper (Boosted by: $boostValue)")
        playingField.notifyObservers()
      case None =>
        println("⚠️ No goalkeeper available to boost!")
    }
  }

  def revertCard(card: Card): Card = {
    val revertedCard = card.revertAdditionalValue()
    //    // ✅ If the reverted card is in the attacker's or defender's field, update it
    val attackerField = fieldState.getDefenders(playingField.getAttacker)
    val defenderField = fieldState.getDefenders(playingField.getDefender)

    val updatedAttackerField = attackerField.map(c => if (c == card) revertedCard else c)
    val updatedDefenderField = defenderField.map(c => if (c == card) revertedCard else c)

    fieldState.setPlayerDefenders(playingField.getAttacker, updatedAttackerField)
    fieldState.setPlayerDefenders(playingField.getDefender, updatedDefenderField)

    playingField.notifyObservers() // ✅ Notify after updating game state
    revertedCard
  }
}