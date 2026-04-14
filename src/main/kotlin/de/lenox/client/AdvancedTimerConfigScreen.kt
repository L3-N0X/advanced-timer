package de.lenox.client

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color

object AdvancedTimerConfigScreen {
    fun create(parent: Screen?): Screen {
        GlobalConfigManager.ensureLoaded()

        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Advanced Timer Config"))
            .save(GlobalConfigManager::save)
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .option(Option.createBuilder<Color>()
                    .name(Component.literal("Timer Color"))
                    .description(OptionDescription.of(Component.literal("Sets the color of the timer text.")))
                    .binding(
                        Color.WHITE,
                        { Color(GlobalConfigManager.config.timerColor) },
                        { newVal -> GlobalConfigManager.config.timerColor = newVal.rgb }
                    )
                    .controller { opt -> ColorControllerBuilder.create(opt).allowAlpha(false) }
                    .build()
                )
                .option(Option.createBuilder<Boolean>()
                    .name(Component.literal("Bold Text"))
                    .description(OptionDescription.of(Component.literal("Makes the timer text bold.")))
                    .binding(
                        false,
                        { GlobalConfigManager.config.timerBold },
                        { newVal -> GlobalConfigManager.config.timerBold = newVal }
                    )
                    .controller { opt -> TickBoxControllerBuilder.create(opt) }
                    .build()
                )
                .build())
            .build()
            .generateScreen(parent)
    }
}
