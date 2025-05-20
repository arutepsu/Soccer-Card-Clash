package de.htwg.se.soccercardclash.view.gui.components.dialog

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import scalafx.animation.FadeTransition
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.*
import scalafx.scene.image.*
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.*
import scalafx.stage.{Modality, Stage}
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.{UIAction, UIActionScheduler}
import de.htwg.se.soccercardclash.view.gui.utils.{CardImageRegistry, Styles}
import scalafx.animation.*
import scalafx.util.Duration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Text

import java.util.concurrent.{Executors, TimeUnit}

// TODO: Fix - The tie comparison isn't shown. Positions of cards should be changed when attack fails
object ComparisonDialogGenerator {
  def showSingleComparison(
                            attacker: IPlayer,
                            defender: IPlayer,
                            attackingCard: ICard,
                            defendingCard: ICard,
                            attackSuccess: Boolean,
                            sceneWidth: Double
                          ): Node = {
    showComparisonUI(attacker, defender, Some(attackingCard), None, defendingCard, attackSuccess, None, None, sceneWidth)
  }

  def showDoubleComparison(
                            attacker: IPlayer,
                            defender: IPlayer,
                            attackingCard1: ICard,
                            attackingCard2: ICard,
                            defendingCard: ICard,
                            attackSuccess: Boolean,
                            sceneWidth: Double
                          ): Node = {
    showComparisonUI(attacker, defender, Some(attackingCard1), Some(attackingCard2), defendingCard, attackSuccess, None, None, sceneWidth)
  }

  def showTieComparison(
                         attacker: IPlayer,
                         defender: IPlayer,
                         attackingCard: ICard,
                         defendingCard: ICard,
                         extraAttackerCard: ICard,
                         extraDefenderCard: ICard,
                         sceneWidth: Double
                       ): Node = {
    showComparisonUI(attacker, defender, Some(attackingCard), None, defendingCard, attackSuccess = false, Some(extraAttackerCard), Some(extraDefenderCard), sceneWidth)
  }

  def showDoubleTieComparison(
                               attacker: IPlayer,
                               defender: IPlayer,
                               attackingCard1: ICard,
                               attackingCard2: ICard,
                               defendingCard: ICard,
                               extraAttackerCard: ICard,
                               extraDefenderCard: ICard,
                               sceneWidth: Double
                             ): Node = {
    showComparisonUI(attacker, defender, Some(attackingCard1), Some(attackingCard2), defendingCard, attackSuccess = false, Some(extraAttackerCard), Some(extraDefenderCard), sceneWidth)
  }
  private val scheduler = Executors.newSingleThreadScheduledExecutor()
  private def runLater(delayMillis: Long)(block: => Unit): Unit = {
    scheduler.schedule(new Runnable {
      override def run(): Unit = Platform.runLater(block)
    }, delayMillis, TimeUnit.MILLISECONDS)
  }
  def createCardImageView(card: ICard, scale: Float): ImageView = {
    new ImageView(CardImageRegistry.getImage(card.fileName)) {
      fitWidth = 325 * scale
      fitHeight = 275 * scale
      preserveRatio = true
      smooth = true
    }
  }

  private def showComparisonUI(
                                attacker: IPlayer,
                                defender: IPlayer,
                                attackingCard1: Option[ICard],
                                attackingCard2: Option[ICard],
                                defendingCard: ICard,
                                attackSuccess: Boolean,
                                extraAttackerCard: Option[ICard],
                                extraDefenderCard: Option[ICard],
                                sceneWidth: Double
                              ): Node = {
    val baseWidth = 1200.0
    val scaleFactor = Math.max(0.7, Math.min(1.5, sceneWidth / baseWidth))
    Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Bold.ttf"), 20)
    Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 20)
    val resultMessage = if (attackSuccess) "Attack Successful!" else "Attack Failed!"

    val resultText = new Text(resultMessage) {
      style = s"-fx-font-size: ${16 * scaleFactor}px;" +
        s"-fx-font-weight: bold;" +
        s"-fx-fill: white;" +
        s"-fx-font-family: Rajdhani;"
      opacity = 0.0
    }

