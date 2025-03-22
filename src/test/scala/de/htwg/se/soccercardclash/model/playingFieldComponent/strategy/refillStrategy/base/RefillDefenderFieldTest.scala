//package model.playingFieldComponent.strategy.refillStrategy.base
//
//import model.cardComponent.ICard
//import model.playerComponent.IPlayer
//import model.playingFiledComponent.manager.IDataManager
//import model.playingFiledComponent.dataStructure.{HandCardsQueue, IHandCardsQueue}
//import model.playingFiledComponent.strategy.refillStrategy.base.RefillDefenderField
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.Mockito.*
//
//class RefillDefenderFieldTest extends AnyFlatSpec with Matchers with MockitoSugar {
//
//  // Mocks
//  val mockFieldState: IDataManager = mock[IDataManager]
//  val mockDefender: IPlayer = mock[IPlayer]
//  val mockHand: IHandCardsQueue = mock[IHandCardsQueue]
//  val mockDefenderField: List[ICard] = List(mock[ICard], mock[ICard], mock[ICard])
//  val mockGoalkeeper: Option[ICard] = Some(mock[ICard])
//
//  "RefillDefenderField" should "completely refill defender field when there are no defenders or goalkeeper" in {
//    // Prepare mocks for the case of empty field and no goalkeeper
//    when(mockFieldState.getPlayerDefenders(mockDefender)).thenReturn(List())
//    when(mockFieldState.getPlayerGoalkeeper(mockDefender)).thenReturn(None)
//    when(mockFieldState.getPlayerHand(mockDefender)).thenReturn(new HandCardsQueue(List(mock[ICard], mock[ICard], mock[ICard], mock[ICard])))
//
//    val refill = new RefillDefenderField
//    refill.refill(mockFieldState, mockDefender)
//
//    // Verify that the methods were called correctly
//    verify(mockFieldState).setPlayerDefenders(mockDefender, List(mock[ICard], mock[ICard], mock[ICard], mock[ICard]))
//    verify(mockFieldState).setPlayerGoalkeeper(mockDefender, Some(mock[ICard]))  // Ensure goalkeeper set
//  }
//
//  it should "partially refill defender field when there are fewer than 3 defenders and a goalkeeper exists" in {
//    // Prepare mocks for partially filled defender field
//    when(mockFieldState.getPlayerDefenders(mockDefender)).thenReturn(mockDefenderField.take(2))  // Only 2 defenders
//    when(mockFieldState.getPlayerGoalkeeper(mockDefender)).thenReturn(mockGoalkeeper)
//    when(mockFieldState.getPlayerHand(mockDefender)).thenReturn(new HandCardsQueue(List(mock[ICard])))
//
//    val refill = new RefillDefenderField
//    refill.refill(mockFieldState, mockDefender)
//
//    // Verify the updated defender field (3 defenders and goalkeeper should remain)
//    verify(mockFieldState).setPlayerDefenders(mockDefender, mockDefenderField.take(2) :+ mock[ICard])  // 2 defenders + 1 new
//    verify(mockFieldState).setPlayerGoalkeeper(mockDefender, mockGoalkeeper)
//  }
//
//  it should "correctly update goalkeeper if a better card is available" in {
//    // Set up defenders and goalkeeper
//    val higherDefender = mock[ICard]
//    val lowerDefender = mock[ICard]
//    when(higherDefender.valueToInt).thenReturn(10)
//    when(lowerDefender.valueToInt).thenReturn(5)
//
//    when(mockFieldState.getPlayerDefenders(mockDefender)).thenReturn(List(higherDefender, lowerDefender))
//    when(mockFieldState.getPlayerGoalkeeper(mockDefender)).thenReturn(Some(mock[ICard]))  // Existing goalkeeper
//
//    val refill = new RefillDefenderField
//    refill.refill(mockFieldState, mockDefender)
//
//    // Verify that the goalkeeper was replaced by the higher defender
//    verify(mockFieldState).setPlayerGoalkeeper(mockDefender, Some(higherDefender))
//  }
//
//  it should "handle the case where the hand has fewer than needed cards to refill" in {
//    // If the hand has fewer than the number of needed cards to complete the refill, handle gracefully
//    when(mockFieldState.getPlayerDefenders(mockDefender)).thenReturn(List())  // Empty defender field
//    when(mockFieldState.getPlayerGoalkeeper(mockDefender)).thenReturn(None)
//    when(mockFieldState.getPlayerHand(mockDefender)).thenReturn(new HandCardsQueue(List(mock[ICard])))  // Only 1 card available
//
//    val refill = new RefillDefenderField
//    refill.refill(mockFieldState, mockDefender)
//
//    // The field should be refilled with what is available
//    verify(mockFieldState).setPlayerDefenders(mockDefender, List(mock[ICard]))
//  }
//}