package de.htwg.se.soccercardclash.view.gui.components.playerView

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import de.htwg.se.soccercardclash.view.gui.utils.BoostImage
import scala.util.{Try, Success, Failure}
import scalafx.scene.image.Image
import scala.collection.mutable
import de.htwg.se.soccercardclash.model.playerComponent.base.*
object PlayerAvatarRegistry {

  val avatarsPath = "/images/data/players/"
  private val images: mutable.Map[String, Image] = mutable.Map.empty
  private val playerImageMap: mutable.Map[IPlayer, Image] = mutable.Map.empty
  def getImages: Map[String, Image] = images.toMap
  def preloadAvatars(): Unit = {
    val fileNames = Seq("player1.jpg", "player2.jpg", "ai.jpg", "taka.jpg", "defendra.jpg", "bitstrom.jpg", "meta.jpg")

    fileNames.foreach { fileName =>
      val stream = getClass.getResourceAsStream(avatarsPath + fileName)
      if (stream != null) {
        images.put(fileName, new Image(stream))
      } else {
        throw new RuntimeException(s"Avatar image not found: $fileName")
      }
    }
  }

  def assignAvatar(player: IPlayer, fileName: String): Unit = {
    val fullPath = avatarsPath + fileName

    val image = images.getOrElseUpdate(fileName, {
      val stream = getClass.getResourceAsStream(fullPath)
      if (stream == null) throw new RuntimeException(s"Avatar image not found: $fileName")
      new Image(stream)
    })

    playerImageMap.put(player, image)
  }

  def assignAvatarsInOrder(players: Seq[IPlayer]): Unit = {
    var humanCounter = 1

    players.foreach { player =>
      player.playerType match {
        case AI(strategy) =>
          val fileName = strategy.getClass.getSimpleName match {
            case "TakaStrategy" => "taka.jpg"
            case "BitstormStrategy" => "bitstrom.jpg"
            case "DefendraStrategy" => "defendra.jpg"
            case "MetaAIStrategy" => "meta.jpg"
            case _ => "ai.jpg" // fallback
          }
          assignAvatar(player, fileName)
        case Human =>
          val fileName = s"player$humanCounter.jpg"
          assignAvatar(player, fileName)
          humanCounter += 1
      }
    }
  }


  def getAvatarImage(player: IPlayer): Image =
    playerImageMap.getOrElse(player,
      throw new RuntimeException(s"No avatar assigned for player: ${player.name}")
    )

  def getAvatarView(player: IPlayer, scale: Float): ImageView = {
    val image = getAvatarImage(player)
    new ImageView(image) {
      fitWidth = 1500 * scale
      preserveRatio = true
      smooth = true
    }
  }

//  def clear(): Unit = {
//    images.clear()
//    playerImageMap.clear()
//  }
}
