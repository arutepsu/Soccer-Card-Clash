package de.htwg.se.soccercardclash.util

trait ObservableEvent

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

  def notifyObservers(e: ObservableEvent): Unit = {
    if (subscribers.isEmpty) {
      return
    }

    val distinctSubscribers = subscribers.distinct

    distinctSubscribers.zipWithIndex.foreach { case (observer, index) =>
      observer.update(e)
    }
  }



  def removeAllObservers(): Unit = {
    subscribers = Vector()
  }

  def printAllObservers(): Unit = {
    if (subscribers.isEmpty) {
    } else {
      subscribers.zipWithIndex.foreach { case (observer, index) =>
        println(s"[$index] ${observer.getClass.getSimpleName} @ ${observer.hashCode()}")
      }
    }
  }
}
