package util
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import util._
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ObservableTest extends AnyFlatSpec with Matchers {

  "Observable" should "add an observer" in {
    val observable = new Observable
    val observer = new Observer {
      def update(e: ObservableEvent): Unit = {}
    }

    observable.add(observer)

    observable.subscribers should contain(observer) // Ensure observer is added
  }

  it should "not add the same observer twice" in {
    val observable = new Observable
    val observer = new Observer {
      def update(e: ObservableEvent): Unit = {}
    }

    observable.add(observer)
    observable.add(observer) // Try adding the same observer again

    observable.subscribers.size shouldBe 1 // Only one instance should be added
  }

  it should "remove an observer" in {
    val observable = new Observable
    val observer = new Observer {
      def update(e: ObservableEvent): Unit = {}
    }

    observable.add(observer)
    observable.remove(observer)

    observable.subscribers should not contain observer // Ensure observer is removed
  }

  it should "notify all registered observers" in {
    val observable = new Observable
    var updateCalled = false

    // Create an observer that sets a flag when updated
    val observer = new Observer {
      def update(e: ObservableEvent): Unit = {
        updateCalled = true
      }
    }

    observable.add(observer)
    observable.notifyObservers()

    updateCalled shouldBe true // Ensure the observer was notified
  }

  it should "notify only unique observers" in {
    val observable = new Observable
    var updateCount = 0

    // Create an observer that increments a counter when updated
    val observer = new Observer {
      def update(e: ObservableEvent): Unit = {
        updateCount += 1
      }
    }

    observable.add(observer)
    observable.add(observer) // Add the same observer twice
    observable.notifyObservers()

    updateCount shouldBe 1 // Ensure observer is only notified once
  }

  it should "remove all observers" in {
    val observable = new Observable
    val observer = new Observer {
      def update(e: ObservableEvent): Unit = {}
    }

    observable.add(observer)
    observable.removeAllObservers()

    observable.subscribers shouldBe empty // Ensure no observers are left
  }

  it should "notify no observers if none are registered" in {
    val observable = new Observable

    // Capture the output from println )
    val out = new ByteArrayOutputStream()

    // Use Console.withOut to redirect output
    Console.withOut(new PrintStream(out)) {
      observable.notifyObservers()
    }

    // Now you can check the output
    out.toString should include("⚠️ WARNING: No observers registered! Skipping event:")

    observable.notifyObservers()

    out.toString should include ("⚠️ WARNING: No observers registered! Skipping event:")
  }
}
