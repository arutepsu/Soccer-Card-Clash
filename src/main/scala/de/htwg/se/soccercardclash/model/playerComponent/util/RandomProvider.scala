package de.htwg.se.soccercardclash.model.playerComponent.util

trait IRandomProvider {
  def nextInt(bound: Int): Int

  def between(min: Int, max: Int): Int
}

class RandomProvider(seed: Long) extends IRandomProvider {
  private val random = new scala.util.Random(seed)

  override def nextInt(bound: Int): Int = random.nextInt(bound)

  override def between(min: Int, max: Int): Int = random.between(min, max)
}
