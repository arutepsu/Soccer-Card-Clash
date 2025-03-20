package view.gui.components.comparison

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import scalafx.animation.FadeTransition
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.Pos
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.*
import scalafx.scene.image.*
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.*
import scalafx.stage.{Modality, Stage}
import view.gui.components.playerView.PlayerAvatar
import view.gui.utils.{CardImageLoader, Styles}
import scalafx.animation._
import scalafx.util.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object ComparisonDialogGenerator {
  def showSingleComparison(
                            player1: String,
                            player2: String,
                            attacker: IPlayer,
                            defender: IPlayer,
                            attackingCard: ICard,
                            defendingCard: ICard,
                            attackSuccess: Boolean,
                            sceneWidth: Double
                          ): Node = {
    showComparisonUI(player1, player2, attacker, defender, Some(attackingCard), None, defendingCard, attackSuccess, None, None, sceneWidth)
  }

  def showDoubleComparison(
                            player1: String,
                            player2: String,
                            attacker: IPlayer,
                            defender: IPlayer,
                            attackingCard1: ICard,
                            attackingCard2: ICard,
                            defendingCard: ICard,
                            attackSuccess: Boolean,
                            sceneWidth: Double
                          ): Node = {
    showComparisonUI(player1, player2, attacker, defender, Some(attackingCard1), Some(attackingCard2), defendingCard, attackSuccess, None, None, sceneWidth)
  }

  def showTieComparison(
                         player1: String,
                         player2: String,
                         attacker: IPlayer,
                         defender: IPlayer,
                         attackingCard: ICard,
                         defendingCard: ICard,
                         extraAttackerCard: ICard,
                         extraDefenderCard: ICard,
                         sceneWidth: Double
                       ): Node = {
    showComparisonUI(player1, player2, attacker, defender, Some(attackingCard), None, defendingCard, attackSuccess = false, Some(extraAttackerCard), Some(extraDefenderCard), sceneWidth)
  }

  def showDoubleTieComparison(
                               player1: String,
                               player2: String,
                               attacker: IPlayer,
                               defender: IPlayer,
                               attackingCard1: ICard,
                               attackingCard2: ICard,
                               defendingCard: ICard,
                               extraAttackerCard: ICard,
                               extraDefenderCard: ICard,
                               sceneWidth: Double
                             ): Node = {
    showComparisonUI(player1, player2, attacker, defender, Some(attackingCard1), Some(attackingCard2), defendingCard, attackSuccess = false, Some(extraAttackerCard), Some(extraDefenderCard), sceneWidth)
  }

  import scalafx.scene.paint.Color
  import scalafx.scene.shape.Rectangle
  import scalafx.scene.image.ImageView
  import scalafx.scene.layout.{HBox, StackPane, VBox}
  import scalafx.scene.text.Text

  private def showComparisonUI(
                                player1: String,
                                player2: String,
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
    val baseWidth = 1200.0  // Reference width for scaling
    val scaleFactor = Math.max(0.7, Math.min(1.5, sceneWidth / baseWidth))

    val resultMessage = if (attackSuccess) "✅ Attack Successful!" else "❌ Attack Failed!"

    val resultText = new Text(resultMessage) {
      style = s"-fx-font-size: ${16 * scaleFactor}px; -fx-font-weight: bold; -fx-fill: white;"
      opacity = 0.0 // Initially hidden
    }
    def showResultText(): Unit = {
      val fadeIn = new FadeTransition(Duration(500), resultText)
      fadeIn.fromValue = 0.0
      fadeIn.toValue = 1.0
      fadeIn.play()
    }
    val attackerAvatarPath = "/images/data/players/player1.jpeg"
    val defenderAvatarPath = "/images/data/players/player2.jpeg"

    val attackerAvatar = new PlayerAvatar(
      attacker, 1,
      scaleAvatar = (0.1 * scaleFactor).toFloat,
      scaleFont = (0.3 * scaleFactor).toFloat,
      profilePicturePath = attackerAvatarPath
    )

    val defenderAvatar = new PlayerAvatar(
      defender, 2,
      scaleAvatar = (0.1 * scaleFactor).toFloat,
      scaleFont = (0.3 * scaleFactor).toFloat,
      profilePicturePath = defenderAvatarPath
    )

    val attackingCardImage1 = attackingCard1.map(card => CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false,  scaleFactor = (0.7 * scaleFactor).toFloat))
    val attackingCardImage2 = attackingCard2.map(card => CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false,  scaleFactor = (0.7 * scaleFactor).toFloat))
    val defendingCardImage = CardImageLoader.loadCardImage(defendingCard, flipped = false, isLastCard = false, scaleFactor = 0.6f)

    val extraAttackerCardImage = extraAttackerCard.map { card =>
      CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false,  scaleFactor = (0.7 * scaleFactor).toFloat)
    }

    val extraDefenderCardImage = extraDefenderCard.map { card =>
      CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false,  scaleFactor = (0.7 * scaleFactor).toFloat)
    }


    val attackerWins = attackSuccess
    val defenderWins = !attackSuccess

    def createCardFrame(image: ImageView, card: Option[ICard], highlightGreen: Boolean, highlightRed: Boolean): StackPane = {
      val borderEffect = new Rectangle {
        width = image.fitWidth.value + 10
        height = image.fitHeight.value + 10
        stroke = Color.Transparent  // Initially no color
        strokeWidth = 3
        fill = Color.Transparent
      }

      val frame = new StackPane {
        children = Seq(image, borderEffect)
      }

      // ✅ Step 1: Slide cards into position
      slideInNode(frame, fromX = if (highlightGreen) -200 else 200, durationMillis = 700)

      // ✅ Step 2: After 1 second, highlight the winning/losing card
      Future {
        Thread.sleep(1000)  // Wait 1 second before highlighting
        Platform.runLater {
          borderEffect.stroke = if (highlightGreen) Color.LimeGreen else if (highlightRed) Color.Red else Color.Transparent
        }
      }

      frame
    }

    val attackingCardFrame1 = attackingCard1.map { card =>
      createCardFrame(CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false, scaleFactor = 0.7f),
        Some(card), highlightGreen = attackerWins, highlightRed = false)
    }

    val attackingCardFrame2 = attackingCard2.map { card =>
      createCardFrame(CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false, scaleFactor = 0.7f),
        Some(card), highlightGreen = attackerWins, highlightRed = false)
    }

    val defendingCardFrame = createCardFrame(CardImageLoader.loadCardImage(defendingCard, flipped = false, isLastCard = false, scaleFactor = 0.7f),
      Some(defendingCard), highlightGreen = false, highlightRed = defenderWins)

    val extraAttackingCardFrame = extraAttackerCard.map { card =>
      createCardFrame(CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false, scaleFactor = 0.7f),
        Some(card), highlightGreen = attackerWins, highlightRed = false)
    }

    val extraDefendingCardFrame = extraDefenderCard.map { card =>
      createCardFrame(CardImageLoader.loadCardImage(card, flipped = false, isLastCard = false, scaleFactor = 0.7f),
        Some(card), highlightGreen = false, highlightRed = defenderWins)
    }

    val winnerText = new Text(s"🏆 Winner: " + (if (attackerWins) player1 else player2)) {
      style = if (attackerWins) "-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: green;"
      else s"-fx-font-size: ${16 * scaleFactor}px; -fx-font-weight: bold; -fx-fill: red;"
      opacity = 0.0 // Initially hidden
    }
    // ✅ Function to show winner text with a fade-in effect
    def showWinnerText(): Unit = {
      val fadeIn = new FadeTransition(Duration(500), winnerText)
      fadeIn.fromValue = 0.0
      fadeIn.toValue = 1.0
      fadeIn.play()
    }

    Future {
      Thread.sleep(1500)
      Platform.runLater {
        showWinnerText()
      }
    }
    Future {
      Thread.sleep(1500)
      Platform.runLater {
        showResultText()
      }
    }

