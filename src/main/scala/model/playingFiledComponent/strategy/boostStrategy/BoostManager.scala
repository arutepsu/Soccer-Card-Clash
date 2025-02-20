package model.playingFiledComponent.strategy.boostStrategy

import model.cardComponent.base.{BoostedCard, RegularCard}
import model.cardComponent.ICard
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.base.{DataManager, PlayingField}

class BoostManager(
                    playingField: PlayingField,
                    roles: RolesManager,
                    fieldState: DataManager
                  ) {

  def chooseBoostCardDefender(index: Int): Unit = {
    var attackersDefenderField = fieldState.getPlayerDefenders(roles.attacker)

    if (index < 0 || index >= attackersDefenderField.size) {
      return
    }

    val originalCard = attackersDefenderField(index)
    
    val boostedCard = originalCard.boost()

    attackersDefenderField = attackersDefenderField.updated(index, boostedCard)
    
    
    fieldState.setPlayerDefenders(roles.attacker, attackersDefenderField)

    roles.attacker.performAction(PlayerActionPolicies.Boost)
    playingField.notifyObservers()
  }
  
  def chooseBoostCardGoalkeeper(): Unit = {
    val attackersGoalkeeperOpt = fieldState.getPlayerGoalkeeper(roles.attacker)

    attackersGoalkeeperOpt match {
      case Some(goalkeeper) =>
        goalkeeper.boost()
        fieldState.setGoalkeeperForAttacker(goalkeeper)
        roles.attacker.performAction(PlayerActionPolicies.Boost)
        playingField.notifyObservers()
      case None =>
    }
  }
  
  def revertCard(card: ICard): ICard = {
    val revertedCard = card match {
      case boosted: BoostedCard =>
        boosted.revertBoost()

      case regular =>
        regular
    }
    val attackerField = fieldState.getPlayerDefenders(playingField.getAttacker)
    val defenderField = fieldState.getPlayerDefenders(playingField.getDefender)

    val updatedAttackerField = attackerField.map(c => if (c == card) revertedCard else c)
    val updatedDefenderField = defenderField.map(c => if (c == card) revertedCard else c)

    fieldState.setPlayerDefenders(playingField.getAttacker, updatedAttackerField)
    fieldState.setPlayerDefenders(playingField.getDefender, updatedDefenderField)
    
    playingField.notifyObservers()

    revertedCard
  }

}
