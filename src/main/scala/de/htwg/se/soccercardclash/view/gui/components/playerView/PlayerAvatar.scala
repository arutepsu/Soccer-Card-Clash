package de.htwg.se.soccercardclash.view.gui.components.playerView

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import de.htwg.se.soccercardclash.view.gui.utils.ImageUtils
import scala.util.{Try, Success, Failure}

class PlayerAvatar(
                    player: IPlayer,
                    playerIndex: Int,
                    scaleAvatar: Float,
                    scaleFont: Float,
                    profilePicturePath: String,
                  ) extends VBox {

  val avatar: ImageView = Try {
    ImageUtils.importImageAsView(profilePicturePath, scaleAvatar)
  }.recover { case e: Exception =>
    ImageUtils.importImageAsView("/images/data/players/player2.jpg", scaleAvatar)
  }.get

  alignment = Pos.TOP_CENTER
  spacing = 5
  children = Seq(avatar)
}
