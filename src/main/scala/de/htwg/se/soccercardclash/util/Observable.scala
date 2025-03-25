package de.htwg.se.soccercardclash.util

class ObservableEvent {}

class Observable {
  var subscribers: Vector[Observer] = Vector()

  def add(s: Observer): Unit = {
    if (!subscribers.contains(s)) {
      subscribers = subscribers :+ s
    }
  }

  def remove(s: Observer): Unit = {
    subscribers = subscribers.filterNot(o => o == s)
  }

  def notifyObservers(e: ObservableEvent = ObservableEvent()): Unit = {
    if (subscribers.isEmpty) {
      return
    }
    subscribers.distinct.foreach(_.update(e))
  }


  def removeAllObservers(): Unit = {
    subscribers = Vector()
  }
}
