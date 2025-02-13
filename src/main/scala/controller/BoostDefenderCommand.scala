package controller
import util.Command
import model.playingFiledComponent.PlayingField
import scala.collection.mutable
import model.cardComponent.Card
import controller.Memento
//package controller
import util.Command
import model.playingFiledComponent.PlayingField
import controller.Memento

class BoostDefenderCommand(cardIndex: Int, pf: PlayingField) extends Command {
  private var memento: Option[Memento] = None
  private var boostValue: Int = 0 // ✅ Track boost value separately for undo

  override def doStep(): Unit = {
    // Save the current state before making changes
    memento = Some(createMemento())

    // Get the selected card
    val selectedCard = pf.getHand(pf.getAttacker).toList.lift(cardIndex)

    selectedCard.foreach { card =>
      boostValue = card.getBoostingPolicies // ✅ Get and store boost value
      pf.chooseBoostCardDefender(cardIndex)
    }
  }

  override def undoStep(): Unit = {
    // Restore the previous state
    memento.foreach(restoreMemento)
  }

  override def redoStep(): Unit = {
    doStep() // Reapply the boost effect
  }

  private def createMemento(): Memento = {
    val boostValues = pf.playerDefenders(pf.getAttacker).zipWithIndex.collect {
      case (card, index) if card.additionalValue > 0 => index -> (card.additionalValue, card.lastBoostValue, card.wasBoosted)
    }.toMap

    val goalkeeperBoost = pf.getGoalkeeper(pf.getAttacker).map { gk =>
      (gk.additionalValue, gk.lastBoostValue, gk.wasBoosted)
    }

    Memento(
      attacker = pf.getAttacker,
      defender = pf.getDefender,
      player1Defenders = pf.playerDefenders(pf.getAttacker).map(_.copy()), // ✅ Ensure deep copy
      player2Defenders = pf.playerDefenders(pf.getDefender).map(_.copy()), // ✅ Ensure deep copy
      player1Goalkeeper = pf.getGoalkeeper(pf.getAttacker),
      player2Goalkeeper = pf.getGoalkeeper(pf.getDefender),
      player1Hand = pf.getHand(pf.getAttacker).toList,
      player2Hand = pf.getHand(pf.getDefender).toList,
      player1Score = pf.getScorePlayer1,
      player2Score = pf.getScorePlayer2,
      boostValues = boostValues,
      goalkeeperBoost = goalkeeperBoost
    )
  }


  private def restoreMemento(memento: Memento): Unit = {
    pf.setRoles(memento.attacker, memento.defender)

    // ✅ Restore player-specific states (INCLUDING ATTACKER'S FIELD)
    val restoredAttackerField = memento.player1Defenders.map(_.copy()) // ✅ Deep copy first
    val restoredDefenderField = memento.player2Defenders.map(_.copy())

    // ✅ Restore player hands (using deep copies to avoid references)
    val attackerHand = pf.getHand(memento.attacker)
    attackerHand.clear()
    attackerHand.enqueueAll(memento.player1Hand.map(_.copy()))

    val defenderHand = pf.getHand(memento.defender)
    defenderHand.clear()
    defenderHand.enqueueAll(memento.player2Hand.map(_.copy()))

    // ✅ Restore scores
    pf.setScorePlayer1(memento.player1Score)
    pf.setScorePlayer2(memento.player2Score)

    // ✅ Apply stored boost values to ATTACKER’S FIELD (using only attacker boost values)
    memento.boostValues.filter(_._1 >= 0).foreach { case (index, (additional, lastBoost, wasBoosted)) =>
      restoredAttackerField.lift(index).foreach { card =>
        println(s"Restoring boost for attacker's field card: $card")
        card.additionalValue = additional
        card.lastBoostValue = lastBoost
        card.wasBoosted = wasBoosted
        card.updateValue() // ✅ Ensure value is updated
      }
    }

    // ✅ Apply stored boost values to DEFENDER’S FIELD (using only defender boost values)
    memento.boostValues.filter(_._1 >= 0).foreach { case (index, (additional, lastBoost, wasBoosted)) =>
      restoredDefenderField.lift(index).foreach { card =>
        println(s"Restoring boost for defender's field card: $card")
        card.additionalValue = additional
        card.lastBoostValue = lastBoost
        card.wasBoosted = wasBoosted
        card.updateValue() // ✅ Ensure value is updated
      }
    }

    // ✅ Set fields back in `PlayingField`
    pf.setPlayerDefenders(memento.attacker, restoredAttackerField)
    pf.setPlayerDefenders(memento.defender, restoredDefenderField)

    // ✅ Restore boosted ATTACKER’S GOALKEEPER if applicable
    memento.goalkeeperBoost.foreach { case (additional, lastBoost, wasBoosted) =>
      pf.getGoalkeeper(memento.attacker).foreach { goalkeeper =>
        println(s"Restoring boost for attacker's goalkeeper: ${goalkeeper}")
        goalkeeper.additionalValue = additional
        goalkeeper.lastBoostValue = lastBoost
        goalkeeper.wasBoosted = wasBoosted
        goalkeeper.updateValue() // ✅ Ensure value updates correctly
        pf.setPlayerGoalkeeper(memento.attacker, Some(goalkeeper))
      }
    }

    // ✅ Restore boosted DEFENDER’S GOALKEEPER if applicable
    memento.goalkeeperBoost.foreach { case (additional, lastBoost, wasBoosted) =>
      pf.getGoalkeeper(memento.defender).foreach { goalkeeper =>
        println(s"Restoring boost for defender's goalkeeper: ${goalkeeper}")
        goalkeeper.additionalValue = additional
        goalkeeper.lastBoostValue = lastBoost
        goalkeeper.wasBoosted = wasBoosted
        goalkeeper.updateValue() // ✅ Ensure value updates correctly
        pf.setPlayerGoalkeeper(memento.defender, Some(goalkeeper))
      }
    }

    // ✅ Notify observers of the restored state
    pf.notifyObservers()
  }

}