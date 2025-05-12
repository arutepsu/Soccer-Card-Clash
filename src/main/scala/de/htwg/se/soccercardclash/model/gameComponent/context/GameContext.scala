package de.htwg.se.soccercardclash.model.gameComponent.context

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.util.UndoManager

case class GameContext(
                        state: IGameState,
                        undoManager: UndoManager
                      ) 