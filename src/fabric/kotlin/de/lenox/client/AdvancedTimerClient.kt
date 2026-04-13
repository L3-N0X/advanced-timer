package de.lenox.client

import com.mojang.brigadier.Command
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.resources.Identifier
import net.minecraft.network.chat.Component

class AdvancedTimerClient : ClientModInitializer {
    override fun onInitializeClient() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("advanced_timer", "timer_hud")) { graphics, _ ->
            TimerHudRenderer.render(graphics)
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("timer")
                    .then(literal("start").executes { context ->
                        TimerHudRenderer.isVisible = true
                        context.source.sendFeedback(Component.literal("Timer started"))
                        Command.SINGLE_SUCCESS
                    })
                    .then(literal("pause").executes { context ->
                        TimerHudRenderer.isVisible = false
                        context.source.sendFeedback(Component.literal("Timer paused"))
                        Command.SINGLE_SUCCESS
                    })
            )
        }
    }
}
