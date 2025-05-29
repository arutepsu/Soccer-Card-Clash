package de.htwg.se.soccercardclash.model.gameComponent.context

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.util.UndoManager

case class GameContext(
                        state: IGameState,
                        undoManager: UndoManager
                      ) 