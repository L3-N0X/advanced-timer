package de.lenox.client

import com.mojang.brigadier.Command
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.resources.Identifier
import net.minecraft.network.chat.Component

class AdvancedTimerClient : ClientModInitializer {
    override fun onInitializeClient() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("advanced_timer", "timer_hud")) { graphics, _ ->
            TimerHudRenderer.render(graphics)
        }

        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            TimerManager.load()
            if (TimerManager.isRunning || TimerManager.currentTimeMs > 0) {
                TimerHudRenderer.isVisible = true
            }
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            TimerManager.save()
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("timer")
                    .then(literal("start").executes { context ->
                        TimerManager.reset()
                        TimerManager.start()
                        TimerHudRenderer.isVisible = true
                        context.source.sendFeedback(Component.literal("Timer started"))
                        Command.SINGLE_SUCCESS
                    })
                    .then(literal("pause").executes { context ->
                        TimerManager.pause()
                        context.source.sendFeedback(Component.literal("Timer paused"))
                        Command.SINGLE_SUCCESS
                    })
                    .then(literal("continue").executes { context ->
                        TimerManager.start()
                        TimerHudRenderer.isVisible = true
                        context.source.sendFeedback(Component.literal("Timer continued"))
                        Command.SINGLE_SUCCESS
                    })
                    .then(literal("hide").executes { context ->
                        TimerHudRenderer.isVisible = false
                        context.source.sendFeedback(Component.literal("Timer hidden"))
                        Command.SINGLE_SUCCESS
                    })
            )
        }
    }
}