    def showResultText(): Unit = {
      val fadeIn = new FadeTransition(Duration(500), resultText)
      fadeIn.fromValue = 0.0
      fadeIn.toValue = 1.0
      fadeIn.play()
    }

    val leftPlayer = attacker
    val rightPlayer = defender


    val leftAvatar: ImageView = PlayerAvatarRegistry.getAvatarView(
      player = leftPlayer,
      scale = (0.1 * scaleFactor).toFloat
    )

    val rightAvatar: ImageView = PlayerAvatarRegistry.getAvatarView(
      player = rightPlayer,
      scale = (0.1 * scaleFactor).toFloat
    )

    val attackingCardImage1 = attackingCard1.map(createCardImageView(_, (0.7 * scaleFactor).toFloat))
    val attackingCardImage2 = attackingCard2.map(createCardImageView(_, (0.7 * scaleFactor).toFloat))
    val defendingCardImage = createCardImageView(defendingCard, 0.6f)

    val extraAttackerCardImage = extraAttackerCard.map { card =>
      createCardImageView(card, (0.7 * scaleFactor).toFloat)
    }

    val extraDefenderCardImage = extraDefenderCard.map { card =>
      createCardImageView(card, (0.7 * scaleFactor).toFloat)
    }



    def createCardFrame(image: ImageView, card: Option[ICard], highlightGreen: Boolean, highlightRed: Boolean): StackPane = {
      val borderEffect = new Rectangle {
        width = image.fitWidth.value
        height = image.fitHeight.value
        stroke = Color.Transparent
        strokeWidth = 3
        fill = Color.Transparent

      }

      val frame = new StackPane {
        children = Seq(image, borderEffect)
      }

      slideInNode(frame, fromX = if (highlightGreen) -100 else 100, durationMillis = 700)

      val pause = new PauseTransition(Duration(1000))
      pause.setOnFinished(_ => borderEffect.stroke =
        if (highlightGreen) Color.LimeGreen
        else if (highlightRed) Color.Red
        else Color.Transparent
      )
      Platform.runLater {
        pause.play()
      }


      frame
    }
    val attackingCardFrame1 = attackingCard1.map { card =>
      createCardFrame(
        createCardImageView(card, 0.7f),
        Some(card),
        highlightGreen = attackSuccess,
        highlightRed = false
      )
    }

    val attackingCardFrame2 = attackingCard2.map { card =>
      createCardFrame(
        createCardImageView(card, 0.7f),
        Some(card),
        highlightGreen = attackSuccess,
        highlightRed = false
      )
    }

    val extraAttackingCardFrame = extraAttackerCard.map { card =>
      createCardFrame(
        createCardImageView(card, 0.7f),
        Some(card),
        highlightGreen = attackSuccess,
        highlightRed = false
      )
    }

    val defendingCardFrame = createCardFrame(
      createCardImageView(defendingCard, 0.7f),
      Some(defendingCard),
      highlightGreen = false,
      highlightRed = !attackSuccess
    )

    val extraDefendingCardFrame = extraDefenderCard.map { card =>
      createCardFrame(
        createCardImageView(card, 0.7f),
        Some(card),
        highlightGreen = false,
        highlightRed = !attackSuccess
      )
    }
    val isDefenderWinner = !attackSuccess

    val winnerTextContent =
      if (isDefenderWinner) s"🏆 Winner: ${attacker.name}" //roles changed and defender is now attacker
      else s"🏆 Winner: ${attacker.name}"

    val winnerColor = if (isDefenderWinner) "red" else "green"

    val winnerText = new Text(winnerTextContent) {
      style = s"-fx-font-size: ${30 * scaleFactor}px;" +
        s"-fx-font-weight: bold;" +
        s"-fx-fill: $winnerColor;" +
        s"-fx-font-family: Rajdhani;"
      opacity = 0.0
    }

    def showWinnerText(): Unit = {
      val fadeIn = new FadeTransition(Duration(500), winnerText)
      fadeIn.fromValue = 0.0
      fadeIn.toValue = 1.0
      Platform.runLater {
        fadeIn.play()
      }
    }

