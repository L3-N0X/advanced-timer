# Is it possible to implement a minecraft mod that adds another action bar above the actual action bar for e.g. a timer? Was this done already by someone? Is this very complicated?

Yes, it is entirely possible to add a second action bar for a timer, and it has been done many times before. The complexity of this project is quite low, especially if you are developing a client-side mod.

## Existing Implementations

Several existing mods already add secondary action bars or custom HUD elements for players [^1_1]. Client-side mods like Action Bar Info and BetterHud allow users to render custom text directly above the vanilla action bar without interfering with default game messages [^1_2][^1_1]. Similarly, speedrunning and utility mods like SpeedrunUtils and ChatTimer natively draw timers and status messages in that specific screen area [^1_3][^1_4].

## Implementation Comparison

When building this, you must choose between a client-side mod or a server-side plugin. Both are achievable but have drastically different constraints regarding the vanilla Minecraft client.


| Dimension | Client-Side Mod (Fabric) | Server-Side Plugin (PaperMC) |
| :-- | :-- | :-- |
| **Methodology** | Native GUI rendering hooks [^1_5] | Packet interception and string concatenation [^1_6] |
| **Complexity** | Very low [^1_7] | Moderate |
| **Visual Freedom** | Total control over screen coordinates [^1_2] | Restricted to vanilla UI limitations [^1_1] |
| **Requirement** | Players must install the mod locally [^1_3] | Works natively for vanilla clients [^1_6] |

## Client-Side Development

Creating this in a Fabric client mod is straightforward and not complicated [^1_7]. Historically, developers used `HudRenderCallback` to draw text at a specific Y-offset on the screen so it sits perfectly above the vanilla bar [^1_5][^1_8]. In modern versions like 1.21+, rendering heavily utilizes the `LayeredDrawer` system or `HudElementRegistry`, which provides an ordered way to render custom HUD elements without accidentally overlapping vanilla text [^1_9][^1_5].

## Server-Side Limitations

If you are building a server-side plugin, you cannot force a vanilla client to render a literal second action bar UI element [^1_6]. Instead, you must multiplex the existing action bar by intercepting packets and dynamically combining strings, such as appending your timer to other incoming vanilla messages [^1_3]. An alternative for server-side timers is utilizing the BossBar API, which inherently supports stacking multiple bars at the top of the player's screen [^1_1].

Are you planning to build this as a client-side Fabric mod, or does it need to work as a server-side plugin for vanilla players?
<span style="display:none">[^1_10][^1_11][^1_12][^1_13][^1_14][^1_15][^1_16][^1_17][^1_18][^1_19][^1_20][^1_21][^1_22][^1_23][^1_24][^1_25][^1_26][^1_27][^1_28][^1_29]</span>

# i want to build a fabric mod that does this. Adding a config screen and hotkeys should be straight forward as well. How well does communication work from this kind of fabric mod to a papermc server? Can i add some kind of api that the server can take over to control the client timer?

Communication between a Fabric client mod and a PaperMC server works seamlessly through Minecraft's native **Plugin Messaging Channels** (also known as Custom Payload Packets). Because this system is built into vanilla Minecraft, it allows custom data to flow between server plugins and client mods without requiring the server itself to be modded.

You can easily build an API on the PaperMC server that sends lightweight instructions (like starting, stopping, or updating a timer) for the Fabric mod to interpret and render on the client.

## Building the API Link

To set up this communication, both the Fabric mod and the PaperMC plugin need to agree on a unique channel identifier (e.g., `mymod:timer_control`).

### Server-Side: The PaperMC Plugin

On the server side, you do not need any special libraries. You use the standard Bukkit/Spigot Plugin Messaging API [^2_1]:

1. **Register the Channel:** In your plugin's `onEnable` method, register the outgoing channel using `getServer().getMessenger().registerOutgoingPluginChannel(this, "mymod:timer_control")` [^2_1].
2. **Send the Data:** When you want to trigger a timer for a player, write your data (e.g., duration, timer title, and color) to a byte array and send it to the specific player using `player.sendPluginMessage(plugin, "mymod:timer_control", byteArray)` [^2_2].

### Client-Side: The Fabric Mod

Modern Fabric (1.20.5 and 1.21+) handles incoming data using the `CustomPayload` and `ClientPlayNetworking` systems [^2_3][^2_4]:

