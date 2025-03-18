package view.gui.components.comparison

import controller.*
import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import scalafx.stage.Stage
import util.{ObservableEvent, Observer}
import view.gui.components.comparison.ComparisonInfo
import view.gui.components.sceneView.*
import view.gui.components.sceneView.cardBar.{PlayersFieldBar, PlayersHandBar, SelectablePlayersFieldBar}
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.overlay.Overlay
import view.gui.utils.{ImageUtils, Styles}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ComparisonHandler(controller: IController, overlay: Overlay, updateDisplay: () => Unit) {

  private var lastAttackingCard: Option[ICard] = None
  private var lastAttackingCard1: Option[ICard] = None
  private var lastAttackingCard2: Option[ICard] = None
  private var lastDefendingCard: Option[ICard] = None
  private var lastExtraAttackerCard: Option[ICard] = None
  private var lastExtraDefenderCard: Option[ICard] = None
  private var lastAttackSuccess: Option[Boolean] = None
  
  def handleComparisonEvent(e: ObservableEvent): Unit = {
    e match {
      case ComparedCardsEvent(attackingCard, defendingCard) =>
        println(s"🔄 Compared Cards: Attacker -> ${attackingCard}, Defender -> ${defendingCard}")
        lastAttackingCard = Some(attackingCard)
        lastDefendingCard = Some(defendingCard)

      case DoubleComparedCardsEvent(attackingCard1, attackingCard2, defendingCard) =>
        println(s"🔄 Double Attack! Cards: ${attackingCard1} & ${attackingCard2} vs Defender: ${defendingCard}")
        lastAttackingCard1 = Some(attackingCard1)
        lastAttackingCard2 = Some(attackingCard2)
        lastDefendingCard = Some(defendingCard)

      case AttackResultEvent(attacker, defender, attackSuccess) =>
        println(s"✅ Attack Result: ${attacker.name} vs ${defender.name}, Success: $attackSuccess")
        lastAttackSuccess = Some(attackSuccess)

      case TieComparisonEvent(attackingCard, defendingCard, extraAttackerCard, extraDefenderCard) =>
        println(s"⚖️ Tie Occurred! Resolving with extra cards: ${extraAttackerCard} vs ${extraDefenderCard}")
        lastAttackingCard = Some(attackingCard)
        lastDefendingCard = Some(defendingCard)
        lastExtraAttackerCard = Some(extraAttackerCard)
        lastExtraDefenderCard = Some(extraDefenderCard)

      case DoubleTieComparisonEvent(attackingCard1, attackingCard2, defendingCard, extraAttackerCard, extraDefenderCard) =>
        println(s"⚖️ Double Attack Tie! Resolving with extra cards: ${extraAttackerCard} vs ${extraDefenderCard}")
        lastAttackingCard1 = Some(attackingCard1)
        lastAttackingCard2 = Some(attackingCard2)
        lastDefendingCard = Some(defendingCard)
        lastExtraAttackerCard = Some(extraAttackerCard)
        lastExtraDefenderCard = Some(extraDefenderCard)

      case Events.RegularAttack =>
        println("⚔️ Regular Attack Event Triggered!")

        (lastAttackingCard, lastDefendingCard, lastAttackSuccess) match {
          case (Some(attackingCard), Some(defendingCard), Some(attackSuccess)) =>
            println(s"📢 Showing Overlay: $attackingCard vs $defendingCard")
            overlay.show(
              ComparisonInfo.showSingleComparison(
                controller.getCurrentGame.getPlayer1.name,
                controller.getCurrentGame.getPlayer2.name,
                controller.getCurrentGame.getPlayingField.getAttacker, controller.getCurrentGame.getPlayingField.getDefender, attackingCard, defendingCard, attackSuccess
              )
            )

          case _ =>
            println("⚠️ Missing attack data, skipping alert.")
        }
        resetLastCards()
        updateDisplayAfterOverlay()

      case Events.DoubleAttack =>
        println("⚔️ Double Attack Event Triggered!")

        (lastAttackingCard1, lastAttackingCard2, lastDefendingCard, lastAttackSuccess) match {
          case (Some(attackingCard1), Some(attackingCard2), Some(defendingCard), Some(attackSuccess)) =>
            println(s"📢 Showing Double Attack Overlay: $attackingCard1 & $attackingCard2 vs $defendingCard")
            overlay.show(
              ComparisonInfo.showDoubleComparison(
                controller.getCurrentGame.getPlayer1.name,
                controller.getCurrentGame.getPlayer2.name,
                controller.getCurrentGame.getPlayingField.getAttacker, controller.getCurrentGame.getPlayingField.getDefender, attackingCard1, attackingCard2, defendingCard, attackSuccess
              )
            )

          case _ =>
            println("⚠️ Missing double attack data, skipping alert.")
        }
        resetLastCards()
        updateDisplayAfterOverlay()

      case Events.TieComparison =>
        println("⚔️ Tie Resolution Event Triggered!")

        (lastAttackingCard, lastDefendingCard, lastExtraAttackerCard, lastExtraDefenderCard) match {
          case (Some(attackingCard), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
            println(s"📢 Showing Tie Overlay: $attackingCard vs $defendingCard | Tie-breaker: $extraAttackerCard vs $extraDefenderCard")
            overlay.show(
              ComparisonInfo.showTieComparison(
                controller.getCurrentGame.getPlayer1.name,
                controller.getCurrentGame.getPlayer2.name,
                controller.getCurrentGame.getPlayingField.getAttacker, controller.getCurrentGame.getPlayingField.getDefender, attackingCard, defendingCard, extraAttackerCard, extraDefenderCard
              )
            )

          case _ =>
            println("⚠️ Missing tie-breaker data, skipping alert.")
        }
        resetLastCards()
        updateDisplayAfterOverlay()

      case Events.DoubleTieComparison =>
        println("⚔️ Double Attack Tie Resolution Event Triggered!")

        (lastAttackingCard1, lastAttackingCard2, lastDefendingCard, lastExtraAttackerCard, lastExtraDefenderCard) match {
          case (Some(attackingCard1), Some(attackingCard2), Some(defendingCard), Some(extraAttackerCard), Some(extraDefenderCard)) =>
            println(s"📢 Showing Double Tie Overlay: $attackingCard1 & $attackingCard2 vs $defendingCard | Tie-breaker: $extraAttackerCard vs $extraDefenderCard")
            overlay.show(
              ComparisonInfo.showDoubleTieComparison(
                controller.getCurrentGame.getPlayer1.name,
                controller.getCurrentGame.getPlayer2.name,
                controller.getCurrentGame.getPlayingField.getAttacker, controller.getCurrentGame.getPlayingField.getDefender, attackingCard1, attackingCard2, defendingCard, extraAttackerCard, extraDefenderCard
              )
            )

          case _ =>
            println("⚠️ Missing double tie-breaker data, skipping alert.")
        }
        resetLastCards()
        updateDisplayAfterOverlay()
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

  private def updateDisplayAfterOverlay(): Unit = {
    Future {
      Thread.sleep(3000)
      Platform.runLater(() => updateDisplay())
    }
  }
}
