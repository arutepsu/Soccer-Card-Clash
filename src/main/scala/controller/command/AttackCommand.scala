package controller.command
import scala.collection.mutable
import model.playingFiledComponent.PlayingField
import util.Command


class AttackCommand(defenderIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  private var attackSuccessful: Boolean = false // Tracks if the attack was successful
  override protected def executeAction(): Unit = {
    attackSuccessful = pf.attack(defenderIndex) // ✅ Performs the attack action
  }
}
