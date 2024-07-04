# Take it from the top

Solitavo is a Klondike Solitaire clone.

## The game world

The game is made up of a collection of 52 `Cards` organized on the screen into a `Tableau` of 7 piles of `Cards` where each pile holds one additional card starting from left to right. The final `Card` in each pile is initially turned over to reveal itself. The rest of the `Cards` are shuffled into the `Stock` face down. Next t the `Stock` exists the `Waste` where cards drawn from the `Stock` are placed and can be selected from. Lastly, there are 4 `Foundations` where revealed cards can be stacked by `Suit` starting from the lowest `Rank` to the highest.

### Cards

A `Card` is the basic game entity.
It is made up of the following elements:
- 2D Position
- 2D Size
- `Rank`
    - Ace
    - 2 - 10
    - Jack
    - Queen
    - King
- `Suit`
    - Heart (Red)
    - Diamond (Red)
    - Club (Black)
    - Spade (Black)
- Face-up Boolean

### Tableau

The `Tableau` comprises the primary playing field where cards are revealed and organized. `Cards` from each pile may only be moved if they are first turned face-up. Initially, only the final `Card` in the pile is face-up and face-down `Cards` may only be turned over when the `Card` above them is either moved to another pile in the `Tableau` or placed in a `Foundation`. Other `Cards` may be added to a pile either if the pile is empty and the `Card` or pile of `Cards` to be added starts with a `Card` of `Rank.King`, or if the top `Card` in the pile is both one `Rank` higher than the first `Card` in the pile to be added and the `Suit` is an alternate color.

`Cards` dragged from a pile are temporarily held in the `Hand`, and if not placed on a valid target (Either another suitable pile in the `Tableau` or onto a `Foundation`) they will be removed from the `Hand` and placed back in their original pile. If they are moved to a valid target and the new top `Card` in the pile is face down then that `Card` will be turned over.

### Stock

The `Stock` consists of all the other `Cards` that were not initially sorted into the `Tableau`. When the `Stock` is clicked on the top 1 or 3 (depending on preferred game settings) `Cards` are turned over and placed into the `Waste`. If the `Stock` runs out of `Cards`, clicking on it again will recycle all of the `Cards` in the `Waste` back into the `Stock` in the same order they were originally drawn from.

### Waste

The `Waste` consists of all `Cards` revealed from the `Stock`. Only the top `Card` in the `Waste` can be dragged and temporarily held in the `Hand`, and if not placed on a valid target (Either a suitable pile in the `Tableau` or onto a `Foundation`) it will be removed from the `Hand` and placed back on to the `Waste`. Only the top 3 `Cards` in the `Waste` are visible, fanned out from left to right, any additional `Cards` will be hidden until the top `Card` is moved to a valid target.

### Foundations

The `Foundations` begin empty and can only be filled one `Card` at a time starting at `Ace` and increasing sequentially in `Rank` up to `King`. Each `Foundation` can only consist of `Cards` in the same `Suit`. The top `Card` in each `Foundation` can be dragged and temporarily held by the `Hand`, and if not placed on a valid target (A suitable pile in the `Tableau`) it will be removed from the `Hand` and placed back on to their original `Foundation`.