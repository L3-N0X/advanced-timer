package de.lenox.client

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.YetAnotherConfigLib
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object AdvancedTimerConfigScreen {
    fun create(parent: Screen?): Screen {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Advanced Timer Config"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .build())
            .build()
            .generateScreen(parent)
    }
}
