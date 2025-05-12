//package de.htwg.se.soccercardclash.view.gui.components.sceneComponents
//
//import javafx.application.Platform
//import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
//import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IGameState
//import scalafx.Includes.*
//import scalafx.animation.*
//import scalafx.geometry.Pos
//import scalafx.scene.control.Label
//import scalafx.scene.effect.DropShadow
//import scalafx.scene.input.MouseEvent
//import scalafx.scene.layout.HBox
//import scalafx.scene.paint.Color
//import scalafx.util.Duration
//import de.htwg.se.soccercardclash.view.gui.components.cardView.HandCard
//import de.htwg.se.soccercardclash.view.gui.components.uiFactory.CardAnimationFactory
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.{ExecutionContext, Future}
//import scala.math.*

//class PlayersHandBar(player: IPlayer, playingField: IGameState, isLeftSide: Boolean) extends HBox {
//
//  alignment = if (isLeftSide) Pos.CENTER_LEFT else Pos.CENTER_RIGHT
//  spacing = Math.max(-30 + (playingField.getDataManager.getPlayerHand(player).getHandSize * -2), -50)
//
//
//  private var selectedCard: Option[HandCard] = None
//
//  /** Label indicating whose hand this is */
//  private val playerLabel = new Label {
//    text = s"${player.name}'s Hand"
//    style = "-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px; -fx-background-radius: 10px;"
//  }
//
//  /** Creates ImageViews for all Hand Cards with visual effects */
//  def createHandCardRow(): HBox = {
//    val hand = playingField.getDataManager.getPlayerHand(player)
//
//    val handCards = hand.toList.zipWithIndex.map { case (card, index) =>
//
//      val isLastCard = index == hand.getHandSize - 1
////      val handCard = new HandCard(flipped = !isLastCard, card = card)
//      val handCard = new HandCard(flipped = false, card = card)
//      // Drop shadow for depth
//      handCard.effect = new DropShadow(10, Color.BLACK)
//
//      // Hover effect
//      handCard.onMouseEntered = (_: MouseEvent) => {
//        val hoverEffect = new ScaleTransition(Duration(200), handCard)
//        hoverEffect.toX = 1.15
//        hoverEffect.toY = 1.15
//        hoverEffect.play()
//      }
//
//      handCard.onMouseExited = (_: MouseEvent) => {
//        val exitEffect = new ScaleTransition(Duration(200), handCard)
//        exitEffect.toX = 1.0
//        exitEffect.toY = 1.0
//        exitEffect.play()
//      }
//
//      handCard
//    }
//
//    new HBox {
//      alignment = Pos.CENTER
//      spacing = -25 // More overlapping for a fanned-out look
//      children = handCards
//    }
//  }
//
//  children = Seq(playerLabel, createHandCardRow())
//
//  /** Updates Hand UI with enhanced animations */
//  def updateBar(): Unit = {
//    println("🔄 Updating Hand UI with enhanced animation...")
//
//    val oldChildren = children.tail // Exclude label
//    val newHandRow = createHandCardRow()
//
//    // Animate existing cards with a smoother transition
//    oldChildren.foreach {
//      case hbox: javafx.scene.layout.HBox =>
//        hbox.getChildren.forEach { node =>
//          val moveTransition = new TranslateTransition(Duration(500), node) // ⏳ Slower, more impactful
//          moveTransition.byX = 25 // Move further
//          moveTransition.interpolator = Interpolator.EaseOut
//
//          val scaleTransition = new ScaleTransition(Duration(400), node)
//          scaleTransition.toX = 1.0
//          scaleTransition.toY = 1.1
//          scaleTransition.autoReverse = true
//
//          val rotateTransition = new RotateTransition(Duration(400), node)
//          rotateTransition.byAngle = 8
//          rotateTransition.autoReverse = true
//
//          moveTransition.play()
//          scaleTransition.play()
//          rotateTransition.play()
//        }
//      case _ => // Ignore non-HBox elements
//    }
//
//    // Add new hand row after animation with fade-in effect
//    Future {
//      Thread.sleep(500) // Wait for transition
//      Platform.runLater(() => {
//        children.clear()
//        children.addAll(playerLabel, newHandRow)
//
//        // Apply fade-in effect for new cards
//        newHandRow.children.foreach { node =>
//          val fadeTransition = new FadeTransition(Duration(500), node)
//          fadeTransition.fromValue = 0.0
//          fadeTransition.toValue = 1.0
//          fadeTransition.play()
//        }
//
//        selectedCard = CardAnimationFactory.highlightLastHandCard(player, playingField)
//
////        playingField.notifyObservers()
//      })
//    }
//  }
//}
