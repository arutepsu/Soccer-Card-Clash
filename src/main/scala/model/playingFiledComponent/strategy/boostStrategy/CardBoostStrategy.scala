package model.playingFiledComponent.strategy.boostStrategy
import model.cardComponent.ICard

class CardBoostStrategy(boostType: String) extends BoostStrategy {
  override def boost(card: ICard): Unit = {
    if (card.wasBoosted) {
      println(s"⚠️ Boost prevented! ${card} has already been boosted once.")
      return
    }

    val boostValue = card.getBoostingPolicies
    card.setAdditionalValue(boostValue)

    println(s"✅ ${boostType} Boosted: $card (Boosted by: $boostValue)")
  }
}
