# SC2002-Restaurant-Rush-Group-Assignment
source code for the 3 stages for the project 

# Restaurant Rush – Stage 1 → Stage 2: VIP and Critic customers

This file lists every difference between tags `stage-1` and `stage-2`: new files, existing files changed or deleted (with reasons), and the GitHub compare link.

**Compare:** https://github.com/lzgbaccount/SC2002-Restaurant-Rush-Group-Assignment/compare/stage-1...stage-2

## New files

| File | Purpose |
|---|---|
| `RegularCustomer.java` | Subclass of `Customer`: loses 8 satisfaction per waiting turn and pays full price. |
| `VIPCustomer.java` | Subclass of `Customer`: loses 5 per waiting turn and overrides `calculateBill()` to give 10% off. |
| `CriticCustomer.java` | Subclass of `Customer`: loses 12 per waiting turn and overrides `choosefood()` to always order Pasta. |

## Changed files

| File | What changed | Why |
|---|---|---|
| `Customer.java` | Became `abstract`, with a `protected` constructor. Added abstract `getTypeName()` and `getSatisfactionLoss()`, plus an overridable `calculateBill(MenuItem)` that returns the full price. Removed the fixed `LOSS_SATISFACTION` constant and calls getSatisfactionLoss()`instead.  placeOrder()` passes `calculateBill(item)` to the new `Order`. |  Different customer type must behave differently through its own object. Every type follows the same order and service flow, and only the parts that differ are overridden. |
| `Order.java` | Added `billCents`, set by a new constructor `Order(Customer, MenuItem, int billCents)`. `getBill() : float` was replaced by `getBillCents() : int`. | The bill now depends on the customer type, so it is worked out when the order is taken and stored. A later change can't alter a bill already placed. |
| `MenuItem.java` | `price : float` became `priceCents : int` (`getPriceCents()`). Added `static percentOf(cents, percent)`, which rounds to the nearest cent. | A 10% discount on a float price might produce rounding errors. so we changed it to a whole cents which prevent the error |
| `GameController.java` | `ARRIVAL_TURNS` and `nextCustomerId` were replaced by a fixed `arrivals` list of customer objects. Removed the `cents(Order)` helper, since payment now uses `getBillCents()`. `label()` now shows the customer type. The order message also shows the menu price when the bill differs from it. | Each arrival needs a specific type, and the required run must contain at least one VIP and one Critic among six arrivals. The turn logic itself is unchanged. |
| `ConsoleUI.java` | The welcome text now explains the three customer types. | So the player knows the new rules. |

## Deleted files

None.

## Unchanged

`ActionRejectedException`, `Cashier`, `Chef`, `CustomerStatus`, `GameResult`, `Main`, `OrderStatus`, `Result`, `Settings`, `Staff`, `Table`, `Waiter`, `WaiterAction`, `WaitingQueue`. The staff classes and the turn rules needed no changes for the new customer types.


# Restaurant Rush – Stage 2 → Stage 3: Host, ComboMeal and Happy Hour

This file lists every difference between tags `stage-2` and `stage-3`: new files, existing files changed or deleted (with reasons), and the GitHub compare link.

**Compare:** https://github.com/lzgbaccount/SC2002-Restaurant-Rush-Group-Assignment/compare/stage-2...stage-3

## New files

| File | Purpose |
|---|---|
| `Host.java` | Subclass of `Staff` with `seat(Customer, Table)`. The Host can seat one queued customer per turn, in addition to the Waiter's task. It reuses the once-per-turn rule (`canAct`, `waitTurn`) inherited from `Staff`. |
| `ComboMeal.java` | Subclass of `MenuItem`, composed of two `MenuItem`s (`first`, `second`). Prep units are the sum of the two, price is 90% of their combined price, rounded with `MenuItem.percentOf`. Because a combo *is* a `MenuItem`, `Order`, `Chef` and `Cashier` handle it with no changes. |
| `HappyHourPowerUp.java` |  Contains information on whether it has been used, how many turns remain, `activate()`, `applyTo(cents)` (20% off while active) and endTurn(). |

## Changed files

| File | What changed | Why |
|---|---|---|
| `GameController.java` | Added the `host` and `powerUp` fields. `startTurn()` now returns the start-of-turn events . Added `activateHappyHourPowerUp()`, `hostSeat()`, `hostWait()` and `requireHostFirst()`, so the Host acts before the Waiter. The old `seat()` was split into `seatNextCustomer()` and `tableForSeating()`, which the Host and the Waiter share. `endTurn()` also requires the Host to have acted, and counts down the power-up. | The controller enforces the turn order and owns the new staff member and power-up. Sharing the seating code means the Host and Waiter apply the same rules. |
| `Customer.java` | `choosefood()` returns the Salad + Pasta `ComboMeal` for every third customer (`COMBO_EVERY = 3`). `placeOrder()` takes the `HappyHourPowerUp` and locks `powerUp.applyTo(calculateBill(item))`. Added `recover(amount)`, capped at 100. | This is our combo rule. Applying Happy Hour to the result of `calculateBill()` gives the VIP discount first and Happy Hour second, as required, and locks the price when the order is taken. Waiting customers recover satisfaction during Happy Hour. |
| `Waiter.java` | `takeOrder(Customer)` became `takeOrder(Customer, HappyHourPowerUp)`. | Passes the power-up through to `placeOrder()`, so the bill is decided at the moment the order is taken. |
| `MenuItem.java` | Added a `protected MenuItem(String, int, int)` constructor. | `ComboMeal` can set its own name, price and prep units. The public constructor still only accepts the three menu dishes. |
| `ConsoleUI.java` | Added `powerUpPhase()` and `hostPhase()`. `seatMenu()` now takes a seating action, so the Host and Waiter share it. Added `describePowerUp()`, which shows Happy Hour's state and remaining turns, plus welcome text for the Host, combo and Happy Hour. | The new manager choices and the required display of Happy Hour's remaining duration. |
|  ||

## Deleted files

None.

## Unchanged

`ActionRejectedException`, `Cashier`, `Chef`, `CriticCustomer`, `CustomerStatus`, `GameResult`, `Main`, `OrderStatus`, `RegularCustomer`, `Result`, `Settings`, `Staff`, `Table`, `VIPCustomer`, `WaiterAction`, `WaitingQueue`. The Stage 2 customer subclasses, the Chef and the Cashier work with combos and Happy Hour as they are.

