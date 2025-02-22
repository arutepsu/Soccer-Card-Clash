package model.playingFiledComponent.base
import com.google.inject.Singleton
import com.google.inject.Inject
import model.cardComponent.factory.DeckFactory
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.factory.{IActionManagerFactory, IDataManagerFactory, IPlayerScoresFactory}
import model.playingFiledComponent.manager.*
import model.playerComponent.playerRole.*
import model.playerComponent.base.factories.IPlayerFactory
import model.playingFiledComponent.strategy.scoringStrategy.*
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, IAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.swapStrategy.*
import util.Observable
import play.api.libs.json.*
import scala.compiletime.summonInline
import com.google.inject.Inject
import com.google.inject.Inject
import com.google.inject.{Inject, Provider}
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores

class PlayingField @Inject()(
                    val player1: IPlayer,
                    val player2: IPlayer
                  )(using manager: IPlayingFieldManager) extends IPlayingField {

  private val dataManager: IDataManager = summon[IPlayingFieldManager].createDataManager(this, player1, player2)
  private val actionManager: IActionManager = summon[IPlayingFieldManager].createActionManager(this)
  private val roles: IRolesManager = summon[IPlayingFieldManager].createRolesManager(this, player1, player2)
  private val scores: IPlayerScores = summon[IPlayingFieldManager].createScoresManager(this, player1, player2)

  override def setPlayingField(): Unit = {
    dataManager.initializeFields()
  }

  override def getAttacker: IPlayer = roles.attacker

  override def getDefender: IPlayer = roles.defender

  override def getDataManager: IDataManager = dataManager

  override def getActionManager: IActionManager = actionManager

  override def getRoles: IRolesManager = roles

  override def getScores: IPlayerScores = scores
}
