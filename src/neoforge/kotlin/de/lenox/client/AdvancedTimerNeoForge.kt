package de.lenox.client

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
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

    private fun sendHelp(source: CommandSourceStack) {
        val helpText = "§6--- Advanced Timer Help ---\n" +
            "§e/timer start§f: Starts or continues the timer\n" +
            "§e/timer restart§f: Restarts the timer\n" +
            "§e/timer pause§f: Pauses the timer\n" +
            "§e/timer continue§f: Continues the timer\n" +
            "§e/timer help§f: Shows this help message\n" +
            "§e/timer hide§f: Hides the timer\n" +
            "§e/timer time <add|subtract|set> <sec> [min] [hr] [day]§f: Modifies time"
        source.sendSystemMessage(Component.literal(helpText))
    }

    private fun handleTimeAction(
        context: CommandContext<CommandSourceStack>,
        action: String,
        hasMin: Boolean,
        hasHr: Boolean,
        hasDay: Boolean
    ): Int {
        val sec = IntegerArgumentType.getInteger(context, "seconds")
        val min = if (hasMin) IntegerArgumentType.getInteger(context, "minutes") else 0
        val hr = if (hasHr) IntegerArgumentType.getInteger(context, "hours") else 0
        val day = if (hasDay) IntegerArgumentType.getInteger(context, "days") else 0
        
        val timeMs = (sec * 1000L) + (min * 60000L) + (hr * 3600000L) + (day * 86400000L)
        
        when (action) {
            "add" -> TimerManager.addTimeMs(timeMs)
            "subtract" -> TimerManager.subtractTimeMs(timeMs)
            "set" -> TimerManager.setTimeMs(timeMs)
        }
        
        context.source.sendSystemMessage(Component.literal("Timer time updated"))
        return Command.SINGLE_SUCCESS
    }

    @SubscribeEvent
    fun onClientLogin(event: ClientPlayerNetworkEvent.LoggingIn) {
        TimerManager.load()
        ClientEventDetector.reset()
        if (TimerManager.isRunning || TimerManager.currentTimeMs > 0) {
            TimerHudRenderer.isVisible = true
        }
    }

    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        ClientEventDetector.tick(net.minecraft.client.Minecraft.getInstance())
    }

    @SubscribeEvent
    fun onClientLogout(event: ClientPlayerNetworkEvent.LoggingOut) {
        if (GlobalConfigManager.config.pauseOnLeave) {
            TimerManager.pause()
        }
        TimerManager.save()
    }

    @SubscribeEvent
    fun onRegisterClientCommands(event: RegisterClientCommandsEvent) {
        val timeCommand = Commands.literal("time")
        listOf("add", "subtract", "set").forEach { action ->
            timeCommand.then(Commands.literal(action)
                .then(Commands.argument("seconds", IntegerArgumentType.integer(0))
                    .executes { ctx -> handleTimeAction(ctx, action, false, false, false) }
                    .then(Commands.argument("minutes", IntegerArgumentType.integer(0))
                        .executes { ctx -> handleTimeAction(ctx, action, true, false, false) }
                        .then(Commands.argument("hours", IntegerArgumentType.integer(0))
                            .executes { ctx -> handleTimeAction(ctx, action, true, true, false) }
                            .then(Commands.argument("days", IntegerArgumentType.integer(0))
                                .executes { ctx -> handleTimeAction(ctx, action, true, true, true) }
                            )
                        )
                    )
                )
            )
        }

        event.dispatcher.register(
            Commands.literal("timer")
                .executes { context ->
                    sendHelp(context.source)
                    Command.SINGLE_SUCCESS
                }
                .then(Commands.literal("help").executes { context ->
                    sendHelp(context.source)
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.literal("restart").executes { context ->
                    TimerManager.reset()
                    TimerManager.start()
                    TimerHudRenderer.isVisible = true
                    context.source.sendSystemMessage(Component.literal("Timer restarted"))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.literal("start").executes { context ->
                    TimerManager.start()
                    TimerHudRenderer.isVisible = true
                    context.source.sendSystemMessage(Component.literal("Timer started"))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.literal("pause").executes { context ->
                    TimerManager.pause()
                    context.source.sendSystemMessage(Component.literal("Timer paused"))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.literal("continue").executes { context ->
                    TimerManager.start()
                    TimerHudRenderer.isVisible = true
                    context.source.sendSystemMessage(Component.literal("Timer continued"))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.literal("hide").executes { context ->
                    TimerHudRenderer.isVisible = false
                    context.source.sendSystemMessage(Component.literal("Timer hidden"))
                    Command.SINGLE_SUCCESS
                })
                .then(timeCommand)
        )
    }
}
