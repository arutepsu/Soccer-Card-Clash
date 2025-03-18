package view.gui.scenes
import scalafx.application.JFXApp3
import scalafx.stage.{Stage, Modality}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.text.Text
import scalafx.geometry.Pos

object GameAlert {
  def show(dialogTitle: String, message: String): Boolean = {
    var result = false

    // Create a new Stage (modal dialog)
    val dialog = new Stage {
      initModality(Modality.ApplicationModal)
      this.title.value = dialogTitle  // Explicitly reference the title property
    }

    // Message text
    val messageText = new Text(message)

    // OK and Cancel buttons
    val okButton = new Button("OK") {
      onAction = _ => {
        result = true
        dialog.close()
      }
    }

    val cancelButton = new Button("Cancel") {
      onAction = _ => {
        result = false
        dialog.close()
      }
    }

    // Button container
    val buttonBox = new HBox(10, okButton, cancelButton) {
      alignment = Pos.Center
    }

    // Layout
    val vbox = new VBox(20, messageText, buttonBox) {
      alignment = Pos.Center
      prefWidth = 300
      prefHeight = 150
    }

    // Set Scene
    dialog.scene = new Scene {
      root = vbox
    }

    // Show dialog and wait for user response
    dialog.showAndWait()

    result // Return user selection
  }
}