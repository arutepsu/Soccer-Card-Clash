package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IDataManager
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
case class PlayingFieldViewContext(
                                    state: IGameState,
                                    player1: IPlayer,
                                    player2: IPlayer,
                                    attacker: IPlayer,
                                    defender: IPlayer,
                                    dataManager: IDataManager,
                                    score1: Int,
                                    score2: Int,
                                    hasGoalkeeper1: Boolean
                                  )
