package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import com.google.inject.Singleton

case class GameStartupData(
                            var humanPlayerName: Option[String] = None
                            //difficulty and etc.
                          )
@Singleton
class GameStartupDataHolder {
  val data: GameStartupData = GameStartupData()
}
