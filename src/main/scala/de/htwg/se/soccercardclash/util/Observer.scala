package de.htwg.se.soccercardclash.util

trait Observer {
  def update(e: ObservableEvent): Unit
}