package de.htwg.se.soccercardclash.view.gui.utils

import scalafx.scene.image.Image
import java.io.File
import scala.collection.mutable
import scala.jdk.CollectionConverters._

object CardImageRegistry {

  private val cardsPath = "/images/data/cards/"
  private val images: mutable.Map[String, Image] = mutable.Map.empty

  lazy val fallbackImage: Image = {
    val stream = getClass.getResourceAsStream(cardsPath + "card_black.png")
    if (stream == null) throw new RuntimeException("Fallback card_black.png not found.")
    new Image(stream)
  }

  def preloadAll(): Unit = {
    val suits = Seq("hearts", "diamonds", "clubs", "spades")
    val values = Seq("2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace")

    val allFileNames = for {
      suit <- suits
      value <- values
    } yield s"${value}_of_${suit}.png"

    val allFiles = allFileNames :+ "card_black.png"

    allFiles.foreach { fileName =>
      val stream = getClass.getResourceAsStream(cardsPath + fileName)
      if (stream != null) {
        val image = new Image(stream)
        images.put(fileName, image)
      } else {
      }
    }

    if (!images.contains("card_black.png")) {
      images.put("card_black.png", fallbackImage)
    }
  }
  
  def getImage(fileName: String): Image =
    images.getOrElse(fileName, {
      fallbackImage
    })

  def getImageForCard(cardFileName: String): Image =
    getImage(cardFileName)

  def getAllFileNames: Seq[String] = images.keys.toSeq

  def clear(): Unit = {
    images.clear()
  }
}
