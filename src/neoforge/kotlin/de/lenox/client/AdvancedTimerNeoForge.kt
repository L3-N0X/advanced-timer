package de.lenox.client

import com.mojang.brigadier.Command
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent

@Mod("advanced_timer")
@EventBusSubscriber(modid = "advanced_timer", value = [Dist.CLIENT])
object AdvancedTimerNeoForge {

    @SubscribeEvent
    fun onRegisterClientCommands(event: RegisterClientCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("timer")
                .then(Commands.literal("start").executes { context ->
                    context.source.sendSystemMessage(Component.literal("Timer started"))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.literal("pause").executes { context ->
                    context.source.sendSystemMessage(Component.literal("Timer paused"))
                    Command.SINGLE_SUCCESS
                })
        )
    }
}
