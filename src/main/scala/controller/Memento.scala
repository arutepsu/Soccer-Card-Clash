package controller
import util.Command
import model.playingFiledComponent.PlayingField
import model.playerComponent.Player
import scala.collection.mutable
import model.cardComponent.Card
import controller.Memento
case class Memento(
                      attacker: Player,
                      defender: Player,
                      player1Defenders: List[Card],
                      player2Defenders: List[Card],
                      player1Goalkeeper: Option[Card],
                      player2Goalkeeper: Option[Card],
                      player1Hand: mutable.Queue[Card],
                      player2Hand: mutable.Queue[Card],
                      player1Score: Int,
                      player2Score: Int,
                      boostValues: Map[Int, Int] // ✅ Added boostValues field
                    )

