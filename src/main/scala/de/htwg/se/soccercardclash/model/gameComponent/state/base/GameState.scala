package de.htwg.se.soccercardclash.model.gameComponent.state.base

import com.google.inject.{Inject, Provider, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.{AttackManager, IAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.util.Observable
import play.api.libs.json.*

import scala.xml.*

case class GameState(
                      dataManager: IDataManager,
                      roles: IRoles,
                      scores: IScores
                    ) extends IGameState {

  override def getPlayer1: IPlayer = roles.attacker

  override def getPlayer2: IPlayer = roles.defender

  override def getDataManager: IDataManager = dataManager

  override def getRoles: IRoles = roles

  override def getScores: IScores = scores

  override def withDataManager(newDataManager: IDataManager): IGameState =
    copy(dataManager = newDataManager)

  override def withRolesManager(newRoles: IRoles): IGameState =
    copy(roles = newRoles)

  override def withScores(newScores: IScores): IGameState =
    copy(scores = newScores)

}

