package util

import util.GameEvent

trait GameObserver{
  def update(event: GameEvent): Unit
}
