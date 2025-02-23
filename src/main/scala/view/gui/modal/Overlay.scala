package view.gui.modal

import scalafx.Includes.*
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.effect.BoxBlur
import scalafx.scene.image.{ImageView, WritableImage}
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.util.Duration

class Overlay(
               windowWidth: Double,
               windowHeight: Double,
               sceneContent: () => Node,
               modalBox: Node,
               var onOpenFinish: () => Unit = () => (),
               var onCloseFinish: () => Unit = () => ()
             ) {
  val imageView = new ImageView{
    fitWidth <== windowWidth
    fitHeight <== windowHeight
    preserveRatio = false
  }

  val overlay = new Rectangle {
    width = windowWidth
    height = windowHeight
    fill = Color.rgb(0, 0, 0, 0.35) // semi-transparent black
  }

  def updateSnapshot(): Unit = {
    Platform.runLater {
      val snapshot = new WritableImage(windowWidth.toInt, windowHeight.toInt)
      val node = sceneContent()

      node.delegate match {
        case parent: javafx.scene.Parent =>
          parent.applyCss() // ✅ Ensure styles are applied before capturing
          parent.layout() // ✅ Force a UI refresh before taking the snapshot
        case _ =>
          println("⚠️ Warning: Node is not a Parent, skipping layout update.")
      }

      println(s"📷 Taking snapshot of: ${node.getClass.getSimpleName}")

      node.snapshot(null, snapshot) // ✅ Capture full scene, including cards and buttons

      // ✅ Free previous image memory before setting a new snapshot
      if (imageView.image() != null) {
        imageView.image().cancel() // ✅ This ensures the previous image gets released
      }

      imageView.image = snapshot // ✅ Assign the new snapshot to ImageView

      // ✅ Apply blur effect AFTER assigning image
      imageView.effect = new BoxBlur {
        width = 10
        height = 10
        iterations = 3
      }

      println("✅ Snapshot captured & blurred!")
    }
  }


  def openModal(fadeIn: Boolean = false): Unit = {
    updateSnapshot()
    modal.visible = true

    if (fadeIn) {
      val fadeInTransition = new FadeTransition {
        node = modal
        fromValue = 0
        toValue = 1
        duration = Duration(500)
        onFinished = _ => onOpenFinish()
      }
      fadeInTransition.play()
    }
  }

  def closeModal(fadeOut: Boolean = false): Unit = {
    if (fadeOut) {
      val fadeOutTransition = new FadeTransition {
        node = modal
        fromValue = 1
        toValue = 0
        duration = Duration(500)
        onFinished = _ => {
          modal.visible = false
          imageView.image = null
          onCloseFinish()
        }
      }
      fadeOutTransition.play()
    } else {
      modal.visible = false
      imageView.image = null
      onCloseFinish()
    }
  }

  def toggleModal(): Unit = {
    if (modal.visible.value) {
      closeModal()
    } else {
      openModal()
    }
  }

  val modal = new StackPane {
    children = Seq(
      imageView,  // ✅ Add blurred snapshot as a background
      overlay,
      modalBox
    )
    visible = false
    alignment = Pos.Center
    prefWidth = windowWidth
    prefHeight = windowHeight
  }

}