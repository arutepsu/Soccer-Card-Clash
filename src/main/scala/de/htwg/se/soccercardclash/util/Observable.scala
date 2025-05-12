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
    subscribers.distinct.foreach(_.update(e))
  }

  def removeAllObservers(): Unit = {
    subscribers = Vector()
  }

  def printAllObservers(): Unit = {
    println("📋 All Observers (including duplicates):")
    if (subscribers.isEmpty) {
      println("  (No observers registered)")
    } else {
      subscribers.zipWithIndex.foreach { case (observer, index) =>
        println(s"  [$index] ${observer.getClass.getSimpleName} @ ${observer.hashCode()}")
      }
    }
  }


}
