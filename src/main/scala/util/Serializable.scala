package util

import play.api.libs.json._
import scala.xml._

trait Serializable {
  def toJson: JsObject
  def toXml: Elem
}
