package de.lenox.client

import com.mojang.brigadier.Command
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@Mod("advanced_timer")
class AdvancedTimerNeoForge(container: ModContainer) {
    init {
        container.registerExtensionPoint(IConfigScreenFactory::class.java, IConfigScreenFactory { _, parent ->
            AdvancedTimerConfigScreen.create(parent)
        })
    }
}

@EventBusSubscriber(modid = "advanced_timer", value = [Dist.CLIENT])
object AdvancedTimerNeoForgeModEvents {
    @SubscribeEvent
    fun onRegisterGuiLayers(event: RegisterGuiLayersEvent) {
        event.registerAbove(VanillaGuiLayers.OVERLAY_MESSAGE, Identifier.fromNamespaceAndPath("advanced_timer", "timer_hud")) { graphics, _ ->
            TimerHudRenderer.render(graphics)
        }
    }
}

@EventBusSubscriber(modid = "advanced_timer", value = [Dist.CLIENT])
object AdvancedTimerNeoForgeEvents {

    @SubscribeEvent
    fun onRegisterClientCommands(event: RegisterClientCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("timer")
                .then(Commands.literal("start").executes { context ->
                    TimerHudRenderer.isVisible = true
                    context.source.sendSystemMessage(Component.literal("Timer started"))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.literal("pause").executes { context ->
                    TimerHudRenderer.isVisible = false
                    context.source.sendSystemMessage(Component.literal("Timer paused"))
                    Command.SINGLE_SUCCESS
                })
        )
    }
}
