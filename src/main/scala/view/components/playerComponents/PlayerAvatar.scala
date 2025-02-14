package view.components.playerComponents

import model.playerComponent.Player
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.image.{ImageView}
import scalafx.scene.layout.VBox
import view.utils.ImageUtils // ✅ Import ImageUtils

class PlayerAvatar(
                    player: Player,
                    playerIndex: Int,
                    scaleAvatar: Float,
                    scaleFont: Float,
                    profilePicturePath: String, // ✅ Profile Picture Path
                    onPlayerNameButtonClick: () => Unit
                  ) extends VBox {

  val avatar: ImageView = try {
    ImageUtils.importImageAsView(profilePicturePath, scaleAvatar)
  } catch {
    case e: Exception =>
      println(s"⚠️ [ERROR] Profile image not found: $profilePicturePath (${e.getMessage})")
      ImageUtils.importImageAsView("/view/data/players/default_avatar.png", scaleAvatar) // ✅ Fallback
  }

  val playerNameButton = new Button(player.name) {
    style = s"-fx-font-size: ${scaleFont * 12};"
    onAction = _ => onPlayerNameButtonClick()
  }

  alignment = Pos.TOP_CENTER
  spacing = 5
  children = Seq(avatar, playerNameButton)
}
