package de.htwg.se.soccercardclash.view.gui.scenes

import com.google.inject.Inject
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.playerComponent.base.*
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.playerView.{AIProfile, PlayerAvatarRegistry}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameStartupDataHolder
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.{AIRegistry, Assets, Styles}
import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Priority, StackPane, VBox}
import scalafx.scene.text.{Font, Text}

class AISelectionScene(
                        controller: IController,
                        contextHolder: IGameContextHolder,
                        startupDataHolder: GameStartupDataHolder
                      ) extends GameScene {

  this.getStylesheets.add(Styles.aiSelection)
  private val overlay = new Overlay(this)

  private val aiProfiles = AIRegistry.aiProfiles

  private var selectedAI: Option[String] = None

  private val title = new Label("Choose Your AI Opponent") {
    styleClass += "title"
    padding = Insets(20)
  }

  private val aiCardContainer: HBox = new HBox {
    spacing = 20
    alignment = Pos.CENTER
    children = aiProfiles.map(createAICard)
  }

  private val startButton = GameButtonFactory.createGameButton("Start Game", 250, 60) { () =>
    (startupDataHolder.data.humanPlayerName, selectedAI) match {
      case (Some(humanName), Some(aiName)) =>
        controller.createGameWithAI(humanName, aiName)

        val players = Seq(
          contextHolder.get.state.getRoles.attacker,
          contextHolder.get.state.getRoles.defender
        )
        PlayerAvatarRegistry.assignAvatarsInOrder(players)

        EventDispatcher.dispatchSingle(controller, SceneSwitchEvent.PlayingField)

      case (_, None) =>
        showAlert("Please select an AI opponent.")
      case (None, _) =>
        showAlert("Missing player name.")
    }
  }

  private val backButton = GameButtonFactory.createGameButton("Back", 250, 60) { () =>
    GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayerWithAI)
  }

  private val buttonBox = new HBox(30, startButton, backButton) {
    alignment = Pos.CENTER
    padding = Insets(30, 0, 0, 0)
  }

  private val layout = new VBox(20, title, aiCardContainer, buttonBox) {
    alignment = Pos.TOP_CENTER
    padding = Insets(20)
    styleClass += "ai-selection-layout"
  }

  root = new StackPane {
    children = Seq(layout)
  }

  private def createAICard(profile: AIProfile): VBox = {
    val image = new ImageView(PlayerAvatarRegistry.getImages(profile.imageFile)) {
      fitWidth = 450
      fitHeight = 300
      preserveRatio = true
      smooth = true
    }

    val nameLabel = new Label(profile.name) {
      styleClass += "ai-card-title"
    }

    val descLabel = new Label(profile.description) {
      wrapText = true
      maxWidth = 180
      styleClass += "ai-card-description"
    }

    val card = new VBox(10, image, nameLabel, descLabel) {
      alignment = Pos.CENTER
      padding = Insets(10)
      styleClass += "ai-card"
    }

    image.fitWidthProperty.bind(card.widthProperty.subtract(20))

    card.onMouseClicked = _ => {
      selectedAI = Some(profile.name)
      highlightSelectedCard(card)
    }

    card
  }

  private def highlightSelectedCard(selected: VBox): Unit = {
    aiCardContainer.children.foreach {
      case node: javafx.scene.layout.VBox =>
        node.getStyleClass.remove("selected-card")
      case _ =>
    }
    selected.styleClass += "selected-card"
  }


  private def showAlert(content: String): Unit = {
    val alert = GameAlertFactory.createAlert(content, overlay, autoHide = false)
    overlay.show(alert, autoHide = false)
  }
}

