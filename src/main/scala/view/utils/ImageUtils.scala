package view.utils
import scalafx.scene.image.{Image, ImageView}

object ImageUtils {

  def importImageAsView(imageUrl: String, scaleFactor: Float): ImageView = {
    val image = importImage(imageUrl)
    val imageView = new ImageView(image)
    imageView.fitWidth = image.width.value * scaleFactor
    imageView.fitHeight = image.height.value * scaleFactor
    imageView
  }

  def importImage(imageUrl: String): Image = {
    val image = new Image(getClass.getResourceAsStream(imageUrl))
    image
  }

  /**
   * Loads an image and returns it as a properly scaled ImageView for boost effects.
   */
  def importImageAsViewBoost(imageUrl: String, scaleFactor: Float, maxW: Double, maxH: Double): ImageView = {
    val loadedImage = importImageBoost(imageUrl) // Load the image

    // Create an ImageView and scale it while maintaining the aspect ratio
    new ImageView(loadedImage) {
      preserveRatio = true
      fitWidth = Math.min(loadedImage.width.value * scaleFactor.toDouble, maxW) // Ensure type consistency
      fitHeight = Math.min(loadedImage.height.value * scaleFactor.toDouble, maxH)
    }
  }

  /**
   * Loads an image from resources specifically for boost effects.
   */
  def importImageBoost(imageUrl: String): Image = {
    new Image(getClass.getResourceAsStream(imageUrl))
  }
}