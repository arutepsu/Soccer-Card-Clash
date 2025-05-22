package de.htwg.se.soccercardclash.view.gui.utils

import de.htwg.se.soccercardclash.view.gui.components.playerView.AIProfile


object AIRegistry {
  val aiProfiles = Seq(
    AIProfile("Taka", "Balanced and smart attacker.", "taka.jpg"),
    AIProfile("Bitstorm", "Aggressive, double-hitting striker.", "bitstrom.jpg"),
    AIProfile("Defendra", "Specialist in boosting defense.", "defendra.jpg"),
    AIProfile("MetaAI", "Adaptive, unpredictable decision-maker.", "meta.jpg")
  )

}
