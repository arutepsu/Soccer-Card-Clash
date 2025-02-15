//package view
//
//import model.cardComponent.Deck
//import model.playerComponent.Player
//import model.playingFiledComponent.PlayingField
//import model.cardComponent.Card
//import util.Observable
//import util.Observer
//import scala.io.StdIn.readLine
//import scala.collection.mutable
//import scala.util.Try
//import controller.IController
//import scala.io.StdIn.readLine
//import model.playerComponent.Player
//import controller.BaseControllerImplementation.Controller
//
//
//class Tui(controller: Controller) extends Observer {
//
//  // ✅ Register as an Observer
//  controller.add(this)
//
//  /** Starts the TUI loop */
//  def start(): Unit = {
//    println("Welcome to the Soccer Card Game (TUI Mode)!")
//
//    // ✅ Ensure the game is initialized
//    controller.startGame()
//
//    println("Type 'attack', 'undo', 'redo', 'swap', or 'exit' to quit.")
//    update// Show initial game state
//
//    var running = true
//    while (running) {
//      val input = readLine().trim.toLowerCase
//      input match {
//        case "attack" =>
//          val defenderPosition = controller.selectDefenderPosition()
//          if (defenderPosition == -1) {
//            println("Attacking the goalkeeper.")
//          } else if (defenderPosition == -2) {
//            println("Select a defender position to attack:")
//            val pos = readLine().toIntOption.getOrElse(-1)
//            if (pos >= 1) controller.executeAttackCommand(pos - 1)
//            else println("❌ Invalid input.")
//          }
//
//        case "undo" =>
//          controller.undo()
//          println("🔄 Undo executed!")
//
//        case "redo" =>
//          controller.redo()
//          println("🔁 Redo executed!")
//
//        case "swap" =>
//          println("Select a card index to swap from attacker's hand:")
//          val index = readLine().toIntOption.getOrElse(-1)
//          if (index >= 0) {
//            controller.swapAttackerCard(index)
//            println(s"🔄 Swapped attacker card at index: $index")
//          } else {
//            println("❌ Invalid card index.")
//          }
//
//        case "exit" =>
//          println("👋 Exiting TUI...")
//          running = false
//
//        case _ =>
//          println("❌ Invalid command. Type 'attack', 'undo', 'redo', 'swap', or 'exit'.")
//      }
//    }
//  }
//
//  /** ✅ Observer Pattern: Refresh the game state whenever notified */
//  override def update: Unit = {
//    println("================================")
//    println(displayGameState())
//  }
//
//  /** Displays the current game state */
//  def displayGameState(): String = {
//    val pf = controller.getPlayingField
//    val attacker = pf.getAttacker
//    val defender = pf.getDefender
//    val attackingCard = pf.getAttackingCard
//    val attackerHand = pf.getHand(attacker).mkString(", ")
//    val defenderHand = pf.getHand(defender).mkString(", ")
//    val defenderField = pf.playerDefenders(defender).mkString(", ")
//    val defenderGoalkeeper = pf.getGoalkeeper(defender)
//
//    s"""🏆 **Current Game State**
//       |--------------------------------
//       |⚔️  Attacker: ${attacker.name}
//       |   🎴 Hand: $attackerHand
//       |   🃏 Attacking Card: $attackingCard
//       |
//       |🛡️  Defender: ${defender.name}
//       |   🎴 Hand: $defenderHand
//       |   🏟️ Field: $defenderField
//       |   🥅 Goalkeeper: $defenderGoalkeeper
//       |--------------------------------
//       |""".stripMargin
//  }
//}
//class Tui(controller: Controller) extends Observer {
//
//  controller.add(this) // TUI subscribes to Controller updates
//
//  /** Starts the TUI loop */
//  def start(): Unit = {
//    println("Starting the game in TUI mode...")
//    println("Type 'attack', 'undo', 'redo', or 'exit' to quit.")
//    update // Show initial game state
//
//    var running = true
//    while (running) {
//      val input = scala.io.StdIn.readLine().trim.toLowerCase
//      input match {
//        case "attack" =>
//          val defenderPosition = controller.selectDefenderPosition()
//          if (defenderPosition == -1) {
//            println("Attacking the goalkeeper.")
//          } else if (defenderPosition == -2) {
//            println("Select a defender position to attack.")
//            val pos = scala.io.StdIn.readLine().toIntOption.getOrElse(-1)
//            if (pos >= 1) controller.executeAttackCommand(pos - 1)
//            else println("Invalid input.")
//          }
//        case "undo" =>
//          controller.undo()
//          println("Undo executed!")
//
//        case "redo" =>
//          controller.redo()
//          println("Redo executed!")
//
//        case "exit" =>
//          println("Exiting TUI...")
//          running = false
//
//        case _ =>
//          println("Invalid command. Type 'attack', 'undo', 'redo', or 'exit'.")
//      }
//    }
//  }
//
//  /** Updates the game state */
//  override def update: Unit = {
//    println("================================")
//    println(displayGameState())
//  }
//
//  /** Displays the current game state */
//  def displayGameState(): String = {
//    val pf = controller.getPlayingField
//    val attacker = pf.getAttacker
//    val defender = pf.getDefender
//    val attackingCard = pf.getAttackingCard
//    val attackerHand = pf.getHand(attacker).mkString(", ")
//    val defenderHand = pf.getHand(defender).mkString(", ")
//    val defenderField = pf.playerDefenders(defender).mkString(", ")
//    val defenderGoalkeeper = pf.getGoalkeeper(defender)
//    val attackerHandCount = pf.getHand(attacker).size
//    val defenderHandCount = pf.getHand(defender).size
//
//    s"""Attacker: ${attacker.name} (Cards in hand: $attackerHandCount)
//       |Attacker's Hand: $attackerHand
//       |Defender's Hand: $defenderHand
//       |Attacking Card: $attackingCard
//       |
//       |Defender: ${defender.name} (Cards in hand: $defenderHandCount)
//       |Defender's Field: $defenderField
//       |Defender's Goalkeeper: $defenderGoalkeeper
//       |""".stripMargin
//  }
//}


