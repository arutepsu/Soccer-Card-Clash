//package model.gameComponent.factory
//
//import model.gameComponent.state.IGameState
//import model.playingFiledComponent.factory.PlayingFieldDeserializer
//import model.playerComponent.factory.PlayerDeserializer
//import model.cardComponent.factory.CardDeserializer
//import model.playingFiledComponent.dataStructure.{HandCardsQueueDeserializer, IHandCardsQueueFactory}
//import model.gameComponent.state.IGameStateFactory
//import model.playerComponent.IPlayer
//import model.playingFiledComponent.IPlayingField
//import model.cardComponent.ICard
//import model.playingFiledComponent.dataStructure.IHandCardsQueue
//import model.gameComponent.state.GameState
//import org.mockito.ArgumentMatchers.{any, anyInt}
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//import org.mockito.Mockito._
//import org.scalatestplus.mockito.MockitoSugar
//import play.api.libs.json._
//import model.playerComponent.playerAction.*
//import scala.xml.Elem
//
//class GameDeserializerSpec extends AnyWordSpec with Matchers with MockitoSugar {
//  // Mocks
//  val mockField = mock[IPlayingField]
//  val mockPlayer1 = mock[IPlayer]
//  val mockPlayer2 = mock[IPlayer]
//  val mockFactory = mock[IGameStateFactory]
//  val mockCard = mock[ICard]
//  val mockHand1 = mock[IHandCardsQueue]
//  val mockHand2 = mock[IHandCardsQueue]
//  val mockPlayingFieldDeserializer = mock[PlayingFieldDeserializer]
//  val mockPlayerDeserializer = mock[PlayerDeserializer]
//  val mockQueueDeserializer = mock[HandCardsQueueDeserializer]
//  val mockQueueFactory = mock[IHandCardsQueueFactory]
//  val mockCardDeserializer = mock[CardDeserializer]
//  val mockGameState = mock[IGameState]
//
//  when(mockPlayer1.getActionStates).thenReturn(Map(PlayerActionPolicies.Boost -> CanPerformAction(2)))
//  when(mockPlayer2.getActionStates).thenReturn(Map(PlayerActionPolicies.Boost -> OutOfActions))
//
//  val deserializer = new GameDeserializer(
//    mockFactory,
//    mockPlayingFieldDeserializer,
//    mockPlayerDeserializer,
//    mockQueueDeserializer,
//    mockQueueFactory,
//    mockCardDeserializer
//  )
//
//  "GameDeserializer" should {
//    "deserialize from valid JSON" in {
//      val json: JsObject = Json.obj(
//        "playingField" -> Json.obj(
//          "attacker" -> Json.obj("name" -> "Alice"),
//          "defender" -> Json.obj("name" -> "Bob"),
//          "scores" -> Json.obj(
//            "scorePlayer1" -> 5,
//            "scorePlayer2" -> 3
//          )
//        ),
//        "player1Hand" -> Json.arr(Json.obj("value" -> "Two", "suit" -> "Hearts", "type" -> "Regular")),
//        "player2Hand" -> Json.arr(Json.obj("value" -> "Three", "suit" -> "Clubs", "type" -> "Regular")),
//        "player1Field" -> Json.arr(Json.obj("value" -> "Four", "suit" -> "Diamonds", "type" -> "Regular")),
//        "player2Field" -> Json.arr(Json.obj("value" -> "Five", "suit" -> "Spades", "type" -> "Regular"))
//      )
//
//      when(mockPlayingFieldDeserializer.fromJson(any())).thenReturn(mockField)
//      when(mockPlayerDeserializer.fromJson(any())).thenReturn(mockPlayer1, mockPlayer2)
//      when(mockQueueDeserializer.fromJson(any())).thenReturn(mockHand1, mockHand2)
//      when(mockCardDeserializer.fromJson(any())).thenReturn(mockCard)
//      when(mockFactory.create(
//        any[IPlayingField],
//        any[IPlayer],
//        any[IPlayer],
//        any[IHandCardsQueue],
//        any[IHandCardsQueue],
//        any[List[ICard]],
//        any[List[ICard]],
//        any[Option[ICard]],
//        any[Option[ICard]],
//        anyInt(),
//        anyInt()
//      )).thenReturn(mockGameState)
//
//      val result = deserializer.fromJson(json)
//
//      result shouldBe mockGameState
//    }
//
//    "deserialize from valid XML" in {
//      val xml: Elem =
//        <GameState>
//          <playingField>
//            <Attacker><Player><name>Alice</name></Player></Attacker>
//            <Defender><Player><name>Bob</name></Player></Defender>
//            <Scores>
//              <PlayerScores>
//                <ScorePlayer1>4</ScorePlayer1>
//                <ScorePlayer2>2</ScorePlayer2>
//              </PlayerScores>
//            </Scores>
//          </playingField>
//          <player1Hand>
//            <Card><value>Six</value><suit>Hearts</suit><type>Regular</type></Card>
//          </player1Hand>
//          <player2Hand>
//            <Card><value>Seven</value><suit>Clubs</suit><type>Regular</type></Card>
//          </player2Hand>
//          <player1Field>
//            <Card><value>Eight</value><suit>Diamonds</suit><type>Regular</type></Card>
//          </player1Field>
//          <player2Field>
//            <Card><value>Nine</value><suit>Spades</suit><type>Regular</type></Card>
//          </player2Field>
//        </GameState>
//
//      when(mockPlayingFieldDeserializer.fromXml(any())).thenReturn(mockField)
//      when(mockPlayerDeserializer.fromXml(any())).thenReturn(mockPlayer1, mockPlayer2)
//      when(mockCardDeserializer.fromXml(any())).thenReturn(mockCard)
//      when(mockQueueFactory.create(any())).thenReturn(mockHand1, mockHand2)
//      when(mockFactory.create(
//        any[IPlayingField],
//        any[IPlayer],
//        any[IPlayer],
//        any[IHandCardsQueue],
//        any[IHandCardsQueue],
//        any[List[ICard]],
//        any[List[ICard]],
//        any[Option[ICard]],
//        any[Option[ICard]],
//        anyInt(),
//        anyInt()
//      )).thenReturn(mockGameState)
//
//      val result = deserializer.fromXml(xml)
//
//      result shouldBe mockGameState
//    }
//  }
//}
