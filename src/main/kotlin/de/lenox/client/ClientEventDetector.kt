package de.lenox.client

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.boss.enderdragon.EnderDragon
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.ElderGuardian
import net.minecraft.world.entity.monster.warden.Warden

object ClientEventDetector {
    private var wasDead = false
    private val seenDyingEntities = mutableSetOf<Int>()
    private var wasCountdownZero = false

    fun tick(mc: Minecraft) {
        val player = mc.player ?: return
        val level = mc.level ?: return

        val config = GlobalConfigManager.config

        // 0. Check countdown reach 0
        if (TimerManager.currentData.direction == TimerDirection.DOWN) {
            if (TimerManager.currentTimeMs <= 0L) {
                if (!wasCountdownZero) {
                    wasCountdownZero = true
                    when (config.timerEndAction) {
                        TimerEndAction.OFF -> {}
                        TimerEndAction.CHAT -> {
                            player.displayClientMessage(
                                    net.minecraft.network.chat.Component.literal(
                                                    "The countdown timer has reached 0!"
                                            )
                                            .withColor(0xff8579),false
                            )
                        }
                        TimerEndAction.TITLE -> {
                            mc.gui.setTitle(
                                    net.minecraft.network.chat.Component.literal("Timer stopped!")
                                            .withColor(0xff8579)
                            )
                            mc.gui.setSubtitle(net.minecraft.network.chat.Component.empty())
                            mc.gui.setTimes(10, 70, 20)
                        }
                    }
                }
            } else {
                wasCountdownZero = false
            }
        } else {
            wasCountdownZero = false
        }

        // 1. Check player death
        val isDead = player.isDeadOrDying
        if (isDead && !wasDead) {
            if (config.autoPauseOnDeath && TimerManager.isRunning) {
                TimerManager.pause()
            }
        }
        wasDead = isDead

        // 2. Check boss kills
        if (!config.autoPauseOnDragonKill &&
                        !config.autoPauseOnWitherKill &&
                        !config.autoPauseOnElderGuardianKill &&
                        !config.autoPauseOnWardenKill
        ) {
            return
        }

        val entities = level.getEntities(null, player.boundingBox.inflate(256.0))
        for (entity in entities) {
            if (entity is LivingEntity && entity.isDeadOrDying) {
                if (seenDyingEntities.add(entity.id)) {
                    var shouldPause = false
                    if (config.autoPauseOnDragonKill && entity is EnderDragon) {
                        shouldPause = true
                    } else if (config.autoPauseOnWitherKill && entity is WitherBoss) {
                        shouldPause = true
                    } else if (config.autoPauseOnElderGuardianKill && entity is ElderGuardian) {
                        shouldPause = true
                    } else if (config.autoPauseOnWardenKill && entity is Warden) {
                        shouldPause = true
                    }

                    if (shouldPause && TimerManager.isRunning) {
                        TimerManager.pause()
                    }
                }
            }
        }
    }

    fun reset() {
        wasDead = false
        seenDyingEntities.clear()
        wasCountdownZero = false
    }
}