//class Tui(controller: Controller) extends Observer {
//
//  controller.add(this)  // TUI subscribes to Controller updates
//
//  def update: Unit = {
//    println("================================")
//    println(displayGameState())
//    handleUserInput()
//  }
//
//  def displayGameState(): String = {
//    val pf = controller.getPlayingField
//    val attacker = pf.getAttacker
//    val defender = pf.getDefender
//    val attackingCard = pf.getAttackingCard
//    val attackerHand = pf.getHand(attacker).mkString(", ")
//    val defenderHand = pf.getHand(defender).mkString(", ")
//    val defenderField = pf.playerDefenders(defender).mkString(", ")
//
//    val defenderGoalkeeper = pf.getGoalkeeper(defender)
//    val attackerHandCount = pf.getHand(attacker).size
//    val defenderHandCount = pf.getHand(defender).size
//
//    s"""Attacker: ${attacker.name} (Cards in hand: $attackerHandCount)
//       |Attacker's Hand: $attackerHand
//       |Defender's Hand: $defenderHand
//       |Attacking Card: $attackingCard
//       |
//       |Defender: ${defender.name} (Cards in hand: $defenderHandCount)
//       |Defender's Field: $defenderField
//       |Defender's Goalkeeper: $defenderGoalkeeper
//       |""".stripMargin
//  }
//
//  def handleUserInput(): Unit = {
//    println("Enter a command (attack, undo, redo): ")
//    val input = scala.io.StdIn.readLine().trim.toLowerCase
//    input match {
//      case "attack" =>
//        val defenderPosition = controller.selectDefenderPosition()
//        if (defenderPosition == -1) {
//          println("Attacking the goalkeeper.")
//        } else if (defenderPosition == -2) {
//          println("Select a defender position to attack.")
//          val pos = scala.io.StdIn.readLine().toIntOption.getOrElse(-1)
//          if (pos >= 1) controller.executeAttackCommand(pos - 1)
//          else println("Invalid input.")
//        }
//      case "undo" => controller.undo()
//      println("undo!!!!!!")
//      case "redo" => controller.redo()
//      case _ => println("Invalid command.")
//    }
//  }
//}