1. **Define the Payload:** Create a Java `record` implementing `CustomPayload` that decodes the bytes sent by the Paper server into usable variables (like an integer for seconds and a string for the title) [^2_4].
2. **Register the Payload:** In your mod's initialization code, register the payload ID and codec using `PayloadTypeRegistry.playS2C().register()` [^2_4].
3. **Listen for Packets:** Use `ClientPlayNetworking.registerGlobalReceiver()` to listen for this specific payload [^2_3]. When the packet arrives, the receiver executes code on the client thread, updating the timer values in your mod's HUD rendering code.

## Why this Architecture is Ideal

Building the mod this way is highly efficient. Instead of the server sending a title/action-bar update every single second (or every tick) which hogs server bandwidth, the server only needs to send **one packet** saying "Start a 60-second timer called 'Event'" [^2_1][^2_3].

The Fabric client then takes over, handling the smooth, 60-FPS frame-by-frame rendering and countdown math locally. You can easily add a config screen to the Fabric mod using YetAnotherConfigLib or Cloth Config to let players shift the timer's X/Y coordinates or change hotkeys, independently of the server's logic.

Are you familiar with byte buffers for encoding/decoding the packet data, or would you prefer to send the data as a serialized JSON string?
<span style="display:none">[^2_10][^2_11][^2_12][^2_13][^2_14][^2_15][^2_16][^2_17][^2_5][^2_6][^2_7][^2_8][^2_9]</span>

---

# Is it possible to target fabric and neoforge at the same time and code this in kotlin.or is this not recommended? I need a config screen with a color picker. And also, how do i persist the timer data per server and per single player world properly so that multiple save states can coexist?

Yes, targeting both Fabric and NeoForge using Kotlin is absolutely possible and has become a highly recommended practice in modern modding. It maximizes your mod's reach with a single unified codebase.

### 1. Multi-Loader \& Kotlin Setup

To achieve this, you should use a multi-loader architecture. This splits your project into three modules: `common`, `fabric`, and `neoforge`. You write 95% of your Kotlin code in the `common` module, and only handle loader-specific hooks (like registering the network packets) in the platform modules.

* **Architectury Loom** or **Stonecutter** are the standard Gradle plugins used to build these projects.
* **Kotlin Support:** Both loaders have native Kotlin adapter mods. You will need to depend on `fabric-language-kotlin` for the Fabric side, and `Kotlin for Forge` (`kff`) for the NeoForge side [^3_1][^3_2].


### 2. Config Screen \& Color Picker

For the configuration screen, **YetAnotherConfigLib (YACL)** is the best library to use [^3_3].

* It natively supports both Fabric and NeoForge out of the box [^3_4].
* It has an incredibly clean, builder-based API in Kotlin/Java.
* It includes a built-in `ColorController`, which provides a very polished, interactive color picker UI for players to change the timer's hex color.


### 3. Persisting Timer Data Per World/Server

How you handle data persistence depends on where the "truth" of the timer lives:

**Scenario A: The Server Controls the Timer (Recommended)**
If the PaperMC server is meant to start/stop the timer, **do not save the active timer state on the client**.

1. The client uses YACL to save only global UI preferences (e.g., *Timer X/Y position = 100, 50; Color = Red*) in a standard `config/mymod.json` file.
2. The server's PaperMC plugin saves the actual active timers in its own `plugins/MyPlugin/timers.yml` file.
3. When a player joins, the Paper server immediately sends a packet to the client saying, "Display a timer starting at 5:00." This naturally isolates timers per server and single-player world.

**Scenario B: The Client Controls the Timer Locally**
If you want players to be able to set their own personal timers that persist across game restarts, you must map the saved data to the current server or world. You can dynamically fetch the player's current environment using the `MinecraftClient` instance:

* **Single-player:** Get the world save folder name using `MinecraftClient.getInstance().server?.session?.directoryName`.
* **Multiplayer:** Get the server IP address using `MinecraftClient.getInstance().currentServerEntry?.address`.

You would sanitize this string (to remove dots or invalid characters) and use it to save/load a specific JSON file, such as `config/mymod_timers/play_hypixel_net.json` or `config/mymod_timers/New_World.json`.

Would you like a brief code snippet showing how to implement the YACL Color Picker in Kotlin, or an example of how the common/platform multi-loader project structure looks?
<span style="display:none">[^3_10][^3_5][^3_6][^3_7][^3_8][^3_9]</span>
