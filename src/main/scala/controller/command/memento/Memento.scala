package controller.command.memento

import controller.command.ICommand
import model.cardComponent.base.Card
import model.playerComponent.IPlayer
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.PlayingField

import scala.collection.mutable
case class Memento(
                    attacker: IPlayer,
                    defender: IPlayer,
                    player1Defenders: List[Card],
                    player2Defenders: List[Card],
                    player1Goalkeeper: Option[Card],
                    player2Goalkeeper: Option[Card],
                    player1Hand: List[Card],
                    player2Hand: List[Card],
                    player1Score: Int,
                    player2Score: Int,
                    player1Actions: Map[PlayerActionPolicies, Int],
                    player2Actions: Map[PlayerActionPolicies, Int]
                  )
