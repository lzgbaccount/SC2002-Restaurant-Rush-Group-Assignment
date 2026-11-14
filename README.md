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
