package view.gui.utils

object Styles {
  val playingFieldCss: String = getClass.getResource("/scenes/Styles.css").toExternalForm
  val mainMenuCss: String = getClass.getResource("/scenes/MainMenuScene.css").toExternalForm
  val createPlayerCss: String = getClass.getResource("/scenes/CreatePlayer.css").toExternalForm
  val playersFieldBarCss: String = getClass.getResource("/components/PlayersFieldBar.css").toExternalForm
  val playersBarCss: String = getClass.getResource("/components/PlayersBar.css").toExternalForm
  val attackerHandSceneCss: String = getClass.getResource("/scenes/AttackerHandScene.css").toExternalForm
  val pauseMenuCss: String = getClass.getResource("/components/menu.css").toExternalForm
}
