package de.htwg.se.soccercardclash.view.gui.utils

import de.htwg.se.soccercardclash.view.gui.utils.Styles.getClass

object Styles {
  val generalCss: String = getClass.getResource("/scenes/Styles.css").toExternalForm
  val mainMenuCss: String = getClass.getResource("/scenes/MainMenuScene.css").toExternalForm
  val createPlayerCss: String = getClass.getResource("/scenes/CreatePlayer.css").toExternalForm
  val createPlayerWitAICss: String = getClass.getResource("/scenes/CreatePlayerWithAI.css").toExternalForm
  val playersFieldBarCss: String = getClass.getResource("/components/PlayersFieldBar.css").toExternalForm
  val playersBarCss: String = getClass.getResource("/components/PlayersBar.css").toExternalForm
  val attackerBarCss: String = getClass.getResource("/components/AttackerBar.css").toExternalForm
  val attackerHandSceneCss: String = getClass.getResource("/scenes/AttackerHandScene.css").toExternalForm
  val attackerFieldSceneCss: String = getClass.getResource("/scenes/AttackerFieldScene.css").toExternalForm
  val pauseMenuCss: String = getClass.getResource("/components/menu.css").toExternalForm
  val comparisonCSS: String = getClass.getResource("/components/Comparison.css").toExternalForm
  val loadGameCss: String = getClass.getResource("/scenes/LoadGameScene.css").toExternalForm
  val infoDialogCss: String = getClass.getResource("/scenes/InfoDialog.css").toExternalForm
  val aiSelection: String = getClass.getResource("/scenes/AISelectionScene.css").toExternalForm
}  
