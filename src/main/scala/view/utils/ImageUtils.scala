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
}