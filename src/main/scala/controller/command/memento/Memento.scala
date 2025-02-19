package controller.command.memento

import controller.command.ICommand
import model.cardComponent.base.Card
import model.playerComponent.Player
import model.playerComponent.PlayerAction.PlayerAction
import model.playingFiledComponent.PlayingField

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
                    player1Actions: Map[PlayerAction, Int],
                    player2Actions: Map[PlayerAction, Int]
                  )
