package module
import model.playingFiledComponent.factory.*
import model.playingFiledComponent.manager.*
import model.playingFiledComponent.strategy.scoringStrategy.*
import model.playingFiledComponent.strategy.scoringStrategy.base.*
import model.playerComponent.factory.PlayerDeserializer
import com.google.inject.AbstractModule
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField

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

    bind(classOf[PlayingFieldDeserializer])
      .toConstructor(classOf[PlayingFieldDeserializer]
        .getConstructor(classOf[IPlayingFieldFactory], classOf[PlayerDeserializer]))
  }
}
