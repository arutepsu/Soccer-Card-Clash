package view.tui
import controller.{Events, IController}

class AttackCommand(controller: IController) extends TuiCommand {
  override def execute(input: Option[String]): Unit = {
    input match {
      case Some(indexStr) =>
        try {
          val position = indexStr.toInt
          println(s"⚔️ Executing attack on position: $position")
          controller.executeSingleAttackCommand(position)
        } catch {
          case _: NumberFormatException =>
            println("❌ Error: Invalid attack format! Expected `:attack <position>`.")
        }
      case None =>
        println("❌ Error: Missing position! Use `:attack <position>`.")
    }
  }
}
