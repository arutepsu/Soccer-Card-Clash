package controller.command

trait ICommand {
  def doStep(): Unit

  def undoStep(): Unit

  def redoStep(): Unit
}