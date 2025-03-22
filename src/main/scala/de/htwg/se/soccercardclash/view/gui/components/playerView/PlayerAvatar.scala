package de.htwg.se.soccercardclash.view.gui.components.playerView

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import de.htwg.se.soccercardclash.view.gui.utils.ImageUtils

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

  alignment = Pos.TOP_CENTER
  spacing = 5
  children = Seq(avatar)
}
