package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, IScores}

trait Memento

case class GameStateMemento(
                             gameCards: IGameCards,
                             roles: IRoles,
                             scores: IScores
                           ) extends Memento
