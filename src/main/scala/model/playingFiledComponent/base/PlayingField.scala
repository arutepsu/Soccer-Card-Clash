package model.playingFiledComponent.base
import com.google.inject.Singleton
import com.google.inject.Inject
import model.cardComponent.factory.DeckFactory
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.*
import model.playerComponent.playerRole.*
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.strategy.scoringStrategy.*
import model.playingFiledComponent.strategy.attackStrategy.{AttackHandler, IAttackStrategy}
import model.playingFiledComponent.strategy.boostStrategy.*
import model.playingFiledComponent.strategy.swapStrategy.*
import util.Observable
import play.api.libs.json.*
import scala.compiletime.summonInline
import model.playingFiledComponent.factory.IPlayingFieldManagerFactory
import com.google.inject.Inject
import com.google.inject.Inject
import com.google.inject.{Inject, Provider}
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores

class PlayingField @Inject()(
                              private var player1: IPlayer,
                              private var player2: IPlayer
                  )(using manager: IPlayingFieldManagerFactory) extends IPlayingField {

  private val dataManager: IDataManager = summon[IPlayingFieldManagerFactory].createDataManager(this, player1, player2)
  private val actionManager: IActionManager = summon[IPlayingFieldManagerFactory].createActionManager(this)
  private val roles: IRolesManager = summon[IPlayingFieldManagerFactory].createRolesManager(this, player1, player2)
  private val scores: IPlayerScores = summon[IPlayingFieldManagerFactory].createScoresManager(this, player1, player2)

  override def setPlayingField(): Unit = {
    dataManager.initializeFields()
  }

  override def getAttacker: IPlayer = roles.attacker

  override def getDefender: IPlayer = roles.defender

  override def getDataManager: IDataManager = dataManager

  override def getActionManager: IActionManager = actionManager

  override def getRoles: IRolesManager = roles

  override def getScores: IPlayerScores = scores

  override def reset(): Unit = {
    println("🔄 Resetting Game...")

    // ✅ Reset players to completely new instances or null
    player1 = null.asInstanceOf[IPlayer]
    player2 = null.asInstanceOf[IPlayer]

    println("✅ PlayingField reset completed!")

    // ✅ Reset internal game state
    println("🔄 Resetting PlayingField...")
    dataManager.clearAll()
    actionManager.reset()
    roles.reset()
    scores.reset()

    // ✅ Notify observers so the UI updates immediately
    notifyObservers()

    println("✅ PlayingField reset completed!")
  }

}
