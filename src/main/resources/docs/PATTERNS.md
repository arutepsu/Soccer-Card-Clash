# Design Patterns Overview

---
## 🧠 GameState – Core Game Snapshot

### ☑️ Implemented Patterns: Memento • Delegation • State

#### 💡 Overview

The GameState is the heart of the game's logic — a snapshot of the current game round. It encapsulates player roles,
scores, and card allocations using immutable components.

Each modification (e.g., scoring a goal or switching roles) results in a new GameState,
preserving functional purity and enabling safe undo/redo.

## 🧩 UML Diagram
![State](src/main/resources/diagrams/png/GameState.png)

### 🔄 Memento Pattern

| **Role**     | **Class**           | **Description**                                               |
|--------------|---------------------|---------------------------------------------------------------|
| Originator   | `GameState`         | Captures/restores its internal state                          |
| Memento      | `GameStateMemento`  | Stores the state of roles, scores, and cards                  |
| Caretaker    | External (e.g. undo stack) | Holds mementos externally without modifying them       |

This supports undo/redo, replays, and safe time travel.

### 📦 Delegation Pattern
| **Responsibility** | **Delegated To** | **Example Methods**                             |
| ------------------ | ---------------- | ----------------------------------------------- |
| Role management    | `IRoles`         | `attacker()`, `defender()`, `switchRoles()`     |
| Score tracking     | `IScores`        | `scoreGoal()`, `newScore()`, `getScore()`       |
| Card handling      | `IGameCards`     | `getPlayerHand()`, `newPlayerDefenders()`, etc. |

### 🌀 State Pattern (Conceptual)
| **Element**       | **Description**                                                             |
| ----------------- | --------------------------------------------------------------------------- |
| `GameState`       | Represents a snapshot of the current game state                             |
| Transition Method | Transitions to a new state using `newGameCards`, `newScores`, `newRoles`    |
| Result            | Each call returns a new immutable `GameState`, simulating state transitions |

---
## 🧠 Action Strategies – Flexible Game Behavior
### ☑️ Implemented Pattern: Strategy
#### 💡 Overview
Different in-game actions (attacks, boosts, swaps) follow customizable rules — yet share a common interface. 
The IActionStrategy defines this interface, and each action type (e.g. SingleAttackStrategy, DefenderBoostStrategy) 
provides a concrete implementation.

This is a classic use of the Strategy Pattern, promoting open/closed design and behavioral decoupling.

## 🧩 UML Diagram
![Strategy](src/main/resources/diagrams/png/ActionStrategy.png)

### 🧠 Strategy Pattern

| **Component**                                                                                                                                          | **Role**                                                             |
| ------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------- |
| [`IActionStrategy`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/trigger/IActionStrategy.scala)                       | Strategy interface – defines the `execute()` method                  |
| [`SingleAttackStrategy`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/trigger/attack/SingleAttackStrategy.scala)      | Concrete strategy – performs a basic attack                          |
| [`DoubleAttackStrategy`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/trigger/attack/DoubleAttackStrategy.scala)      | Concrete strategy – performs a double-card attack                    |
| [`DefenderBoostStrategy`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/trigger/boost/DefenderBoostStrategy.scala)     | Concrete strategy – boosts a defender card                           |
| [`GoalkeeperBoostStrategy`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/trigger/boost/GoalkeeperBoostStrategy.scala) | Concrete strategy – boosts a goalkeeper card                         |
| [`RegularSwapStrategy`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/trigger/swap/RegularSwapStrategy.scala)          | Concrete strategy – swaps last hand card with chosen hand card index |
| [`ReverseSwapStrategy`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/trigger/swap/ReverseSwapStrategy.scala)          | Concrete strategy – reverts the last swap made (hand ↔ hand)         |


Each action is modular, testable, and swap-friendly at runtime, enabling both player and AI decision logic to reuse the
same interface.
---
## 🔗 Action Handler Chain – Processing Dynamic Actions
### ☑️ Implemented Pattern: Chain of Responsibility
#### 💡 Overview
In the game logic, actions like attacks, boosts, or swaps are handled dynamically based on their type. 
Instead of using massive if-else chains or type casting, we use the Chain of Responsibility Pattern
to pass an action through a chain of handlers.

Each handler checks if it can execute the given IActionStrategy. 
If it can't, it delegates to the next handler. This keeps the system extensible, decoupled, and open/closed.

## 🧩 UML Diagram
![ActionHandler](src/main/resources/diagrams/png/ActionHandler.png)

### 🔗 Chain of Responsibility Pattern
| **Component**                                                                                                                             | **Role**                                                                                       |
|-------------------------------------------------------------------------------------------------------------------------------------------| ---------------------------------------------------------------------------------------------- |
| [`IActionHandler`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/executor/IActionHandler.scala)           | Handler interface – defines `setNext` and `handle` methods                                     |
| [`ActionHandler`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/executor/ActionHandler.scala)             | Abstract base – holds reference to `nextHandler` and delegates if current handler can't handle |
| [`StrategyHandler`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/executor/StrategyHandler.scala)         | Concrete handler – uses a specific `StrategyExecutor` to process matching strategies           |
| [`StrategyExecutor`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/executor/StrategyExecutor.scala)       | Executes a specific strategy type after `canHandle()` check                                    |
| [`HandlerChainFactory`](src/main/scala/de/htwg/se/soccercardclash/model/gameComponent/action/strategy/executor/HandlerChainFactory.scala) | Creates the full handler chain from all known strategy handlers                                |

This pattern allows extensible support for new strategies without changing existing logic.

### 🔄 Example Flow
1. A player triggers an IActionStrategy (e.g., DoubleAttackStrategy)
2. The first StrategyHandler checks if its StrategyExecutor can handle it.
3. If not, it calls handleNext(...) -> passes the request to the next handler.
4. When a handler matches, it executes the strategy and returns the updated game state + events.


