package util

trait Observer {
  def update(e: ObservableEvent): Unit
}