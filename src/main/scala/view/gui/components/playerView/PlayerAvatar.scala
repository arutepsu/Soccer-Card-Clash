package view.gui.components.playerView

import model.playerComponent.IPlayer
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import view.gui.utils.ImageUtils

class PlayerAvatar(
                    player: IPlayer,
                    playerIndex: Int,
                    scaleAvatar: Float,
                    scaleFont: Float,
                    profilePicturePath: String, // ✅ Profile Picture Path
                  ) extends VBox {

  val avatar: ImageView = try {
    ImageUtils.importImageAsView(profilePicturePath, scaleAvatar)
  } catch {
    case e: Exception =>
      println(s"⚠️ [ERROR] Profile image not found: $profilePicturePath (${e.getMessage})")
      ImageUtils.importImageAsView("/images/data/players/player2.jpeg", scaleAvatar) // ✅ Fallback
  }

  val playerNameButton = new Button(player.name) {
    style = s"-fx-font-size: ${scaleFont * 12};"
  }

  alignment = Pos.TOP_CENTER
  spacing = 5
  children = Seq(avatar, playerNameButton)
}
