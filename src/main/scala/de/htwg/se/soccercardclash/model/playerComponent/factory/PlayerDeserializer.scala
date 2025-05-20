package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import de.htwg.se.soccercardclash.util.Deserializer
import play.api.libs.json.*
import scala.util.Random
import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
import scala.xml.Elem
import de.htwg.se.soccercardclash.model.playerComponent.base.*
import de.htwg.se.soccercardclash.model.playerComponent.ai.*
@Singleton
class PlayerDeserializer @Inject()(
                                    playerFactory: IPlayerFactory,
                                    cardDeserializer: CardDeserializer
                                  ) extends Deserializer[IPlayer] {

  override def fromXml(xml: Elem): IPlayer = {
    Try {
      val name = xml.attribute("name").map(_.text.trim)
        .getOrElse(throw new IllegalArgumentException("Missing player 'name' attribute"))

      val typeAttr = xml.attribute("type").map(_.text.trim)
        .getOrElse(throw new IllegalArgumentException("Missing player 'type' attribute"))

      val strategyAttr = xml.attribute("strategy").map(_.text.trim).getOrElse("")


      val playerType: PlayerType = typeAttr match {
        case "Human" => Human
        case "AI" =>
          createAIStrategy(strategyAttr).map(AI(_))
            .getOrElse(throw new IllegalArgumentException(s"Unknown AI strategy: $strategyAttr"))
        case other =>
          throw new IllegalArgumentException(s"Unknown player type: $other")
      }

      val actionStates = (xml \ "ActionStates" \ "ActionState").map { node =>
        val policyStr = (node \ "@policy").text.trim
        val policy = PlayerActionPolicies.values.find(_.toString == policyStr)
          .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $policyStr"))

        val state = PlayerActionState.fromString(node.text.trim)

        policy -> state
      }.toMap

      val player = playerType match {
        case Human =>
          playerFactory.createPlayer(name)
        case AI(strategy) =>
          playerFactory.createAIPlayer(name, strategy)
      }

      player.setActionStates(actionStates)

    } match {
      case Success(player) => player
      case Failure(exception) =>
        throw new RuntimeException(s"Error parsing Player XML: ${exception.getMessage}", exception)
    }
  }

  override def fromJson(json: JsObject): IPlayer = {
    Try {
      val name = (json \ "name").as[String].trim
      val typeStr = (json \ "type").asOpt[String].getOrElse("Human")
      val strategyStr = (json \ "strategy").asOpt[String].getOrElse("")

      val playerType: PlayerType = typeStr match {
        case "Human" => Human
        case "AI" =>
          createAIStrategy(strategyStr).map(AI(_))
            .getOrElse(throw new IllegalArgumentException(s"Unknown AI strategy: $strategyStr"))
        case other =>
          throw new IllegalArgumentException(s"Unknown player type: $other")
      }

      val actionStates = (json \ "actionStates").asOpt[Map[String, String]].getOrElse(Map()).map {
        case (key, value) =>
          val policy = PlayerActionPolicies.values.find(_.toString == key)
            .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $key"))

          val state = PlayerActionState.fromString(value)
          policy -> state
      }

      val player = playerType match {
        case Human =>
          playerFactory.createPlayer(name)
        case AI(strategy) =>
          playerFactory.createAIPlayer(name, strategy)
      }

      player.setActionStates(actionStates)

    } match {
      case Success(player) => player
      case Failure(exception) =>
        throw new RuntimeException(s"Error parsing Player JSON: ${exception.getMessage}", exception)
    }
  }

  private def createAIStrategy(name: String): Option[IAIStrategy] = name match {
    case "SimpleAIStrategy" => Some(new SimpleAttackAIStrategy)
    case "SmartAIStrategy" => Some(new SmartAttackAIStrategy)
    case "RandomAIStrategy" => Some(new RandomAttackAIStrategy())
    case "MetaAIStrategy" => Some(new MetaAIStrategy(new Random()))
    case _ => None
  }
  
}