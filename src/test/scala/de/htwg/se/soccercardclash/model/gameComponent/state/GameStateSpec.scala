package de.htwg.se.soccercardclash.model.gameComponent.state

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.{IPlayingField, manager}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.cardComponent.base.types.*
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json._
import scala.collection.mutable

class GameStateSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameState" should {
    "expose all values from memento and convert to JSON/XML" in {
      val mockField = mock[IPlayingField]
      val mockFactory = mock[IHandCardsQueueFactory]

      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      val gk1 = mock[ICard]
      val gk2 = mock[ICard]
      val d1 = mock[ICard]
      val d2 = mock[ICard]
      val handCard1 = mock[ICard]
      val handCard2 = mock[ICard]

      val queue1 = mock[IHandCardsQueue]
      val queue2 = mock[IHandCardsQueue]

      when(mockFactory.create(List(handCard1))).thenReturn(queue1)
      when(mockFactory.create(List(handCard2))).thenReturn(queue2)

      doReturn(mutable.Queue(handCard1)).when(queue1).getCards
      doReturn(mutable.Queue(handCard2)).when(queue2).getCards

      when(mockField.toXml).thenReturn(<playingField/>)
      when(mockField.toJson).thenReturn(Json.obj("field" -> "data"))

      when(d1.toXml).thenReturn(<card1/>)
      when(d2.toXml).thenReturn(<card2/>)
      when(handCard1.toXml).thenReturn(<h1/>)
      when(handCard2.toXml).thenReturn(<h2/>)
      when(gk1.toXml).thenReturn(<gk1/>)
      when(gk2.toXml).thenReturn(<gk2/>)

      when(d1.toJson).thenReturn(Json.obj("d1" -> 1))
      when(d2.toJson).thenReturn(Json.obj("d2" -> 2))
      when(handCard1.toJson).thenReturn(Json.obj("h1" -> 1))
      when(handCard2.toJson).thenReturn(Json.obj("h2" -> 2))
      when(gk1.toJson).thenReturn(Json.obj("gk1" -> true))
      when(gk2.toJson).thenReturn(Json.obj("gk2" -> true))

      val memento = Memento(
        player1,
        player2,
        List(d1),
        List(d2),
        Some(gk1),
        Some(gk2),
        List(handCard1),
        List(handCard2),
        3,
        4,
        Map.empty,
        Map.empty
      )

      val state = new GameState(mockField, memento, mockFactory)

      state.player1 shouldBe player1
      state.player2 shouldBe player2
      state.player1Hand shouldBe queue1
      state.player2Hand shouldBe queue2
      state.player1Defenders should contain only d1
      state.player2Defenders should contain only d2
      state.player1Goalkeeper should contain(gk1)
      state.player2Goalkeeper should contain(gk2)
      state.player1Score shouldBe 3
      state.player2Score shouldBe 4

      val xml = state.toXml
      (xml \ "player1Score").headOption.map(_.text.trim.toInt) shouldBe Some(3)
      (xml \ "player2Score").headOption.map(_.text.trim.toInt) shouldBe Some(4)

      val json = state.toJson
      (json \ "player1Score").as[Int] shouldBe 3
      (json \ "player2Score").as[Int] shouldBe 4
    }
  }
}
