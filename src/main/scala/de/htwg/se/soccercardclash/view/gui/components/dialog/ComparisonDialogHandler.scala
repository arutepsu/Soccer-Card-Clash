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
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.UIAction
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

  def createOverlayAction(e: GameActionEvent): Option[UIAction] = {
    val width = overlay.getPane.getWidth
    val state = contextHolder.get.state
    val attacker = state.getRoles.attacker
    val defender = state.getRoles.defender

    e match {
      case GameActionEvent.RegularAttack =>
        for {
          card <- lastAttackingCard
          defCard <- lastDefendingCard
          success <- lastAttackSuccess
        } yield UIAction.delayed(0) {
          overlay.show(
            ComparisonDialogGenerator.showSingleComparison(
              state.getPlayer1, state.getPlayer2,
              attacker, defender,
              card, defCard, success, width
            ),
            true
          )
        }

      case GameActionEvent.DoubleAttack =>
        for {
          c1 <- lastAttackingCard1
          c2 <- lastAttackingCard2
          defCard <- lastDefendingCard
          success <- lastAttackSuccess
        } yield UIAction.delayed(0) {
          overlay.show(
            ComparisonDialogGenerator.showDoubleComparison(
              state.getPlayer1, state.getPlayer2,
              attacker, defender, c1, c2, defCard, success, width
            ),
            true
          )
        }

      case GameActionEvent.TieComparison =>
        for {
          c1 <- lastAttackingCard
          c2 <- lastDefendingCard
          c3 <- lastExtraAttackerCard
          c4 <- lastExtraDefenderCard
        } yield UIAction.delayed(0) {
          overlay.show(
            ComparisonDialogGenerator.showTieComparison(
              state.getPlayer1, state.getPlayer2,
              attacker, defender, c1, c2, c3, c4, width
            ),
            true
          )
        }

      case GameActionEvent.DoubleTieComparison =>
        for {
          c1 <- lastAttackingCard1
          c2 <- lastAttackingCard2
          c3 <- lastDefendingCard
          c4 <- lastExtraAttackerCard
          c5 <- lastExtraDefenderCard
        } yield UIAction.delayed(0) {
          overlay.show(
            ComparisonDialogGenerator.showDoubleTieComparison(
              state.getPlayer1, state.getPlayer2,
              attacker, defender, c1, c2, c3, c4, c5, width
            ),
            true
          )
        }

      case _ => None
    }
  }
  def handleComparisonEvent(e: ObservableEvent): Unit = {
    e match {
      case StateEvent.ComparedCardsEvent(Some(attackingCard), Some(defendingCard)) =>
        lastAttackingCard = Some(attackingCard)
        lastDefendingCard = Some(defendingCard)
        println(f"${lastAttackingCard} and ${lastDefendingCard}")

      case StateEvent.DoubleComparedCardsEvent(Some(attackingCard1), Some(attackingCard2), Some(defendingCard)) =>
        lastAttackingCard1 = Some(attackingCard1)
        lastAttackingCard2 = Some(attackingCard2)
        lastDefendingCard = Some(defendingCard)

      case StateEvent.AttackResultEvent(attacker, defender, attackSuccess) =>
        lastAttackSuccess = Some(attackSuccess)
        println(f"${lastAttackSuccess}")
      case StateEvent.TieComparisonEvent(Some(attackingCard), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
        lastAttackingCard = Some(attackingCard)
        lastDefendingCard = Some(defendingCard)
        lastExtraAttackerCard = Some(extraAttackerCard)
        lastExtraDefenderCard = Some(extraDefenderCard)

      case StateEvent.DoubleTieComparisonEvent(Some(attackingCard1), Some(attackingCard2), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
        lastAttackingCard1 = Some(attackingCard1)
        lastAttackingCard2 = Some(attackingCard2)
        lastDefendingCard = Some(defendingCard)
        lastExtraAttackerCard = Some(extraAttackerCard)
        lastExtraDefenderCard = Some(extraDefenderCard)
        
      case _ =>
    }
  }

  def resetLastCards(): Unit = {
    lastAttackingCard = None
    lastAttackingCard1 = None
    lastAttackingCard2 = None
    lastDefendingCard = None
    lastExtraAttackerCard = None
    lastExtraDefenderCard = None
    lastAttackSuccess = None
  }

}