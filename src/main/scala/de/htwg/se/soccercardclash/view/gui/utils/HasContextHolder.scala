package de.htwg.se.soccercardclash.view.gui.utils
import de.htwg.se.soccercardclash.controller.IGameContextHolder
trait HasContextHolder {
  def getContextHolder: IGameContextHolder
}
