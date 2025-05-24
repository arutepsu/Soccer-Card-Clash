package de.htwg.se.soccercardclash.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.soccercardclash.util._
import java.io.ByteArrayOutputStream
import java.io.PrintStream

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyFlatSpec with Matchers {

  behavior of "Observable"

  it should "notify added observers when notifyObservers is called" in {
    // Step 1: Create a concrete ObservableEvent
    case object DummyEvent extends ObservableEvent

    // Step 2: Track if update was called
    var updateCalled = false

    // Step 3: Create an observer that flips the flag
    val observer = new Observer {
      override def update(e: ObservableEvent): Unit = {
        updateCalled = true
      }
    }

    // Step 4: Create observable and test
    val observable = new Observable
    observable.add(observer)
    observable.notifyObservers(DummyEvent)

    // Step 5: Assert
    updateCalled shouldBe true
  }
}