//    val resultText = new Text(resultMessage)


    val attackerInfo = new VBox(5, attackerAvatar)
    val defenderInfo = new VBox(5, defenderAvatar)

    val attackingCardsBox = new HBox(10)
    attackingCardFrame1.foreach(frame => attackingCardsBox.children.add(frame))
    attackingCardFrame2.foreach(frame => attackingCardsBox.children.add(frame))
    attackingCardsBox.alignment = scalafx.geometry.Pos.Center

    val tiebreakerCardsBox = new HBox(10)
    extraAttackingCardFrame.foreach(frame => tiebreakerCardsBox.children.add(frame))
    extraDefendingCardFrame.foreach(frame => tiebreakerCardsBox.children.add(frame))
    tiebreakerCardsBox.alignment = scalafx.geometry.Pos.Center

    val cardImagesBox = new VBox(10, new HBox(20, attackingCardsBox, defendingCardFrame) {
      alignment = scalafx.geometry.Pos.Center
    })

    if (extraAttackerCard.isDefined || extraDefenderCard.isDefined) {
      cardImagesBox.children.add(tiebreakerCardsBox)
    }

    val playerInfoBox = new HBox(10, attackerInfo, cardImagesBox, defenderInfo) {
      alignment = scalafx.geometry.Pos.Center
    }

    val backgroundImagePath = "/images/data/frames/pause (1).png"
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
           | -fx-background-image: url('$imageUrl'); /* ✅ Ensures correct image loading */
           | -fx-background-size: 100% 100%; /* ✅ Makes the image twice as big */
           | -fx-background-repeat: no-repeat;
           | -fx-background-position: center;
           | -fx-background-color: transparent; /* ✅ Removes default white background */
           | -fx-padding: 15px;
           | -fx-border-radius: 10px;
""".stripMargin
    }

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
    slideIn.interpolator = Interpolator.EaseOut // ✅ Smooth deceleration
    slideIn.play()
  }


}