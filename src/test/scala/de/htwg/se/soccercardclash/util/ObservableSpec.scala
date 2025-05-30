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
    case object DummyEvent extends ObservableEvent

    var updateCalled = false

    val observer = new Observer {
      override def update(e: ObservableEvent): Unit = {
        updateCalled = true
      }
    }

    val observable = new Observable
    observable.add(observer)
    observable.notifyObservers(DummyEvent)

    updateCalled shouldBe true
  }
  it should "remove an observer when remove is called" in {
    var updateCalled = false

    val observer = new Observer {
      override def update(e: ObservableEvent): Unit = {
        updateCalled = true
      }
    }

    val observable = new Observable
    observable.add(observer)
    observable.remove(observer)
    observable.notifyObservers(new ObservableEvent {}) // anonymous event

    updateCalled shouldBe false // observer should not be called
  }

  it should "do nothing when removing an observer that was never added" in {
    var updateCalled = false

    val observerAdded = new Observer {
      override def update(e: ObservableEvent): Unit = {
        updateCalled = true
      }
    }

    val observerNotAdded = new Observer {
      override def update(e: ObservableEvent): Unit = {
        fail("This observer should not be called")
      }
    }

    val observable = new Observable
    observable.add(observerAdded)
    observable.remove(observerNotAdded) // no effect

    observable.notifyObservers(new ObservableEvent {})
    updateCalled shouldBe true // only the added one was triggered
  }

}
