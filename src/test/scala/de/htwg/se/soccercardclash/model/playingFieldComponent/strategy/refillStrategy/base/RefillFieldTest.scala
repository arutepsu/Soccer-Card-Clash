//package model.playingFieldComponent.strategy.refillStrategy.base
//
//import model.cardComponent.ICard
//import model.playerComponent.IPlayer
//import model.playingFiledComponent.manager.IDataManager
//import model.playingFiledComponent.dataStructure.{HandCardsQueue, IHandCardsQueue}
//import model.playingFiledComponent.strategy.refillStrategy.base.RefillField
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.Mockito.*
//import org.mockito.ArgumentMatchers.any
//import scala.collection.mutable
//
//
//class RefillFieldTest extends AnyFlatSpec with Matchers with MockitoSugar {
//
//  // Mocks
//  val mockFieldState: IDataManager = mock[IDataManager]
//  val mockPlayer: IPlayer = mock[IPlayer]
//  val mockHand: IHandCardsQueue = mock[IHandCardsQueue]  // Change to IHandCardsQueue
//  val mockDefenderField: List[ICard] = List(mock[ICard], mock[ICard], mock[ICard])
//  val mockGoalkeeper: Option[ICard] = Some(mock[ICard])
//
//  "RefillField" should "completely refill defender field when there are no defenders or goalkeeper" in {
//    // Prepare mocks for empty field and no goalkeeper
//    when(mockFieldState.getPlayerDefenders(mockPlayer)).thenReturn(List())
//    when(mockFieldState.getPlayerGoalkeeper(mockPlayer)).thenReturn(None)
//
//    // Mock the behavior for getPlayerHand
//    when(mockFieldState.getPlayerHand(mockPlayer)).thenReturn(new HandCardsQueue(List(mock[ICard], mock[ICard], mock[ICard], mock[ICard])))  // Return HandCardsQueue
//
//    val refill = new RefillField
//    refill.refill(mockFieldState, mockPlayer, mutable.Queue(mock[ICard], mock[ICard], mock[ICard], mock[ICard]))  // Mocked Queue input
//
//    // Verify the methods were called correctly
//    verify(mockFieldState).setPlayerDefenders(mockPlayer, List(mock[ICard], mock[ICard], mock[ICard], mock[ICard]))
//    verify(mockFieldState).setPlayerGoalkeeper(mockPlayer, Some(mock[ICard]))  // Ensure goalkeeper set
//  }
//
//  it should "partially refill defender field when there are fewer than 3 defenders and a goalkeeper exists" in {
//    // Prepare mocks for partially filled defender field with a goalkeeper
//    when(mockFieldState.getPlayerDefenders(mockPlayer)).thenReturn(mockDefenderField.take(2))  // 2 defenders
//    when(mockFieldState.getPlayerGoalkeeper(mockPlayer)).thenReturn(mockGoalkeeper)
//    when(mockFieldState.getPlayerHand(mockPlayer)).thenReturn(new HandCardsQueue(List(mock[ICard])))
//
//    val refill = new RefillField
//    refill.refill(mockFieldState, mockPlayer, mutable.Queue(mock[ICard]))  // Mocked Queue input
//
//    // Verify the updated defender field (3 defenders and goalkeeper should remain)
//    verify(mockFieldState).setPlayerDefenders(mockPlayer, mockDefenderField.take(2) :+ mock[ICard])  // 2 defenders + 1 new
//    verify(mockFieldState).setPlayerGoalkeeper(mockPlayer, mockGoalkeeper)
//  }
//
//  it should "not refill if the defenders and goalkeeper are already filled" in {
//    // Prepare mocks for a full field
//    when(mockFieldState.getPlayerDefenders(mockPlayer)).thenReturn(mockDefenderField)  // Already 3 defenders
//    when(mockFieldState.getPlayerGoalkeeper(mockPlayer)).thenReturn(mockGoalkeeper)
//    when(mockFieldState.getPlayerHand(mockPlayer)).thenReturn(mockHand)
//
//    val refill = new RefillField
//    refill.refill(mockFieldState, mockPlayer, mutable.Queue(mock[ICard]))
//
//    // Ensure no field updates occur
//    verify(mockFieldState, never()).setPlayerDefenders(mockPlayer, any())
//    verify(mockFieldState, never()).setPlayerGoalkeeper(mockPlayer, any())
//  }
//
//  it should "handle the case where there are not enough cards in the hand" in {
//    // Set up with fewer than the required cards
//    when(mockFieldState.getPlayerDefenders(mockPlayer)).thenReturn(List())  // Empty field
//    when(mockFieldState.getPlayerGoalkeeper(mockPlayer)).thenReturn(None)
//    when(mockFieldState.getPlayerHand(mockPlayer)).thenReturn(new HandCardsQueue(List(mock[ICard])))  // Only 1 card in hand
//
//    val refill = new RefillField
//    refill.refill(mockFieldState, mockPlayer, mutable.Queue(mock[ICard]))
//
//    // Only available cards should be added to the field
//    verify(mockFieldState).setPlayerDefenders(mockPlayer, List(mock[ICard]))
//  }
//}