package model.playingFiledComponent.factory

import com.google.inject.Singleton
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer
import model.playerComponent.factory.IPlayerFactory
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.manager.base.DataManager

//@Singleton
//class DataManagerFactory extends IDataManagerFactory {
//  override def createDataManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IDataManager = {
//    new DataManager(playingField, player1, player2)
//  }
//}
//trait IDataManagerFactory {
//  def createDataManager(playingField: IPlayingField, player1:IPlayer, player2:IPlayer): IDataManager
//}
import com.google.inject.{Inject, Singleton}
import model.playingFiledComponent.manager._
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer
import model.playingFiledComponent.manager.base._
import com.google.inject.{Inject, Singleton}
import model.playingFiledComponent.manager._
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer

@Singleton
class DataManagerFactory @Inject() (
                                     handManager: IPlayerHandManager,
                                     fieldManager: IPlayerFieldManager
                                   ) extends IDataManagerFactory {

  override def createDataManager(
                                  playingField: IPlayingField,
                                  player1: IPlayer,
                                  player2: IPlayer
                                ): IDataManager = {
    new DataManager(playingField, player1, player2, handManager, fieldManager)
  }
}


trait IDataManagerFactory {
  def createDataManager(
                         playingField: IPlayingField,
                         player1: IPlayer,
                         player2: IPlayer
                       ): IDataManager
}
