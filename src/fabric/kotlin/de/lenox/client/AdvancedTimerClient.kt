package de.lenox.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.resources.Identifier

class AdvancedTimerClient : ClientModInitializer {
    override fun onInitializeClient() {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("advanced_timer", "timer_hud")
        ) { graphics, _ -> TimerHudRenderer.render(graphics) }

        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            TimerManager.load()
            ClientEventDetector.reset()
            if (TimerManager.isRunning || TimerManager.currentTimeMs > 0) {
                TimerHudRenderer.isVisible = true
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            ClientEventDetector.tick(client)
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            if (GlobalConfigManager.config.pauseOnLeave) {
                TimerManager.pause()
            }
            TimerManager.save()
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            AdvancedTimerFabricCommands.register(dispatcher)
        }
    }
}
