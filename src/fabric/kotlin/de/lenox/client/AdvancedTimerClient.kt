package de.lenox.client

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

class AdvancedTimerClient : ClientModInitializer {
    private fun sendHelp(source: FabricClientCommandSource) {
        val titleColor = 0xB193EB
        val cmdColor = 0x90A7E2
        val colonColor = 0x6e73bf
        val descColor = 0xd5deee

        val addHelpLine = { cmd: String, desc: String ->
            Component.literal("\n").append(Component.literal(cmd).withColor(cmdColor))
                .append(Component.literal(" » ").withColor(colonColor))
                .append(Component.literal(desc).withColor(descColor))
        }

        val helpText = Component.literal(">>> Advanced Timer Help <<<").withColor(titleColor)
            .append(addHelpLine("/timer start", "Starts or continues the timer"))
            .append(addHelpLine("/timer pause", "Pauses the timer"))
            .append(addHelpLine("/timer continue", "Continues the timer"))
            .append(addHelpLine("/timer restart", "Restarts the timer")).append(
                addHelpLine(
                    "/timer time <add|subtract|set> <sec> [min] [hr] [day]", "Modifies time"
                )
            ).append(addHelpLine("/timer hide", "Hides the timer"))
            .append(addHelpLine("/timer show", "Shows the hidden timer"))
            .append(addHelpLine("/timer config", "Opens the config menu"))
            .append(addHelpLine("/timer help", "Shows this help message"))

        source.sendFeedback(helpText)
    }

    private fun handleTimeAction(
        context: CommandContext<FabricClientCommandSource>,
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

        if (GlobalConfigManager.config.showCommandFeedback) {
            context.source.sendFeedback(Component.literal("Timer time updated").withColor(0xd5deee))
        }
        return Command.SINGLE_SUCCESS
    }

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

        ClientTickEvents.END_CLIENT_TICK.register { client -> ClientEventDetector.tick(client) }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            if (GlobalConfigManager.config.pauseOnLeave) {
                TimerManager.pause()
            }
            TimerManager.save()
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val timeCommand = literal("time")
            listOf("add", "subtract", "set").forEach { action ->
                timeCommand.then(
                    literal(action).then(
                        argument(
                            "seconds", IntegerArgumentType.integer(0)
                        ).executes { ctx ->
                            handleTimeAction(
                                ctx, action, false, false, false
                            )
                        }.then(
                            argument(
                                "minutes", IntegerArgumentType.integer(
                                    0
                                )
                            ).executes { ctx ->
                                handleTimeAction(
                                    ctx, action, true, false, false
                                )
                            }.then(
                                argument(
                                    "hours", IntegerArgumentType.integer(
                                        0
                                    )
                                ).executes { ctx ->
                                    handleTimeAction(
                                        ctx, action, true, true, false
                                    )
                                }.then(
                                    argument(
                                        "days", IntegerArgumentType.integer(
                                            0
                                        )
                                    ).executes { ctx ->
                                        handleTimeAction(
                                            ctx, action, true, true, true
                                        )
                                    })
                            )
                        )
                    )
                )
            }

            dispatcher.register(
                literal("timer").executes { context ->
                    sendHelp(context.source)
                    Command.SINGLE_SUCCESS
                }.then(
                    literal("help").executes { context ->
                        sendHelp(context.source)
                        Command.SINGLE_SUCCESS
                    }).then(
                    literal("restart").executes { context ->
                        TimerManager.reset()
                        TimerManager.start()
                        TimerHudRenderer.isVisible = true
                        if (GlobalConfigManager.config.showCommandFeedback) {
                            context.source.sendFeedback(
                                Component.literal("Timer restarted").withColor(0xd5deee)
                            )
                        }
                        Command.SINGLE_SUCCESS
                    }).then(
                    literal("start").executes { context ->
                        TimerManager.start()
                        TimerHudRenderer.isVisible = true
                        if (GlobalConfigManager.config.showCommandFeedback) {
                            context.source.sendFeedback(
                                Component.literal("Timer started").withColor(0xd5deee)
                            )
                        }
                        Command.SINGLE_SUCCESS
                    }).then(
                    literal("pause").executes { context ->
                        TimerManager.pause()
                        if (GlobalConfigManager.config.showCommandFeedback) {
                            context.source.sendFeedback(
                                Component.literal("Timer paused").withColor(0xd5deee)
                            )
                        }
                        Command.SINGLE_SUCCESS
                    }).then(
                    literal("continue").executes { context ->
                        TimerManager.start()
                        TimerHudRenderer.isVisible = true
                        if (GlobalConfigManager.config.showCommandFeedback) {
                            context.source.sendFeedback(
                                Component.literal("Timer continued").withColor(0xd5deee)
                            )
                        }
                        Command.SINGLE_SUCCESS
                    }).then(
                    literal("hide").executes { context ->
                        TimerHudRenderer.isVisible = false
                        if (GlobalConfigManager.config.showCommandFeedback) {
                            context.source.sendFeedback(
                                Component.literal("Timer hidden").withColor(0xd5deee)
                            )
                        }
                        Command.SINGLE_SUCCESS
                    }).then(
                    literal("show").executes { context ->
                        TimerHudRenderer.isVisible = true
                        if (GlobalConfigManager.config.showCommandFeedback) {
                            context.source.sendFeedback(
                                Component.literal("Timer shown").withColor(0xd5deee)
                            )
                        }
                        Command.SINGLE_SUCCESS
                    }).then(
                    literal("config").executes { _ ->
                        net.minecraft.client.Minecraft.getInstance().execute {
                            net.minecraft.client.Minecraft.getInstance().setScreen(
                                AdvancedTimerConfigScreen.create(null)
                            )
                        }
                        Command.SINGLE_SUCCESS
                    }).then(timeCommand)
            )
        }
    }
}
