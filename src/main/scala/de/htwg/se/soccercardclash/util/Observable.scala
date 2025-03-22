package de.htwg.se.soccercardclash.util

class ObservableEvent {}

class Observable {
  var subscribers: Vector[Observer] = Vector()

  def add(s: Observer): Unit = {
    if (!subscribers.contains(s)) {
      subscribers = subscribers :+ s
    } else {
      println(s"⚠️ DEBUG: Observer ${s.getClass.getSimpleName} already registered, skipping.")
    }
    println(s"📋 Observers List AFTER adding: ${subscribers.map(_.getClass.getSimpleName).mkString(", ")}")
  }

  def remove(s: Observer): Unit = {
    println(s"❌ DEBUG: Removing observer: ${s.getClass.getSimpleName}")
    subscribers = subscribers.filterNot(o => o == s)
    println(s"📋 Observers List AFTER removal: ${subscribers.map(_.getClass.getSimpleName).mkString(", ")}")
  }

  def notifyObservers(e: ObservableEvent = ObservableEvent()): Unit = {
    if (subscribers.isEmpty) {
      println(s"⚠️ WARNING: No observers registered! Skipping event: $e")
      return
    }

    println(s"🔄 DEBUG: Notifying ${subscribers.distinct.size} unique observers.") // ✅ Only unique observers

    subscribers.distinct.foreach(_.update(e)) // ✅ Ensures no duplicate events
  }


  def removeAllObservers(): Unit = {
    println(s"🗑️ DEBUG: Removing ALL observers (${subscribers.size})")
    subscribers = Vector() // ✅ Completely clears all observers
    println(s"📋 Observers List AFTER clearing: ${subscribers.map(_.getClass.getSimpleName).mkString(", ")}")
  }

}
