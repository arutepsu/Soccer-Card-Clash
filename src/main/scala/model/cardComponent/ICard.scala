package model.cardComponent
import model.cardComponent.base.Suit.Suit
import model.cardComponent.base.Value.Value
import util.Serializable
import play.api.libs.json._
import scala.xml._

trait ICard extends Serializable {
  def value: Value
  def suit: Suit
  def boost(): ICard
  def revertBoost(): ICard
  def valueToInt: Int
  def compare(that: ICard): Int
  def fileName: String
  def copy(): ICard
  def hashCode(): Int
  def equals(obj: Any): Boolean

  override def toJson: JsObject = Json.obj(
    "value" -> value.toString,
    "suit" -> suit.toString
  )

  override def toXml: Elem =
    <Card>
      <Value>{value.toString}</Value>
      <Suit>{suit.toString}</Suit>
    </Card>
}
