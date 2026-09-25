# Universal Bone Meal

A Minecraft mod. Downloads can be found on [CurseForge](https://www.curseforge.com/members/fuzs_/projects)
and [Modrinth](https://modrinth.com/user/Fuzs).

![](banner.png)

## Configuration

Universal Bone Meal does not ship a config file. Everything that can be changed about the mod — which blocks
react to bone meal, how they react, and under which conditions — is defined inside
**[data packs](https://minecraft.wiki/w/Data_pack)**. If you are already used to vanilla worldgen files,
[block tags](https://minecraft.wiki/w/Block_tag_%28Java_Edition%29) and
[loot tables](https://minecraft.wiki/w/Loot_table), this will feel very familiar.

If you only want to change one or two things, jump straight to [Common recipes](#common-recipes). The rest of this
page documents every field in detail.

### How bone meal is applied

When bone meal is used on a block, Universal Bone Meal follows this chain:

1. **Fertilizer resistant tag** — if the block is in `#universalbonemeal:fertilizer_resistant_plants`, the attempt is
   denied completely, including any vanilla behavior. This applies to `minecraft:torchflower` and
   `minecraft:wither_rose` by default.
2. **Block data map** — the block is looked up in the `universalbonemeal:bonemealables` data map. If it has no entry,
   the mod leaves the interaction alone (vanilla handles it, if anything can).
3. **Replace check** — if the block already implements vanilla bone meal behavior and the entry does not set
   `"replace": true`, the mod steps aside and lets vanilla run.
4. **Conditional behavior** — the entry chooses a *conditional behavior*. This is either a single *action* optionally
   gated behind a *predicate*, or a list of predicate/action pairs with an optional fallback.
5. **Predicate + action** — if a predicate is present it must pass for the block to be a valid target. When the
   interaction succeeds, the action performs the actual growth.

This split is the heart of the mod: a **predicate** answers *"may this block be bone mealed right now?"* and a
**behavior** (also called an *action*) answers *"what happens when it is?"*. The two are independent, which is why
they live in separate registries and can be mixed freely.

### Where files live

Universal Bone Meal exposes two custom registries plus a regular block data map:

| Purpose                            | File location                                                       |
|------------------------------------|---------------------------------------------------------------------|
| Block-to-behavior assignments      | `data/<namespace>/data_maps/block/bonemealables.json`               |
| Reusable bone meal actions         | `data/<namespace>/universalbonemeal/bone_meal_behavior/<name>.json` |
| Reusable target predicates         | `data/<namespace>/universalbonemeal/block_predicate/<name>.json`    |
| Fertilizer resistant block tag     | `data/<namespace>/tags/block/fertilizer_resistant_plants.json`      |
| Loot tables used by `pop_resource` | `data/<namespace>/loot_table/<name>.json`                           |

The two custom registries are data-pack registries. Their folder layout contains the registry namespace on purpose:
`data/<namespace>/universalbonemeal/bone_meal_behavior/` means "an entry whose id namespace is `<namespace>`, stored
in the registry `universalbonemeal:bone_meal_behavior`". For example a file at
`data/mypack/universalbonemeal/bone_meal_behavior/flower.json` defines the action `mypack:flower`.

You can use whichever namespace you like. To keep things short this page uses `mypack` for pack-owned examples.

### The `bonemealables` data map

This is a standard block data map at `data/<namespace>/data_maps/block/bonemealables.json`. It maps a block (or a
block tag) to a single **bonemealable** value. Every vanilla block that the mod makes bone-mealable is defined here,
and other mods are expected to add their own blocks the same way.

#### Top level

| Field     | Type                         | Required | Description                                                                                                      |
|-----------|------------------------------|----------|------------------------------------------------------------------------------------------------------------------|
| `values`  | Map of block id/tag to value | yes      | The entries. Keys are block ids (`minecraft:cactus`) or block tags (`#minecraft:small_flowers`).                 |
| `replace` | Boolean                      | no       | If `true`, the whole data map is replaced instead of merged. **This discards every other pack's entries.**       |
| `remove`  | List of block ids/tags       | no       | Removes the given keys from the final data map. Useful to opt a block back out of a behavior another pack added. |

#### The bonemealable value

Each value is a flat object combining a [conditional behavior](#conditional-behaviors) with one extra field:

| Field      | Type                        | Default | Description                                                                                                                    |
|------------|-----------------------------|---------|--------------------------------------------------------------------------------------------------------------------------------|
| `type`     | Conditional behavior type   | —       | `universalbonemeal:conditional` or `universalbonemeal:combined`. See below.                                                    |
| `replace`  | Boolean                     | `false` | Only matters for blocks that already implement vanilla bone meal. When `true` the mod's behavior is used *instead of* vanilla. |
| *(others)* | Fields of the chosen `type` | —       | The remaining fields are placed at this same level, next to `type` and `replace`.                                              |

A minimal entry:

```json
{
  "values": {
    "minecraft:cactus": {
      "type": "universalbonemeal:conditional",
      "behavior": "universalbonemeal:cactus",
      "when": "universalbonemeal:cactus"
    }
  }
}
```

If a key is a block tag that several packs contribute to, a value may optionally be wrapped to say whether it should
override the tag entry from earlier packs:

```json
{
  "values": {
    "#minecraft:small_flowers": {
      "value": {
        "type": "universalbonemeal:conditional",
        "behavior": "mypack:flower"
      },
      "replace": true
    }
  }
}
```

Do not confuse this wrapper `replace` (which controls tag merging for that single key) with the `replace` field *inside*
a bonemealable value (which controls whether vanilla bone meal behavior is overridden).

### Conditional behaviors

A conditional behavior decides *which* action runs for a given block position.

#### `universalbonemeal:conditional`

Runs one action, optionally only when a predicate matches.

| Field      | Type                     | Required | Description                                                                                                |
|------------|--------------------------|----------|------------------------------------------------------------------------------------------------------------|
| `behavior` | Action (id or inline)    | yes      | The action to run.                                                                                         |
| `when`     | Predicate (id or inline) | no       | If omitted the block is always a valid target. If present the predicate must pass at the clicked position. |

```json
{
  "type": "universalbonemeal:conditional",
  "behavior": "universalbonemeal:small_flower"
}
```

#### `universalbonemeal:combined`

Picks the first action whose predicate matches, falling back to another action otherwise. This is how one block can
behave differently depending on its surroundings.

| Field        | Type                           | Required | Description                                                                              |
|--------------|--------------------------------|----------|------------------------------------------------------------------------------------------|
| `conditions` | List of predicate/action pairs | yes      | Checked in order. The first matching `when` wins.                                        |
| `fallback`   | Action (id or inline)          | no       | Used when no condition matches. Without it the block is not a valid target in that case. |

Each entry in `conditions` is an object with:

| Field      | Type      | Description                     |
|------------|-----------|---------------------------------|
| `when`     | Predicate | The condition to test.          |
| `behavior` | Action    | The action to run if it passes. |

```json
{
  "type": "universalbonemeal:combined",
  "conditions": [
    {
      "when": "universalbonemeal:air_above",
      "behavior": "universalbonemeal:mycelium"
    }
  ]
}
```

### Actions

Actions live in the `universalbonemeal:bone_meal_behavior` registry. You can either define your own reusable action
in that registry and reference it by id, or write it inline wherever an action is expected. The examples below show
inline definitions, which is often the quickest route.

Every action object starts with a `type` field naming one of the following.

#### `universalbonemeal:growing_plant`

Grows a vertical stack of blocks, like sugar cane or cactus. The stack is grown from the topmost connected block in
`direction`, placing `blocks_to_grow` blocks as long as `can_grow_into` passes. Growth stops early if a placed block
is not the same block that was clicked (this is how a cactus flower ends the growth of a cactus).

| Field            | Type                     | Default               | Description                                                                                                                         |
|------------------|--------------------------|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| `direction`      | Direction                | `up`                  | Which way the plant grows. One of `up`, `down`, `north`, `south`, `west`, `east`.                                                   |
| `blocks_to_grow` | Integer provider (0–128) | —                     | How many blocks to attempt to grow.                                                                                                 |
| `can_grow_into`  | Block predicate          | only-in-air predicate | The block can be replaced while growing.                                                                                            |
| `vegetation`     | Block state provider     | —                     | The block(s) to place. Evaluated at the position right next to the previously placed block.                                         |
| `property`       | String                   | none                  | Optional age property name. After growing, that integer property on the head block is reset to `0` so the plant can be grown again. |

```json
{
  "type": "universalbonemeal:growing_plant",
  "blocks_to_grow": {
    "type": "minecraft:uniform",
    "min_inclusive": 1,
    "max_inclusive": 2
  },
  "vegetation": {
    "type": "minecraft:weighted",
    "entries": [
      {
        "data": "minecraft:cactus",
        "weight": 9
      },
      {
        "data": "minecraft:cactus_flower",
        "weight": 1
      }
    ]
  },
  "property": "age"
}
```

#### `universalbonemeal:vine`

A specialized `growing_plant` for hanging vines. It uses the same fields as `growing_plant` minus `property`,
defaults to `direction: "down"`, and strips the `up` face from newly grown blocks so the vine shape stays consistent.
Its `blocks_to_grow` is usually `{ "type": "universalbonemeal:vine" }`, which mirrors vanilla's vine growth.

```json
{
  "type": "universalbonemeal:vine",
  "blocks_to_grow": {
    "type": "universalbonemeal:vine",
    "max_inclusive": 5
  },
  "direction": "down",
  "vegetation": {
    "type": "universalbonemeal:copy_source"
  }
}
```

#### `universalbonemeal:crop_growth`

Increases an integer property on the block, clamped to the property's maximum. This is the generic "age up a crop"
action.

| Field      | Type                         | Default | Description                                                                                              |
|------------|------------------------------|---------|----------------------------------------------------------------------------------------------------------|
| `property` | String                       | —       | Name of the integer property to increase, for example `age`. Matched against the block's own properties. |
| `increase` | Integer provider (0 or more) | —       | How much to add to the current value.                                                                    |

```json
{
  "type": "universalbonemeal:crop_growth",
  "property": "age",
  "increase": {
    "type": "minecraft:weighted_list",
    "distribution": [
      {
        "data": 0,
        "weight": 1
      },
      {
        "data": 1,
        "weight": 3
      }
    ]
  }
}
```

#### `universalbonemeal:random_tick`

Runs vanilla's own random tick logic on the block a number of times. This is the safest way to reproduce a block's
normal growth (for example melon and pumpkin stems, or chorus flowers).

| Field             | Type                    | Default | Description                       |
|-------------------|-------------------------|---------|-----------------------------------|
| `growth_attempts` | Integer provider (1–64) | —       | How many random ticks to perform. |

```json
{
  "type": "universalbonemeal:random_tick",
  "growth_attempts": {
    "type": "minecraft:uniform",
    "min_inclusive": 2,
    "max_inclusive": 5
  }
}
```

#### `universalbonemeal:random_neighbor_spread`

Scatters copies of a block around the clicked position, at most `most_successes` successful placements, similar to
vanilla's `random_neighbor_spread` feature. Used for flowers, lily pads and dead bushes.

| Field            | Type                    | Default | Description                                                                                          |
|------------------|-------------------------|---------|------------------------------------------------------------------------------------------------------|
| `block`          | Block state provider    | —       | The block(s) to spread. Use `{ "type": "universalbonemeal:copy_source" }` to copy the clicked block. |
| `spread_width`   | Integer provider (1–16) | —       | Horizontal spread radius.                                                                            |
| `most_successes` | Integer provider (1–16) | —       | Maximum number of successful placements.                                                             |

```json
{
  "type": "universalbonemeal:random_neighbor_spread",
  "block": {
    "type": "universalbonemeal:copy_source"
  },
  "spread_width": 3,
  "most_successes": 1
}
```

#### `universalbonemeal:vegetation_scatter`

Randomly scatters vegetation in a box around the clicked block. Unlike `random_neighbor_spread` it does not need a
source block to copy and is used for mycelium mushrooms.

| Field           | Type                    | Default | Description               |
|-----------------|-------------------------|---------|---------------------------|
| `vegetation`    | Block state provider    | —       | The block(s) to scatter.  |
| `spread_width`  | Integer provider (1–16) | —       | Horizontal spread radius. |
| `spread_height` | Integer provider (0–16) | —       | Vertical spread radius.   |

```json
{
  "type": "universalbonemeal:vegetation_scatter",
  "spread_width": 3,
  "spread_height": 1,
  "vegetation": {
    "type": "minecraft:weighted",
    "entries": [
      {
        "data": "minecraft:red_mushroom",
        "weight": 1
      },
      {
        "data": "minecraft:brown_mushroom",
        "weight": 1
      }
    ]
  }
}
```

#### `universalbonemeal:placed_feature`

Places a worldgen [placed feature](https://minecraft.wiki/w/Placed_feature) at the clicked position, like vanilla
coral and nether fungus bone meal. The block is removed first, then the feature runs; if the feature fails to place,
the original block is restored.

| Field            | Type              | Default | Description                                                                                                     |
|------------------|-------------------|---------|-----------------------------------------------------------------------------------------------------------------|
| `placed_feature` | Placed feature id | —       | The placed feature to run, for example `minecraft:warm_ocean_vegetation` or one of the built-in coral features. |
| `success_chance` | Float (0.0–1.0)   | —       | Chance for the attempt to succeed. Note the bone meal item is consumed even when the roll fails.                |

```json
{
  "type": "universalbonemeal:placed_feature",
  "placed_feature": "universalbonemeal:brain_coral",
  "success_chance": 0.4
}
```

#### `universalbonemeal:chorus_plant`

Searches through connected chorus plants up to `search_range` blocks away, then random ticks every connected chorus
flower it finds. This is what makes bone meal on a chorus plant grow the whole tree.

| Field          | Type            | Default | Description                                                              |
|----------------|-----------------|---------|--------------------------------------------------------------------------|
| `plant`        | Block set       | —       | The blocks considered part of the plant structure, usually chorus plant. |
| `flower`       | Block set       | —       | The flower blocks to random tick.                                        |
| `search_range` | Integer (1–256) | —       | Maximum traversal distance.                                              |

```json
{
  "type": "universalbonemeal:chorus_plant",
  "plant": "minecraft:chorus_plant",
  "flower": "minecraft:chorus_flower",
  "search_range": 128
}
```

#### `universalbonemeal:neighbor_conversion`

Looks for blocks from `spread_sources` within a width/height radius and converts the clicked block into a random one
of them. This is the dirt-to-grass/mycelium action.

| Field            | Type           | Default | Description                                       |
|------------------|----------------|---------|---------------------------------------------------|
| `spread_sources` | Block set      | —       | Blocks that may be copied onto the clicked block. |
| `spread_width`   | Integer (1–16) | —       | Horizontal search radius.                         |
| `spread_height`  | Integer (0–16) | —       | Vertical search radius.                           |

```json
{
  "type": "universalbonemeal:neighbor_conversion",
  "spread_sources": [
    "minecraft:grass_block",
    "minecraft:mycelium"
  ],
  "spread_width": 1,
  "spread_height": 1
}
```

#### `universalbonemeal:vegetation_patch`

A close port of vanilla's `vegetation_patch` feature: it converts `replaceable` ground into `ground_state` and bone
meals any nearby blocks listed in `bonemealable_blocks`. Used for podzol (ferns and sweet berry bushes).

| Field                 | Type                      | Default | Description                                                                   |
|-----------------------|---------------------------|---------|-------------------------------------------------------------------------------|
| `replaceable`         | Block set                 | —       | Blocks that may be replaced by the patch.                                     |
| `ground_state`        | Block state provider      | —       | The block(s) placed as ground.                                                |
| `bonemealable_blocks` | Block set                 | —       | Existing blocks in the patch area that get an extra chance to be bone mealed. |
| `attempts`            | Integer provider (1–1024) | —       | Total placement attempts.                                                     |
| `attempts_per_step`   | Integer provider (1–128)  | —       | Attempts before the walking position moves one step.                          |

```json
{
  "type": "universalbonemeal:vegetation_patch",
  "replaceable": "minecraft:podzol",
  "ground_state": {
    "type": "minecraft:weighted",
    "entries": [
      {
        "data": "minecraft:fern",
        "weight": 120
      },
      {
        "data": "minecraft:dead_bush",
        "weight": 1
      }
    ]
  },
  "bonemealable_blocks": "minecraft:fern",
  "attempts": 128,
  "attempts_per_step": 16
}
```

#### `universalbonemeal:pop_resource`

Drops items from a loot table at the position, the same mechanism vanilla uses for e.g. spore blossoms. Point it at
a [block interact loot table](#loot-tables).

| Field           | Type                            | Default | Description                                                                     |
|-----------------|---------------------------------|---------|---------------------------------------------------------------------------------|
| `loot`          | Loot table id                   | —       | The loot table to roll.                                                         |
| `drop_strategy` | `from_middle` or `clicked_face` | —       | How the dropped items are positioned.                                           |
| `direction`     | Direction                       | —       | Required when `drop_strategy` is `clicked_face`; the face the items pop out of. |

```json
{
  "type": "universalbonemeal:pop_resource",
  "loot": "universalbonemeal:grow/spore_blossom",
  "drop_strategy": "from_middle"
}
```

```json
{
  "type": "universalbonemeal:pop_resource",
  "loot": "mypack:my_drop",
  "drop_strategy": "clicked_face",
  "direction": "up"
}
```

### Target predicates

Predicates live in the `universalbonemeal:block_predicate` registry and decide whether a block is a valid bone meal
target. They are ordinary [block predicates](https://minecraft.wiki/w/Block_predicate), so every vanilla type is
usable here as well — `minecraft:matching_blocks`, `minecraft:matching_block_tag`, `minecraft:matching_biomes`,
`minecraft:all_of`, `minecraft:any_of`, `minecraft:not`, `minecraft:would_survive`, and so on.

A predicate is evaluated at the clicked block position. Universal Bone Meal adds the following types on top.

#### `universalbonemeal:growing_plant`

Passes when the block is part of a growing plant whose connected height is below `max_height` and the block beyond
the head can be replaced.

| Field           | Type                     | Default               | Description                                                                                       |
|-----------------|--------------------------|-----------------------|---------------------------------------------------------------------------------------------------|
| `direction`     | Direction                | `up`                  | Direction the plant grows in.                                                                     |
| `max_height`    | Integer provider (1–128) | —                     | Maximum allowed plant height. Sampled deterministically per position, so client and server agree. |
| `can_grow_into` | Block predicate          | only-in-air predicate | The block that would be grown into.                                                               |

#### `universalbonemeal:vine`

Passes when the block is a vine attached to a wall on at least one horizontal side. Extends vanilla's
state-testing predicate, so it has an optional `offset`.

| Field    | Type   | Default     | Description                     |
|----------|--------|-------------|---------------------------------|
| `offset` | Vector | `[0, 0, 0]` | Position offset to test around. |

#### `universalbonemeal:block_property`

Passes when the block has an integer property that has not reached its maximum value.

| Field      | Type   | Default     | Description                                                 |
|------------|--------|-------------|-------------------------------------------------------------|
| `property` | String | —           | Name of the integer property to inspect, for example `age`. |
| `offset`   | Vector | `[0, 0, 0]` | Position offset to test around.                             |

#### `universalbonemeal:chorus_plant`

Passes when a non-dead chorus flower can be reached through connected plant blocks within `search_range`.

| Field          | Type            | Default | Description                            |
|----------------|-----------------|---------|----------------------------------------|
| `plant`        | Block set       | —       | Blocks treated as the plant structure. |
| `flower`       | Block set       | —       | Blocks treated as flowers.             |
| `search_range` | Integer (1–256) | —       | Maximum traversal distance.            |

#### `universalbonemeal:fruit_stem`

Passes when the block is a fully grown stem (`age` at maximum) with air next to it above a block from
`fruit_support_blocks`, i.e. a spot a fruit could grow into.

| Field                  | Type      | Default | Description                        |
|------------------------|-----------|---------|------------------------------------|
| `fruit_support_blocks` | Block set | —       | Blocks the fruit may be placed on. |

#### `universalbonemeal:neighbor_conversion`

Passes when skylight can propagate down onto the block and a block from `spread_sources` is nearby.

| Field            | Type           | Default | Description                |
|------------------|----------------|---------|----------------------------|
| `spread_sources` | Block set      | —       | Blocks that may be copied. |
| `spread_width`   | Integer (1–16) | —       | Horizontal search radius.  |
| `spread_height`  | Integer (0–16) | —       | Vertical search radius.    |

### Reusable actions and predicates

Defining an action or predicate in its own registry file lets you reference it by id from any number of data map
entries, and lets other packs override it.

`data/mypack/universalbonemeal/bone_meal_behavior/flower.json`:

```json
{
  "type": "universalbonemeal:random_neighbor_spread",
  "block": {
    "type": "universalbonemeal:copy_source"
  },
  "spread_width": 3,
  "most_successes": 1
}
```

`data/mypack/universalbonemeal/block_predicate/air_above.json`:

```json
{
  "type": "minecraft:matching_block_tag",
  "tag": "minecraft:air",
  "offset": [
    0,
    1,
    0
  ]
}
```

Reference them from the data map with their ids:

```json
{
  "type": "universalbonemeal:conditional",
  "behavior": "mypack:flower",
  "when": "mypack:air_above"
}
```

Fields that expect an action, predicate or state provider accept either an **id string** (a reference to a
registered entry) or an **inline object**. Inline objects are expanded on the spot and are handy for one-off
tweaks, while ids are better when several blocks share the same logic.

### Shared value types

Several fields reference building blocks from vanilla. These are documented on the wiki, but for convenience:

**Block state providers** — used by `vegetation`, `block` and `ground_state`. A provider may be written as a plain
block state, or as a typed provider object. The mod adds one extra type:

| Value                                                                  | Description                                                                |
|------------------------------------------------------------------------|----------------------------------------------------------------------------|
| `"minecraft:stone"`                                                    | A bare block state (default properties).                                   |
| `{ "id": "minecraft:sweet_berry_bush", "properties": { "age": "3" } }` | A block state with explicit properties.                                    |
| `{ "type": "minecraft:weighted", "entries": [ ... ] }`                 | Weighted random choice, entries of `{ "data": <state>, "weight": <int> }`. |
| `{ "type": "universalbonemeal:copy_source" }`                          | Copies the block it grows from (keeps vine/flower shape). No fields.       |

**Integer providers** — used by `blocks_to_grow`, `increase`, `growth_attempts`, `spread_width`, `most_successes`,
`spread_height`, `attempts`, `attempts_per_step` and `max_height`. Standard types include `minecraft:constant`
(`value`), `minecraft:uniform` (`min_inclusive`, `max_inclusive`), `minecraft:biased_to_bottom`,
`minecraft:very_biased_to_bottom`, `minecraft:clamped`, `minecraft:weighted_list` (`distribution`),
`minecraft:clamped_normal` and `minecraft:trapezoid`. The mod adds:

| Value                                                      | Description                                                                              |
|------------------------------------------------------------|------------------------------------------------------------------------------------------|
| `{ "type": "universalbonemeal:vine", "max_inclusive": 5 }` | Mirrors vanilla's vine bonemeal growth. `max_inclusive` is optional, 1–128, default 128. |

**Block sets** — used by `plant`, `flower`, `spread_sources`, `replaceable` and `bonemealable_blocks`. Accepted as a
single id, a tag (`#namespace:tag`) or a list of either.

### Tags

`universalbonemeal:fertilizer_resistant_plants` makes blocks completely immune to bone meal, vanilla behavior
included. It is a normal block tag at `data/<namespace>/tags/block/fertilizer_resistant_plants.json` and can be
extended or replaced like any other tag:

```json
{
  "replace": false,
  "values": [
    "minecraft:torchflower",
    "minecraft:wither_rose"
  ]
}
```

### Loot tables

`universalbonemeal:pop_resource` drops items through a loot table of type `minecraft:block_interact`. This is the
same format vanilla uses for spore blossoms and shears. Point the `loot` field at any such table. Loot table ids may
contain subfolders — for example the built-in spore blossom table is `universalbonemeal:grow/spore_blossom`, stored
at `data/universalbonemeal/loot_table/grow/spore_blossom.json`.

```json
{
  "type": "minecraft:block_interact",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:spore_blossom"
        }
      ]
    }
  ],
  "random_sequence": "mypack:my_drop"
}
```

### Common recipes

**Make a block bone-mealable with no conditions.** Add an entry to the data map pointing at an existing action:

```json
{
  "values": {
    "minecraft:cobblestone": {
      "type": "universalbonemeal:conditional",
      "behavior": "universalbonemeal:mycelium"
    }
  }
}
```

**Override a block that vanilla already handles.** Saplings, crops and other vanilla bone-mealable blocks are only
touched when `replace` is set:

```json
{
  "values": {
    "minecraft:oak_sapling": {
      "type": "universalbonemeal:conditional",
      "behavior": "mypack:big_tree",
      "replace": true
    }
  }
}
```

**Disable a built-in behavior.** Either make the block immune, or remove its data map entry:

```json
{
  "values": {},
  "remove": [
    "minecraft:cactus"
  ]
}
```

```json
{
  "replace": false,
  "values": [
    "minecraft:cactus",
    "minecraft:sugar_cane"
  ]
}
```

(placed in `data/<namespace>/tags/block/fertilizer_resistant_plants.json`)

**Run a placed feature.** The simplest way to reuse worldgen for bone meal:

```json
{
  "values": {
    "minecraft:moss_block": {
      "type": "universalbonemeal:conditional",
      "behavior": {
        "type": "universalbonemeal:placed_feature",
        "placed_feature": "minecraft:grass_bonemeal",
        "success_chance": 1.0
      }
    }
  }
}
```

**Only allow bone meal under specific conditions.** Gate an action behind a predicate, for example requiring air
above the block or a biome:

```json
{
  "values": {
    "minecraft:dirt": {
      "type": "universalbonemeal:conditional",
      "behavior": "universalbonemeal:dirt",
      "when": {
        "type": "minecraft:matching_biomes",
        "biomes": "#minecraft:is_forest"
      }
    }
  }
}
```
