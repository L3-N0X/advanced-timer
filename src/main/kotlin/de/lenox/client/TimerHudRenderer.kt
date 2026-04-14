package de.lenox.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

object TimerHudRenderer {
    var isVisible: Boolean = false

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun render(graphics: GuiGraphics) {
        if (!isVisible) return

        GlobalConfigManager.ensureLoaded()

        val mc = Minecraft.getInstance()
        val font = mc.font
        
        val timeString = formatTime(TimerManager.currentTimeMs)
        val textString = "$timeString - Timer"
        
        var textComponent = Component.literal(textString)
        if (GlobalConfigManager.config.timerBold) {
            textComponent = textComponent.withStyle(net.minecraft.ChatFormatting.BOLD)
        }
        
        val width = font.width(textComponent)
        val color = GlobalConfigManager.config.timerColor
        
        val x = (graphics.guiWidth() - width) / 2
        val y = graphics.guiHeight() - 80
        
        graphics.drawString(font, textComponent, x, y, color, true)
    }
}
