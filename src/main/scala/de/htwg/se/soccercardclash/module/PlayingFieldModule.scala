package de.htwg.se.soccercardclash.module

import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.base.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import com.google.inject.AbstractModule
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.base.PlayingField

  class PlayingFieldModule extends AbstractModule {
    
  override def configure(): Unit = {
    bind(classOf[IPlayingFieldFactory]).to(classOf[PlayingFieldFactory])
    bind(classOf[IPlayingFieldManagerFactory]).to(classOf[PlayingFieldManagerFactory])
    bind(classOf[IPlayingField]).to(classOf[PlayingField])

    bind(classOf[IDataManager]).to(classOf[DataManager])
    bind(classOf[IActionManager]).to(classOf[ActionManager])
    bind(classOf[IRolesManager]).to(classOf[RolesManager])
    bind(classOf[IPlayerScores]).to(classOf[PlayerScores])
    bind(classOf[IScoringStrategy]).to(classOf[StandardScoring])
    bind(classOf[IPlayerActionManager]).to(classOf[PlayerActionManager])

    bind(classOf[PlayingFieldDeserializer])
      .toConstructor(classOf[PlayingFieldDeserializer]
        .getConstructor(classOf[IPlayingFieldFactory], classOf[PlayerDeserializer]))
  }
}
