package de.htwg.se.soccercardclash.util

import play.api.libs.json._
import scala.xml._

trait Serializable {
  def toJson: JsObject
  def toXml: Elem
}
trait Deserializer[T] {
  def fromXml(xml: Elem): T
  def fromJson(json: JsObject): T
}
