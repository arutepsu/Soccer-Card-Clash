package controller.command

trait ICommand {
  def doStep(): Boolean

  def undoStep(): Unit

  def redoStep(): Unit
}