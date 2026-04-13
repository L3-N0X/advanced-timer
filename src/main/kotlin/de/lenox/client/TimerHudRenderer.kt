package de.lenox.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object TimerHudRenderer {
    var isVisible: Boolean = false

    fun render(graphics: GuiGraphics) {
        if (!isVisible) return

        val mc = Minecraft.getInstance()
        val font = mc.font
        val text = "00:00 - Timer"
        val width = font.width(text)
        
        val x = (graphics.guiWidth() - width) / 2
        val y = graphics.guiHeight() - 80
        
        graphics.drawString(font, text, x, y, -1, true)
    }
}
