package controller.command.memento

import controller.command.ICommand
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.playerAction.PlayerActionPolicies

import scala.collection.mutable
case class Memento(
                    attacker: IPlayer,
                    defender: IPlayer,
                    player1Defenders: List[ICard],
                    player2Defenders: List[ICard],
                    player1Goalkeeper: Option[ICard],
                    player2Goalkeeper: Option[ICard],
                    player1Hand: List[ICard],
                    player2Hand: List[ICard],
                    player1Score: Int,
                    player2Score: Int,
                    player1Actions: Map[PlayerActionPolicies, Int],
                    player2Actions: Map[PlayerActionPolicies, Int]
                  )
