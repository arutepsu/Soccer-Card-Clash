package controller.command.base

import model.cardComponent.base.Card
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import util.ICommand
import model.playerComponent.PlayerAction.PlayerAction


import scala.collection.mutable
case class Memento(
                    attacker: Player,
                    defender: Player,
                    player1Defenders: List[Card],
                    player2Defenders: List[Card],
                    player1Goalkeeper: Option[Card],
                    player2Goalkeeper: Option[Card],
                    player1Hand: List[Card],
                    player2Hand: List[Card],
                    player1Score: Int,
                    player2Score: Int,
                    boostValues: Map[Int, (Int, Int, Boolean)],
                    goalkeeperBoost: Option[(Int, Int, Boolean)],
                    player1Actions: Map[PlayerAction, Int],
                    player2Actions: Map[PlayerAction, Int]
                  )
