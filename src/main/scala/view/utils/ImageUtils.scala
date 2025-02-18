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
    val resource = getClass.getResourceAsStream(imageUrl)
    if (resource == null) {
      throw new IllegalArgumentException(s"⚠️ Image not found: $imageUrl. Check that it exists in src/main/resources/")
    }
    new Image(resource)
  }

  /**
   * Loads an image and returns it as a properly scaled ImageView for boost effects.
   */
  def importImageAsViewSize(imageUrl: String, scaleFactor: Float, maxW: Double, maxH: Double): ImageView = {
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
  private def importImageBoost(imageUrl: String): Image = {
    val resource = getClass.getResourceAsStream(imageUrl)
    if (resource == null) {
      throw new IllegalArgumentException(s"⚠️ Boost image not found: $imageUrl. Check that it exists in src/main/resources/")
    }
    new Image(resource)
  }
}
