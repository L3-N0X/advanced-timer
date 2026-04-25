package de.lenox.client

import net.minecraft.client.Minecraft
//? if >=26.1
import net.minecraft.client.gui.GuiGraphicsExtractor
//? if <26.1
//import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

object TimerHudRenderer {
    var isVisible: Boolean = false

    //? if >=26.1
    fun render(graphics: GuiGraphicsExtractor) {
        //? if <26.1
        //fun render(graphics: GuiGraphics) {
        if (!isVisible) return

        GlobalConfigManager.ensureLoaded()

        val mc = Minecraft.getInstance()
        val font = mc.font

        val config = GlobalConfigManager.config

        if (TimerManager.isRunning && TimerManager.currentData.direction == TimerDirection.DOWN) {
            if (TimerManager.currentTimeMs <= 0L) {
                TimerManager.pause()
            }
        }

        val isPaused = !TimerManager.isRunning && TimerManager.currentTimeMs > 0L

        val timeString = config.timerFormat.format(TimerManager.currentTimeMs)
        var textString = timeString

        if (isPaused && config.showPausedState) {
            textString += config.pausedSuffix
        }

        var textComponent = Component.literal(textString)
        if (config.timerBold) {
            textComponent = textComponent.withStyle(net.minecraft.ChatFormatting.BOLD)
        }

        val width = font.width(textComponent)
        val overrideColor = isPaused && config.changePausedColor
        val color = (if (overrideColor) config.pausedColor else config.timerColor) or 0xFF000000.toInt()

        val x: Int
        val y: Int

        when (config.timerPosition) {
            TimerPosition.TOP_CENTER -> {
                x = (graphics.guiWidth() - width) / 2
                y = 10
            }

            TimerPosition.BELOW_ACTION_BAR -> {
                x = (graphics.guiWidth() - width) / 2
                y = graphics.guiHeight() - 50
            }

            TimerPosition.ABOVE_ACTION_BAR -> {
                x = (graphics.guiWidth() - width) / 2
                y = graphics.guiHeight() - 80
            }

            TimerPosition.TOP_LEFT -> {
                x = 10
                y = 10
            }

            TimerPosition.TOP_RIGHT -> {
                x = graphics.guiWidth() - width - 10
                y = 10
            }

            TimerPosition.CUSTOM -> {
                if (config.customPosLeft > 0) {
                    if (config.customPosLeft == 50) x = (graphics.guiWidth() - width) / 2
                    else x = graphics.guiWidth() * config.customPosLeft / 100
                } else if (config.customPosRight > 0) {
                    if (config.customPosRight == 50) x = (graphics.guiWidth() - width) / 2
                    else x = graphics.guiWidth() - width - (graphics.guiWidth() * config.customPosRight / 100)
                } else {
                    x = (graphics.guiWidth() - width) / 2
                }

                if (config.customPosTop > 0) {
                    if (config.customPosTop == 50) y = (graphics.guiHeight() - font.lineHeight) / 2
                    else y = graphics.guiHeight() * config.customPosTop / 100
                } else if (config.customPosBottom > 0) {
                    if (config.customPosBottom == 50) y = (graphics.guiHeight() - font.lineHeight) / 2
                    else y =
                        graphics.guiHeight() - font.lineHeight - (graphics.guiHeight() * config.customPosBottom / 100)
                } else {
                    y = 10
                }
            }
        }

        if (config.enableGradient && !overrideColor) {
            var currentX = x.toFloat()
            val time = System.currentTimeMillis()

            for (i in textString.indices) {
                val char = textString[i].toString()

                var charComponent = Component.literal(char)
                if (config.timerBold) {
                    charComponent = charComponent.withStyle(net.minecraft.ChatFormatting.BOLD)
                }

                val offsetProgress = if (width > 0) (currentX - x) / width.toFloat() else 0f
                var factor = offsetProgress.toDouble() * (config.gradientVisibility / 100.0) - 0.25

                if (config.animateGradient) {
                    val timeModulo = time % 3000000L
                    val timeProgress = (timeModulo.toDouble() * config.animationSpeed.toDouble() / 3000.0)
                    factor -= timeProgress
                }

                val sineFactor = (Math.sin(factor * Math.PI * 2.0) + 1.0) / 2.0

                val c1 = config.timerColor
                val c2 = config.secondTimerColor

                val r = ((c1 shr 16 and 0xFF) + ((c2 shr 16 and 0xFF) - (c1 shr 16 and 0xFF)) * sineFactor).toInt()
                val g = ((c1 shr 8 and 0xFF) + ((c2 shr 8 and 0xFF) - (c1 shr 8 and 0xFF)) * sineFactor).toInt()
                val b = ((c1 and 0xFF) + ((c2 and 0xFF) - (c1 and 0xFF)) * sineFactor).toInt()

                val finalColor = 0xFF000000.toInt() or (r shl 16) or (g shl 8) or b

                //? if >=26.1
                graphics.text(font, charComponent, currentX.toInt(), y, finalColor, true)
                //? if <26.1
                //graphics.drawString(font, charComponent, currentX.toInt(), y, finalColor, true)
                currentX += font.width(charComponent)
            }
        } else {
            //? if >=26.1
            graphics.text(font, textComponent, x, y, color, true)
            //? if <26.1
            //graphics.drawString(font, textComponent, x, y, color, true)
        }
    }
}
