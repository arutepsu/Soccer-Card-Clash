package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.*
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.ai.types.{BitstormStrategy, DefendraStrategy, MetaAIStrategy, TakaStrategy}
import de.htwg.se.soccercardclash.model.playerComponent.base.*
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies, PlayerActionState}
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.Deserializer
import play.api.libs.json.*

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
import scala.xml.Elem
@Singleton
class PlayerDeserializer @Inject()(
                                    cardDeserializer: CardDeserializer,
                                    randoms: Map[String, IRandomProvider]
                                  ) extends Deserializer[IPlayer] {

  override def fromXml(xml: Elem): IPlayer = {
    Try {
      val name = xml.attribute("name").map(_.text.trim)
        .getOrElse(throw new IllegalArgumentException("Missing player 'name' attribute"))

      val typeAttr = xml.attribute("type").map(_.text.trim)
        .getOrElse(throw new IllegalArgumentException("Missing player 'type' attribute"))

      val strategyAttr = xml.attribute("strategy").map(_.text.trim).getOrElse("")

      val actionStates: Map[PlayerActionPolicies, PlayerActionState] =
        (xml \ "ActionStates" \ "ActionState").map { node =>
          val policyStr = (node \ "@policy").text.trim
          val policy = PlayerActionPolicies.values.find(_.toString == policyStr)
            .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $policyStr"))

          val state = PlayerActionState.fromString(node.text.trim)
          policy -> state
        }.toMap

      typeAttr match {
        case "Human" =>
          PlayerBuilder()
          .withName(name)
          .asHuman()
          .withActionStates(actionStates)
          .build()

        case "AI" =>
          val strategy = createAIStrategy(strategyAttr)
            .getOrElse(throw new IllegalArgumentException(s"Unsupported AI strategy: $strategyAttr"))

          PlayerBuilder()
            .withName(name)
            .asAI(strategy)
            .withConvertedLimitsFromStates(actionStates)
            .build()

        case other =>
          throw new IllegalArgumentException(s"Unknown player type: $other")
      }

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

      val actionStates: Map[PlayerActionPolicies, PlayerActionState] =
        (json \ "actionStates").asOpt[Map[String, String]].getOrElse(Map()).map {
          case (key, value) =>
            val policy = PlayerActionPolicies.values.find(_.toString == key)
              .getOrElse(throw new IllegalArgumentException(s"Unknown policy: $key"))

            val state = PlayerActionState.fromString(value)
            policy -> state
        }

      typeStr match {
        case "Human" =>
          
          PlayerBuilder()
            .withName(name)
            .asHuman()
            .withActionStates(actionStates)
            .build()


        case "AI" =>
          val strategy = createAIStrategy(strategyStr)
            .getOrElse(throw new IllegalArgumentException(s"Unsupported AI strategy: $strategyStr"))
          
          PlayerBuilder()
            .withName(name)
            .asAI(strategy)
            .withConvertedLimitsFromStates(actionStates)
            .build()


        case other =>
          throw new IllegalArgumentException(s"Unknown player type: $other")
      }

    } match {
      case Success(player) => player
      case Failure(exception) =>
        throw new RuntimeException(s"Error parsing Player JSON: ${exception.getMessage}", exception)
    }
  }

  private def createAIStrategy(name: String): Option[IAIStrategy] =
    name match {
      case "TakaStrategy" => Some(new TakaStrategy(randoms("Taka")))
      case "BitstormStrategy" => Some(new BitstormStrategy(randoms("Bitstorm")))
      case "DefendraStrategy" => Some(new DefendraStrategy(randoms("Defendra")))
      case "MetaAIStrategy" => Some(new MetaAIStrategy(randoms("MetaAI")))
      case _ => None
    }


}