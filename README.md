![Advanced Timer](https://cdn.modrinth.com/data/cached_images/b1f9825a5c1b070f6a6a59547a4856c182fa8f5a.png)

A highly customizable Minecraft timer mod with extensive color customization, gradient effects, auto-pause features, and per-world/server timer storage. Works with both **Fabric** and **NeoForge**. Does **NOT** use the Action Bar!

[![Available for Fabric](https://cdn.modrinth.com/data/cached_images/b223b5e2e5aca714ff700c82ca4f40f8fbc64b04_0.webp)](https://modrinth.com/mod/sodium/versions?l=fabric) [![Available for NeoForge](https://cdn.modrinth.com/data/cached_images/ba996a98da1c91c804209715787b2ead6aff023b.png)](https://modrinth.com/mod/sodium/versions?l=neoforge)

[Open Demo](https://g.goett.top/y0g12)

## Features

### Color & Visual Customization
- **Primary & Secondary Timer Colors** - Set custom colors for single or gradient modes
- **Gradient Mode** - Enable smooth color gradients between two colors
- **Animated Gradients** - Dynamic gradient animation with adjustable speed
- **Gradient Visibility** - Control gradient intensity (0-100%)
- **Bold Text** - Toggle bold timer display
- **Paused State Indicators**:
  - Optional "(paused)" suffix
  - Change color when paused

### Timer Display
- **Multiple Time Formats**: `DD:HH:MM`, `DD:HH:MM:SS`, `Dd Hh Mm Ss` and more!
- **Flexible Positioning**:
  - Above action bar
  - Top left/center/right
  - Bottom left/center/right
  - Custom coordinates
- **HUD Toggle** - Show/hide timer via command

### Auto-Pause System
Automatically pauses the timer on configurable events:
- Dragon Kill
- Wither Kill
- Elder Guardian Kill
- Warden Kill
- Player Death
- Leaving the game/world

### Multiloader Support
- Works seamlessly with both **Fabric** and **NeoForge** mod loaders

### Per-World/Server Timer Storage
- Automatically saves timer state per world/server
- Switch between worlds or servers without losing your timer data
- Supports multiple concurrent timers (one per world/server)

### Timer Modes
- **Count Up** - Track elapsed time from zero
- **Count Down** - Configurable countdown timer with custom hours, minutes, and seconds
- Timer end actions: `Off`, `Title`, or `Chat` message

### Commands

| Command | Description |
|---------|-------------|
| `/timer start` | Start or continue the timer |
| `/timer pause` | Pause the timer |
| `/timer continue` | Continue the timer (alias for start) |
| `/timer restart` | Reset and start the timer |
| `/timer hide` | Hide the timer HUD |
| `/timer show` | Show the timer HUD |
| `/timer config` | Open the config menu |
| `/timer help` | Display help message |
| `/timer time add <time>` | Add time to the timer in seconds, minutes, hours, days (e.g., `0 30`, `0 0 2`) |
| `/timer time set <time>` | Set the timer to a specific time |

### Configuration Menu
Full GUI configuration via **Yet Another Config Library (YACL)**:
- Timer color picker with hex input
- Gradient enable/disable toggle
- Animation speed slider
- All timer settings accessible in one place
- **ModMenu Integration** (Fabric) - Access config from mod menu

## Configuration Options

| Option | Default | Description |
|--------|---------|-------------|
| `timerColor` | `#8FE163` | Primary timer color |
| `secondTimerColor` | `#00ECD0` | Secondary color for gradient |
| `enableGradient` | `false` | Enable gradient mode |
| `animateGradient` | `true` | Animate gradient colors |
| `animationSpeed` | `1.0` | Gradient animation speed |
| `gradientVisibility` | `50` | Gradient intensity (0-100) |
| `timerBold` | `true` | Bold timer text |
| `timerFormat` | `HH:MM:SS` | Time display format |
| `pauseOnLeave` | `true` | Auto-pause when leaving world |
| `showPausedState` | `false` | Show "(paused)" suffix |
| `pausedSuffix` | `(paused)` | Paused indicator text |
| `changePausedColor` | `false` | Use different color when paused |
| `pausedColor` | `#AAAAAA` | Color when timer is paused |
| `timerDirection` | `UP` | Count up or countdown mode |
| `countdownHours/Minutes/Seconds` | `1:00:00` | Countdown start time |
| `timerPosition` | `ABOVE_ACTION_BAR` | HUD position |
| `autoPauseOnDragonKill` | `false` | Pause on Ender Dragon kill |
| `autoPauseOnWitherKill` | `false` | Pause on Wither kill |
| `autoPauseOnElderGuardianKill` | `false` | Pause on Elder Guardian kill |
| `autoPauseOnWardenKill` | `false` | Pause on Warden kill |
| `autoPauseOnDeath` | `false` | Pause on player death |
| `showCommandFeedback` | `true` | Show command feedback in chat |
| `timerEndAction` | `CHAT` | Action when countdown ends |

## License

This project is available under the MIT license.