//class TUI(controller: Controller) extends Observer {
//
//  controller.add(this)  // TUI subscribes to GameController updates
//
//  def update: Unit = {
//    println("================================")
//    println(displayGameState())
//  }
//
//  def displayGameState(): String = {
//    val pf = controller.getPlayingField
//    val attacker = pf.getAttacker
//    val defender = pf.getDefender
//    val attackingCard = pf.getAttackingCard
//    val attackerHand = pf.getHand(attacker).mkString(", ")
//    val defenderHand = pf.getHand(defender).mkString(", ")
//    val defenderField = pf.playerDefenders(defender).mkString(", ")
//
//    val defenderGoalkeeper = pf.getGoalkeeper(defender)
//    val attackerHandCount = pf.getHand(attacker).size
//    val defenderHandCount = pf.getHand(defender).size
//
//    s"""Attacker: ${attacker.name} (Cards in hand: $attackerHandCount)
//       |Attacker's Hand: $attackerHand
//       |Defender's Hand: $defenderHand
//       |Attacking Card: $attackingCard
//       |
//       |Defender: ${defender.name} (Cards in hand: $defenderHandCount)
//       |Defender's Field: $defenderField
//       |Defender's Goalkeeper: $defenderGoalkeeper
//       |""".stripMargin
//  }
//package view
//
//import model.cardComponent.Deck
//import model.playerComponent.Player
//import model.playingFiledComponent.PlayingField
//import model.cardComponent.Card
//import util.Observable
//import util.Observer
//import scala.io.StdIn.readLine
//import scala.collection.mutable
//import scala.util.Try
//import controller.IController
//
//class Tui(controller: IController) extends Observer {
//
//  // ✅ Register as an Observer
//  controller.add(this)
//
//  /** Starts the TUI menu */
//  def start(): Unit = {
//    println("Welcome to the Soccer Card Game (TUI Mode)!")
//
//    var running = true
//    while (running) {
//      println("\n📜 **Main Menu**")
//      println("1️⃣ New Game")
//      println("2️⃣ Load Game (Not Implemented)")
//      println("3️⃣ Exit")
//      print("🔹 Choose an option: ")
//
//      val input = readLine().trim.toLowerCase
//      input match {
//        case "1" | "new game" =>
//          setupNewGame()
//        case "2" | "load game" =>
//          println("⚠️ Load game is not implemented yet.")
//        case "3" | "exit" =>
//          println("👋 Exiting game...")
//          running = false
//        case _ =>
//          println("❌ Invalid option. Please type '1', '2', or '3'.")
//      }
//    }
//  }
//
//  /** ✅ Handles new game setup, waits for GUI to start the game */
//  private def setupNewGame(): Unit = {
//    println("\n📝 Enter names for the two players:")
//
//    print("🎮 Player 1 name: ")
//    val player1Name = readLine().trim
//
//    print("🎮 Player 2 name: ")
//    val player2Name = readLine().trim
//
//    if (player1Name.isEmpty || player2Name.isEmpty) {
//      println("❌ Both players must have a name.")
//      return
//    }
//
//    // ✅ Set player names
//    controller.setPlayerName(1, player1Name)
//    controller.setPlayerName(2, player2Name)
//
//    println(s"\n✅ Player names set: $player1Name vs $player2Name")
//    println("⏳ Waiting for GUI to start the game...")
//
//    // ✅ Wait until `startGame()` is triggered from the GUI
////    while (!controller.isGameStarted) {
////      Thread.sleep(500)
////    }
//
//    // ✅ Once the game starts, transition into gameplay mode
//    println("🎉 Game started!")
//    println("Type 'attack', 'undo', 'redo', 'swap', 'boost', or 'exit' to quit.")
//    update
//
//    gameLoop() // ✅ Proceed to gameplay loop
//  }
//
//  /** ✅ Main game loop */
//  private def gameLoop(): Unit = {
//    var running = true
//    while (running) {
//      val input = readLine().trim.toLowerCase
//      input match {
//        case "attack" =>
//          val defenderPosition = controller.selectDefenderPosition()
//          if (defenderPosition == -1) {
//            println("Attacking the goalkeeper.")
//          } else if (defenderPosition == -2) {
//            println("Select a defender position to attack:")
//            val pos = readLine().toIntOption.getOrElse(-1)
//            if (pos >= 1) controller.executeAttackCommand(pos - 1)
//            else println("❌ Invalid input.")
//          }
//
//        case "undo" =>
//          controller.undo()
//          println("🔄 Undo executed!")
//
//        case "redo" =>
//          controller.redo()
//          println("🔁 Redo executed!")
//
//        case "swap" =>
//          println("Select a card index to swap from attacker's hand:")
//          val index = readLine().toIntOption.getOrElse(-1)
//          if (index >= 0) {
//            controller.swapAttackerCard(index)
//            println(s"🔄 Swapped attacker card at index: $index")
//          } else {
//            println("❌ Invalid card index.")
//          }
//        case "boost" =>
//          println("Select a card index to swap from attacker's hand:")
//          val index = readLine().toIntOption.getOrElse(-1)
//          if (index >= 0) {
//            controller.boostDefender(index)
//            println(s"🔄 Boosted defender at index: $index")
//          } else {
//            println("❌ Invalid card index.")
//          }
//
//        case "exit" =>
//          println("👋 Exiting game...")
//          running = false
//
//        case _ =>
//          println("❌ Invalid command. Type 'attack', 'undo', 'redo', 'swap', or 'exit'.")
//      }
//    }
//  }
//
//  /** ✅ Observer Pattern: Refresh the game state whenever notified */
//  override def update: Unit = {
//    println("================================")
//    println(displayGameState())
//  }
//
//  /** Displays the current game state */
//  def displayGameState(): String = {
//    val pf = controller.getPlayingField
//    val attacker = pf.getAttacker
//    val defender = pf.getDefender
//    val attackingCard = pf.getAttackingCard
//    val attackerHand = pf.getHand(attacker).mkString(", ")
//    val defenderHand = pf.getHand(defender).mkString(", ")
//    val defenderField = pf.fieldState.getDefenders(defender).mkString(", ")
//    val defenderGoalkeeper = pf.fieldState.getGoalkeeper(defender)
//
//    s"""🏆 **Current Game State**
//       |--------------------------------
//       |⚔️  Attacker: ${attacker.name}
//       |   🎴 Hand: $attackerHand
//       |   🃏 Attacking Card: $attackingCard
//       |
//       |🛡️  Defender: ${defender.name}
//       |   🎴 Hand: $defenderHand
//       |   🏟️ Field: $defenderField
//       |   🥅 Goalkeeper: $defenderGoalkeeper
//       |--------------------------------
//       |""".stripMargin
//  }
//}
//
//