    val player1CardsBox = new HBox(10) {
      alignment = scalafx.geometry.Pos.Center
    }
    val player2CardsBox = new HBox(10) {
      alignment = scalafx.geometry.Pos.Center
    }

    val leftCardFrames: Seq[StackPane] = {
      val frames = scala.collection.mutable.ListBuffer[StackPane]()

      frames ++= attackingCardFrame1
      frames ++= attackingCardFrame2
      frames ++= extraAttackingCardFrame

      frames.toSeq
    }

    val rightCardFrames: Seq[StackPane] = {
      val frames = scala.collection.mutable.ListBuffer[StackPane]()

      frames += defendingCardFrame
      extraDefendingCardFrame.foreach(frames += _)

      frames.toSeq
    }


    val leftSpacing = if (leftCardFrames.size >= 2) 5 else 10
    val rightSpacing = if (rightCardFrames.size >= 2) 5 else 10

    val leftCardsBox = new HBox(leftSpacing) {
      alignment = scalafx.geometry.Pos.Center
      children ++= leftCardFrames.map(_.delegate)
    }

    val rightCardsBox = new HBox(rightSpacing) {
      alignment = scalafx.geometry.Pos.Center
      children ++= rightCardFrames.map(_.delegate)
    }



    val tiebreakerCardsBox = new HBox(10) {
      alignment = scalafx.geometry.Pos.Center
    }
    extraAttackingCardFrame.foreach(frame => tiebreakerCardsBox.children += frame)
    extraDefendingCardFrame.foreach(frame => tiebreakerCardsBox.children += frame)

    val cardImagesHBox = new HBox(20) {
      alignment = scalafx.geometry.Pos.Center
      children ++= Seq(leftCardsBox, rightCardsBox)
    }



    val cardImagesBox = new VBox(10, cardImagesHBox)

    if (extraAttackerCard.isDefined || extraDefenderCard.isDefined) {
      cardImagesBox.children.add(tiebreakerCardsBox)
    }

    val cardImagesBoxPadded = new VBox(20, cardImagesBox) {
      padding = Insets(0, 80, 0, 80) // top, right, bottom, left
    }

    val playerInfoBox = new HBox(10,
      new VBox(5, leftAvatar),
      cardImagesBoxPadded,
      new VBox(5, rightAvatar)
    ) {
      alignment = scalafx.geometry.Pos.Center
    }


    val backgroundImagePath = "/images/data/frames/overlay.png"
    val imageUrl = Option(getClass.getResource(backgroundImagePath))
      .map(_.toExternalForm)
      .getOrElse {
        println(s"Error: Image not found at $backgroundImagePath")
        ""
      }

    val root = new VBox(10, playerInfoBox, winnerText, resultText) {
      alignment = scalafx.geometry.Pos.Center
      style =
        s"""
           | -fx-background-image: url('$imageUrl'); /* Ensures correct image loading */
           | -fx-background-size: 100% 100%; /* Makes the image twice as big */
           | -fx-background-repeat: no-repeat;
           | -fx-background-position: center;
           | -fx-background-color: transparent; /* Removes default white background */
           | -fx-padding: 15px;
           | -fx-border-radius: 10px;
""".stripMargin
    }
    val scheduler = UIActionScheduler()

    scheduler.runSequence(
      UIAction.delayed(0) {
        fadeInNode(root, 700)
      },
      UIAction.delayed(1500) {
        showWinnerText()
      },
      UIAction.delayed(1500) {
        showResultText()
      }
    )
    fadeInNode(root, 700)
    root
  }

  def fadeInNode(node: Node, durationMillis: Int = 500): Unit = {
    val fadeIn = new FadeTransition(Duration(durationMillis), node)
    fadeIn.fromValue = 0.0
    fadeIn.toValue = 1.0
    fadeIn.play()
  }

  def slideInNode(node: Node, fromX: Double = -200, durationMillis: Int = 700): Unit = {
    node.translateX = fromX

    val slideIn = new TranslateTransition(Duration(durationMillis), node)
    slideIn.toX = 0
    slideIn.interpolator = Interpolator.EaseOut
    slideIn.play()
  }


}