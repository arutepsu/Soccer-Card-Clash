package view.tui
import scala.util.{Success, Try}

class Parser {

  /** ✅ Parses an attack command (requires defender index) */
  def parseAttack(input: String, maxDefenders: Int): Option[Int] = {
    Try(input.toInt) match {
      case Success(index) if index >= 0 && index < maxDefenders =>
        Some(index)
      case _ =>
        println(s"❌ Invalid defender index! Must be between 0 and ${maxDefenders - 1}.")
        None
    }
  }

  /** ✅ Parses a swap command (requires card index) */
  def parseSwap(input: String, handSize: Int): Option[Int] = {
    Try(input.toInt) match {
      case Success(index) if index >= 0 && index < handSize =>
        Some(index)
      case _ =>
        println(s"❌ Invalid swap index! Must be between 0 and ${handSize - 1}.")
        None
    }
  }

  /** ✅ Parses a boost command (requires defender index) */
  def parseBoost(input: String, maxDefenders: Int): Option[Int] = {
    Try(input.toInt) match {
      case Success(index) if index >= 0 && index < maxDefenders =>
        Some(index)
      case _ =>
        println(s"❌ Invalid boost index! Must be between 0 and ${maxDefenders - 1}.")
        None
    }
  }


  /** ✅ Parses player names (ensures non-empty input) */
  def parsePlayerName(input: String): Option[String] = {
    val name = input.trim
    if (name.nonEmpty) Some(name)
    else {
      println("❌ Player name cannot be empty!")
      None
    }
  }

  /** ✅ Parses game-related commands */
  def parseCommand(input: String): Option[TuiKeys] = {
    TuiKeys.values.find(_.key.equalsIgnoreCase(input)).orElse {
      println(s"❌ Unknown command: '$input'. Try again.")
      None
    }
  }
}
