package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.controller.{IGameContextHolder, *}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import scalafx.stage.Stage
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent, Observer, StateEvent}
import de.htwg.se.soccercardclash.view.gui.components.dialog.ComparisonDialogGenerator
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{PlayersFieldBar, PlayersHandBar}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.utils.{ImageUtils, Styles}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//class ComparisonDialogHandler(controller: IController, contextHolder: IGameContextHolder, overlay: Overlay) {
//
//  private var lastAttackingCard: Option[ICard] = None
//  private var lastAttackingCard1: Option[ICard] = None
//  private var lastAttackingCard2: Option[ICard] = None
//  private var lastDefendingCard: Option[ICard] = None
//  private var lastExtraAttackerCard: Option[ICard] = None
//  private var lastExtraDefenderCard: Option[ICard] = None
//  private var lastAttackSuccess: Option[Boolean] = None
//
//  def handleComparisonEvent(e: ObservableEvent): Unit = {
//    e match {
//      case Events.ComparedCardsEvent(attackingCard, defendingCard) =>
//        lastAttackingCard = Some(attackingCard)
//        lastDefendingCard = Some(defendingCard)
//
//      case Events.DoubleComparedCardsEvent(attackingCard1, attackingCard2, defendingCard) =>
//        lastAttackingCard1 = Some(attackingCard1)
//        lastAttackingCard2 = Some(attackingCard2)
//        lastDefendingCard = Some(defendingCard)
//
//      case Events.AttackResultEvent(attacker, defender, attackSuccess) =>
//        lastAttackSuccess = Some(attackSuccess)
//
//      case Events.TieComparisonEvent(attackingCard, defendingCard, extraAttackerCard, extraDefenderCard) =>
//        lastAttackingCard = Some(attackingCard)
//        lastDefendingCard = Some(defendingCard)
//        lastExtraAttackerCard = Some(extraAttackerCard)
//        lastExtraDefenderCard = Some(extraDefenderCard)
//
//      case Events.DoubleTieComparisonEvent(attackingCard1, attackingCard2, defendingCard, extraAttackerCard, extraDefenderCard) =>
//        lastAttackingCard1 = Some(attackingCard1)
//        lastAttackingCard2 = Some(attackingCard2)
//        lastDefendingCard = Some(defendingCard)
//        lastExtraAttackerCard = Some(extraAttackerCard)
//        lastExtraDefenderCard = Some(extraDefenderCard)
//
//        import javafx.application.Platform
//
//      case Events.RegularAttack =>
//        (lastAttackingCard, lastDefendingCard, lastAttackSuccess) match {
//          case (Some(attackingCard), Some(defendingCard), Some(attackSuccess)) =>
//            Platform.runLater(() => {
//              overlay.show(
//                ComparisonDialogGenerator.showSingleComparison(
//                  contextHolder.get.state.getPlayer1,
//                  contextHolder.get.state.getPlayer2,
//                  contextHolder.get.state.getRoles.attacker,
//                  contextHolder.get.state.getRoles.attacker,
//                  attackingCard,
//                  defendingCard,
//                  attackSuccess,
//                  overlay.getPane.getWidth
//                ),
//                true
//              )
//            })
//
//          case _ => // no-op
//        }
//
//        resetLastCards()
//
//
//      case Events.DoubleAttack =>
//
//        (lastAttackingCard1, lastAttackingCard2, lastDefendingCard, lastAttackSuccess) match {
//          case (Some(attackingCard1), Some(attackingCard2), Some(defendingCard), Some(attackSuccess)) =>
//            overlay.show(
//              ComparisonDialogGenerator.showDoubleComparison(
//                contextHolder.get.state.getPlayer1,
//                contextHolder.get.state.getPlayer2,
//                contextHolder.get.state.getRoles.attacker,
//                contextHolder.get.state.getRoles.defender,
//                attackingCard1,
//                attackingCard2,
//                defendingCard,
//                attackSuccess,
//                overlay.getPane.getWidth
//              ), true
//            )
//
//          case _ =>
//        }
//        resetLastCards()
//
//      case Events.TieComparison =>
//
//        (lastAttackingCard, lastDefendingCard, lastExtraAttackerCard, lastExtraDefenderCard) match {
//          case (Some(attackingCard), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
//            overlay.show(
//              ComparisonDialogGenerator.showTieComparison(
//                contextHolder.get.state.getPlayer1,
//                contextHolder.get.state.getPlayer2,
//                contextHolder.get.state.getRoles.attacker,
//                contextHolder.get.state.getRoles.defender,
//                attackingCard, defendingCard, extraAttackerCard, extraDefenderCard, overlay.getPane.getWidth
//              ), true
//            )
//
//          case _ =>
//        }
//        resetLastCards()
//
//      case Events.DoubleTieComparison =>
//
//        (lastAttackingCard1, lastAttackingCard2, lastDefendingCard, lastExtraAttackerCard, lastExtraDefenderCard) match {
//          case (Some(attackingCard1), Some(attackingCard2), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
//            overlay.show(
//              ComparisonDialogGenerator.showDoubleTieComparison(
//                contextHolder.get.state.getPlayer1,
//                contextHolder.get.state.getPlayer2,
//                contextHolder.get.state.getRoles.attacker,
//                contextHolder.get.state.getRoles.defender,
//                attackingCard1, attackingCard2, defendingCard, extraAttackerCard, extraDefenderCard, overlay.getPane.getWidth
//              ), true
//            )
//
//          case _ =>
//        }
//        resetLastCards()
//      case _ =>
//    }
//  }
//
//  private def resetLastCards(): Unit = {
//    lastAttackingCard = None
//    lastAttackingCard1 = None
//    lastAttackingCard2 = None
//    lastDefendingCard = None
//    lastExtraAttackerCard = None
//    lastExtraDefenderCard = None
//    lastAttackSuccess = None
//  }
//
//}
//package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import de.htwg.se.soccercardclash.view.gui.components.dialog.ComparisonDialogGenerator
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{PlayersFieldBar, PlayersHandBar}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.utils.{ImageUtils, Styles}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ComparisonDialogHandler(controller: IController, contextHolder: IGameContextHolder, overlay: Overlay) {

  private var lastAttackingCard: Option[ICard] = None
  private var lastAttackingCard1: Option[ICard] = None
  private var lastAttackingCard2: Option[ICard] = None
  private var lastDefendingCard: Option[ICard] = None
  private var lastExtraAttackerCard: Option[ICard] = None
  private var lastExtraDefenderCard: Option[ICard] = None
  private var lastAttackSuccess: Option[Boolean] = None

  def handleComparisonEvent(e: ObservableEvent): Unit = {
    e match {
      case StateEvent.ComparedCardsEvent(attackingCard, defendingCard) =>
        lastAttackingCard = Some(attackingCard)
        lastDefendingCard = Some(defendingCard)
        println(f"${lastAttackingCard} and ${lastDefendingCard}")

      case StateEvent.DoubleComparedCardsEvent(attackingCard1, attackingCard2, defendingCard) =>
        lastAttackingCard1 = Some(attackingCard1)
        lastAttackingCard2 = Some(attackingCard2)
        lastDefendingCard = Some(defendingCard)

      case StateEvent.AttackResultEvent(attacker, defender, attackSuccess) =>
        lastAttackSuccess = Some(attackSuccess)
        println(f"${lastAttackSuccess}")
      case StateEvent.TieComparisonEvent(attackingCard, defendingCard, extraAttackerCard, extraDefenderCard) =>
        lastAttackingCard = Some(attackingCard)
        lastDefendingCard = Some(defendingCard)
        lastExtraAttackerCard = Some(extraAttackerCard)
        lastExtraDefenderCard = Some(extraDefenderCard)

      case StateEvent.DoubleTieComparisonEvent(attackingCard1, attackingCard2, defendingCard, extraAttackerCard, extraDefenderCard) =>
        lastAttackingCard1 = Some(attackingCard1)
        lastAttackingCard2 = Some(attackingCard2)
        lastDefendingCard = Some(defendingCard)
        lastExtraAttackerCard = Some(extraAttackerCard)
        lastExtraDefenderCard = Some(extraDefenderCard)

        import javafx.application.Platform

      case GameActionEvent.RegularAttack =>
        (lastAttackingCard, lastDefendingCard, lastAttackSuccess) match {
          case (Some(attackingCard), Some(defendingCard), Some(attackSuccess)) =>
            Platform.runLater(() => {
              overlay.show(
                ComparisonDialogGenerator.showSingleComparison(
                  contextHolder.get.state.getPlayer1,
                  contextHolder.get.state.getPlayer2,
                  contextHolder.get.state.getRoles.attacker,
                  contextHolder.get.state.getRoles.defender,
                  attackingCard,
                  defendingCard,
                  attackSuccess,
                  overlay.getPane.getWidth
                ),
                true
              )
            })

          case _ =>
        }

        resetLastCards()


      case GameActionEvent.DoubleAttack =>

        (lastAttackingCard1, lastAttackingCard2, lastDefendingCard, lastAttackSuccess) match {
          case (Some(attackingCard1), Some(attackingCard2), Some(defendingCard), Some(attackSuccess)) =>
            overlay.show(
              ComparisonDialogGenerator.showDoubleComparison(
                contextHolder.get.state.getPlayer1,
                contextHolder.get.state.getPlayer2,
                contextHolder.get.state.getRoles.attacker,
                contextHolder.get.state.getRoles.defender, attackingCard1, attackingCard2, defendingCard, attackSuccess, overlay.getPane.getWidth
              ), true
            )

          case _ =>
        }
        resetLastCards()

      case GameActionEvent.TieComparison =>

        (lastAttackingCard, lastDefendingCard, lastExtraAttackerCard, lastExtraDefenderCard) match {
          case (Some(attackingCard), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
            overlay.show(
              ComparisonDialogGenerator.showTieComparison(
                contextHolder.get.state.getPlayer1,
                contextHolder.get.state.getPlayer2,
                contextHolder.get.state.getRoles.attacker,
                contextHolder.get.state.getRoles.defender, attackingCard, defendingCard, extraAttackerCard, extraDefenderCard, overlay.getPane.getWidth
              ), true
            )

          case _ =>
        }
        resetLastCards()

      case GameActionEvent.DoubleTieComparison =>

        (lastAttackingCard1, lastAttackingCard2, lastDefendingCard, lastExtraAttackerCard, lastExtraDefenderCard) match {
          case (Some(attackingCard1), Some(attackingCard2), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
            overlay.show(
              ComparisonDialogGenerator.showDoubleTieComparison(
                contextHolder.get.state.getPlayer1,
                contextHolder.get.state.getPlayer2,
                contextHolder.get.state.getRoles.attacker,
                contextHolder.get.state.getRoles.defender, attackingCard1, attackingCard2, defendingCard, extraAttackerCard, extraDefenderCard, overlay.getPane.getWidth
              ), true
            )

          case _ =>
        }
        resetLastCards()
      case _ =>
    }
  }

  private def resetLastCards(): Unit = {
    lastAttackingCard = None
    lastAttackingCard1 = None
    lastAttackingCard2 = None
    lastDefendingCard = None
    lastExtraAttackerCard = None
    lastExtraDefenderCard = None
    lastAttackSuccess = None
  }

}