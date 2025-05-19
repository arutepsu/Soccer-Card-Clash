package de.htwg.se.soccercardclash.view.gui.utils

import scalafx.scene.image.Image
import java.io.File
import scala.collection.mutable
import scala.jdk.CollectionConverters._

object CardImageRegistry {

  private val cardsPath = "/images/data/cards/"
  private val images: mutable.Map[String, Image] = mutable.Map.empty

  private def loadImage(fileName: String): Option[Image] = {
    val stream = getClass.getResourceAsStream(cardsPath + fileName)
    Option(stream).map(new Image(_))
  }

  lazy val fallbackImage: Image =
    loadImage("flippedCard.png").getOrElse {
      throw new RuntimeException("Fallback card not found.")
    }

  lazy val defeatedCardImage: Image =
    loadImage("defeated.png").getOrElse {
      throw new RuntimeException("Defeated card image not found.")
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
    if (!images.contains("flippedCard.png")) {
      images.put("flippedCard.png", fallbackImage)
    }

    if (!images.contains("defeated.png")) {
      images.put("defeated.png", defeatedCardImage) // ensure it's accessible via getImage
    }
  }


  def getImage(fileName: String): Image =
    images.getOrElse(fileName, {
      fallbackImage
    })

  def getImageForCard(cardFileName: String): Image =
    getImage(cardFileName)

  def getDefeatedImage(): Image =
    images.getOrElse("defeated.png", defeatedCardImage)
  def getAllFileNames: Seq[String] = images.keys.toSeq

  def clear(): Unit = {
    images.clear()
  }
}
