package de.lenox.client

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import java.awt.Color
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object AdvancedTimerConfigScreen {
    fun create(parent: Screen?): Screen {
        GlobalConfigManager.ensureLoaded()

        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Advanced Timer Config"))
            .save(GlobalConfigManager::save)
            .category(
                ConfigCategory.createBuilder().name(Component.literal("Appearance")).group(
                    OptionGroup.createBuilder().name(Component.literal("Text Colors"))
                        .option(
                            Option.createBuilder<Color>().name(
                                Component.literal(
                                    "Timer Color"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Sets the color of the timer text."
                                    )
                                )
                            ).binding(Color(0x99DE5D), {
                                Color(
                                    GlobalConfigManager.config.timerColor
                                )
                            }, { newVal ->
                                GlobalConfigManager.config.timerColor = newVal.rgb
                            }).controller { opt ->
                                ColorControllerBuilder.create(
                                    opt
                                ).allowAlpha(false)
                            }.build()
                        ).option(
                            Option.createBuilder<Color>().name(
                            Component.literal(
                                "Second Timer Color"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Sets the second color of the timer text for the gradient."
                                )
                            )
                        ).binding(Color(0x22A6E5), {
                            Color(
                                GlobalConfigManager.config.secondTimerColor
                            )
                        }, { newVal ->
                            GlobalConfigManager.config.secondTimerColor = newVal.rgb
                        }).controller { opt ->
                            ColorControllerBuilder.create(
                                opt
                            ).allowAlpha(false)
                        }.build()
                        ).build()
                )
                    .group(
                    OptionGroup.createBuilder().name(Component.literal("Gradient Animation"))
                        .option(
                            Option.createBuilder<Boolean>().name(
                                Component.literal(
                                    "Enable Gradient"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Enables the gradient for the timer text."
                                    )
                                )
                            ).binding(true, {
                                GlobalConfigManager.config.enableGradient
                            }, { newVal ->
                                GlobalConfigManager.config.enableGradient = newVal
                            }).controller { opt ->
                                TickBoxControllerBuilder.create(
                                    opt
                                )
                            }.build()
                        ).option(
                            Option.createBuilder<Boolean>().name(
                            Component.literal(
                                "Animate Gradient"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Animates the gradient to flow across the text."
                                )
                            )
                        ).binding(true, {
                            GlobalConfigManager.config.animateGradient
                        }, { newVal ->
                            GlobalConfigManager.config.animateGradient = newVal
                        }).controller { opt ->
                            TickBoxControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).option(
                            Option.createBuilder<Float>().name(
                            Component.literal(
                                "Animation Speed"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Controls the speed of the gradient animation."
                                )
                            )
                        ).binding(1.0f, {
                            GlobalConfigManager.config.animationSpeed
                        }, { newVal ->
                            GlobalConfigManager.config.animationSpeed = newVal
                        }).controller { opt ->
                            FloatSliderControllerBuilder.create(opt).range(0.1f, 10.0f).step(0.1f)
                        }.build()
                        ).option(
                            Option.createBuilder<Int>().name(
                            Component.literal(
                                "Gradient Visibility (%)"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Controls how much of the gradient is visible at a time."
                                )
                            )
                        ).binding(50, {
                            GlobalConfigManager.config.gradientVisibility
                        }, { newVal ->
                            GlobalConfigManager.config.gradientVisibility = newVal
                        }).controller { opt ->
                            IntegerSliderControllerBuilder.create(opt).range(0, 300).step(1)
                        }.build()
                        ).build()
                ).group(
                    OptionGroup.createBuilder().name(Component.literal("Timer Style"))
                        .option(
                            Option.createBuilder<TimerFormat>().name(
                                Component.literal(
                                    "Timer Format"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Select the format for displaying the timer."
                                    )
                                )
                            ).binding(TimerFormat.HH_MM_SS, {
                                GlobalConfigManager.config.timerFormat
                            }, { newVal ->
                                GlobalConfigManager.config.timerFormat = newVal
                            }).controller { opt ->
                                EnumControllerBuilder.create(
                                    opt
                                ).enumClass(
                                    TimerFormat::class.java
                                ).formatValue {
                                    Component.literal(
                                        it.displayName
                                    )
                                }
                            }.build()
                        ).option(
                            Option.createBuilder<Boolean>().name(
                            Component.literal(
                                "Bold Text"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Makes the timer text bold."
                                )
                            )
                        ).binding(true, {
                            GlobalConfigManager.config.timerBold
                        }, { newVal ->
                            GlobalConfigManager.config.timerBold = newVal
                        }).controller { opt ->
                            TickBoxControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).build()
                ).group(
                    OptionGroup.createBuilder().name(Component.literal("Paused Appearance"))
                        .option(
                            Option.createBuilder<Boolean>().name(
                                Component.literal(
                                    "Show Paused State"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Appends a suffix when the timer is paused."
                                    )
                                )
                            ).binding(false, {
                                GlobalConfigManager.config.showPausedState
                            }, { newVal ->
                                GlobalConfigManager.config.showPausedState = newVal
                            }).controller { opt ->
                                TickBoxControllerBuilder.create(
                                    opt
                                )
                            }.build()
                        ).option(
                            Option.createBuilder<String>().name(
                            Component.literal(
                                "Paused Suffix"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "The text added to the end of the timer when paused."
                                )
                            )
                        ).binding(" (paused)", {
                            GlobalConfigManager.config.pausedSuffix
                        }, { newVal ->
                            GlobalConfigManager.config.pausedSuffix = newVal
                        }).controller { opt ->
                            StringControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).option(
                            Option.createBuilder<Boolean>().name(
                            Component.literal(
                                "Change Paused Color"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Changes the color of the timer when paused."
                                )
                            )
                        ).binding(false, {
                            GlobalConfigManager.config.changePausedColor
                        }, { newVal ->
                            GlobalConfigManager.config.changePausedColor = newVal
                        }).controller { opt ->
                            TickBoxControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).option(
                            Option.createBuilder<Color>().name(
                            Component.literal(
                                "Paused Color"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "The color of the timer when paused."
                                )
                            )
                        ).binding(Color(0xAAAAAA), {
                            Color(
                                GlobalConfigManager.config.pausedColor
                            )
                        }, { newVal ->
                            GlobalConfigManager.config.pausedColor = newVal.rgb
                        }).controller { opt ->
                            ColorControllerBuilder.create(
                                opt
                            ).allowAlpha(false)
                        }.build()
                        ).build()
                ).build()
            ).category(
                ConfigCategory.createBuilder().name(Component.literal("Events & Behavior")).group(
                    OptionGroup.createBuilder().name(Component.literal("Timer Mode"))
                        .option(
                            Option.createBuilder<TimerDirection>().name(
                                Component.literal(
                                    "Timer Direction"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Set whether the timer counts up or down."
                                    )
                                )
                            ).binding(TimerDirection.UP, {
                                if (net.minecraft.client.Minecraft.getInstance().level != null) TimerManager.currentData.direction
                                else GlobalConfigManager.config.timerDirection
                            }, { newVal ->
                                GlobalConfigManager.config.timerDirection = newVal
                                if (net.minecraft.client.Minecraft.getInstance().level != null) {
                                    TimerManager.currentData.direction = newVal
                                    TimerManager.save()
                                }
                            }).controller { opt ->
                                EnumControllerBuilder.create(
                                    opt
                                ).enumClass(
                                    TimerDirection::class.java
                                ).formatValue {
                                    Component.literal(
                                        it.displayName
                                    )
                                }
                            }.build()
                        )
                        .option(
                            Option.createBuilder<Int>().name(
                                Component.literal(
                                    "Countdown Hours"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "The number of hours to count down from."
                                    )
                                )
                            ).binding(1, {
                                if (net.minecraft.client.Minecraft.getInstance().level != null) TimerManager.currentData.countdownHours
                                else GlobalConfigManager.config.countdownHours
                            }, { newVal ->
                                GlobalConfigManager.config.countdownHours = newVal
                                if (net.minecraft.client.Minecraft.getInstance().level != null) {
                                    TimerManager.currentData.countdownHours = newVal
                                    TimerManager.save()
                                }
                            }).controller { opt ->
                                IntegerSliderControllerBuilder.create(opt).range(0, 99).step(1)
                            }.build()
                        ).option(
                            Option.createBuilder<Int>().name(
                            Component.literal(
                                "Countdown Minutes"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "The number of minutes to count down from."
                                )
                            )
                        ).binding(0, {
                            if (net.minecraft.client.Minecraft.getInstance().level != null) TimerManager.currentData.countdownMinutes
                            else GlobalConfigManager.config.countdownMinutes
                        }, { newVal ->
                            GlobalConfigManager.config.countdownMinutes = newVal
                            if (net.minecraft.client.Minecraft.getInstance().level != null) {
                                TimerManager.currentData.countdownMinutes = newVal
                                TimerManager.save()
                            }
                        }).controller { opt ->
                            IntegerSliderControllerBuilder.create(opt).range(0, 59).step(1)
                        }.build()
                        ).option(
                            Option.createBuilder<Int>().name(
                            Component.literal(
                                "Countdown Seconds"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "The number of seconds to count down from."
                                )
                            )
                        ).binding(0, {
                            if (net.minecraft.client.Minecraft.getInstance().level != null) TimerManager.currentData.countdownSeconds
                            else GlobalConfigManager.config.countdownSeconds
                        }, { newVal ->
                            GlobalConfigManager.config.countdownSeconds = newVal
                            if (net.minecraft.client.Minecraft.getInstance().level != null) {
                                TimerManager.currentData.countdownSeconds = newVal
                                TimerManager.save()
                            }
                        }).controller { opt ->
                            IntegerSliderControllerBuilder.create(opt).range(0, 59).step(1)
                        }.build()
                        ).build()
                ).group(
                    OptionGroup.createBuilder().name(Component.literal("General Behavior"))
                        .option(
                            Option.createBuilder<Boolean>().name(
                                Component.literal(
                                    "Command Feedback"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Shows a chat message when a command is executed."
                                    )
                                )
                            ).binding(true, {
                                GlobalConfigManager.config.showCommandFeedback
                            }, { newVal ->
                                GlobalConfigManager.config.showCommandFeedback = newVal
                            }).controller { opt ->
                                TickBoxControllerBuilder.create(
                                    opt
                                )
                            }.build()
                        ).option(
                            Option.createBuilder<TimerEndAction>().name(
                            Component.literal(
                                "Timer End Action"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Selects what should happen when a countdown timer reaches 0."
                                )
                            )
                        ).binding(TimerEndAction.CHAT, {
                            GlobalConfigManager.config.timerEndAction
                        }, { newVal ->
                            GlobalConfigManager.config.timerEndAction = newVal
                        }).controller { opt ->
                            EnumControllerBuilder.create(
                                opt
                            ).enumClass(
                                TimerEndAction::class.java
                            ).formatValue {
                                Component.literal(
                                    it.displayName
                                )
                            }
                        }.build()
                        ).option(
                            Option.createBuilder<Boolean>().name(
                            Component.literal(
                                "Pause on Leave"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Automatically pause the timer when leaving the world or server."
                                )
                            )
                        ).binding(true, {
                            GlobalConfigManager.config.pauseOnLeave
                        }, { newVal ->
                            GlobalConfigManager.config.pauseOnLeave = newVal
                        }).controller { opt ->
                            TickBoxControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).build()
                ).group(
                    OptionGroup.createBuilder().name(Component.literal("Player Events"))
                        .option(
                            Option.createBuilder<Boolean>().name(
                                Component.literal(
                                    "Auto-Pause on Player Death"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Automatically pauses the timer when the player dies."
                                    )
                                )
                            ).binding(false, {
                                GlobalConfigManager.config.autoPauseOnDeath
                            }, { newVal ->
                                GlobalConfigManager.config.autoPauseOnDeath = newVal
                            }).controller { opt ->
                                TickBoxControllerBuilder.create(
                                    opt
                                )
                            }.build()
                        ).build()
                ).group(
                    OptionGroup.createBuilder().name(Component.literal("Boss Events"))
                        .option(
                            Option.createBuilder<Boolean>().name(
                                Component.literal(
                                    "Auto-Pause on Ender Dragon Kill"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Automatically pauses the timer when the Ender Dragon is killed."
                                    )
                                )
                            ).binding(false, {
                                GlobalConfigManager.config.autoPauseOnDragonKill
                            }, { newVal ->
                                GlobalConfigManager.config.autoPauseOnDragonKill = newVal
                            }).controller { opt ->
                                TickBoxControllerBuilder.create(
                                    opt
                                )
                            }.build()
                        ).option(
                            Option.createBuilder<Boolean>().name(
                            Component.literal(
                                "Auto-Pause on Wither Kill"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Automatically pauses the timer when a Wither is killed."
                                )
                            )
                        ).binding(false, {
                            GlobalConfigManager.config.autoPauseOnWitherKill
                        }, { newVal ->
                            GlobalConfigManager.config.autoPauseOnWitherKill = newVal
                        }).controller { opt ->
                            TickBoxControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).option(
                            Option.createBuilder<Boolean>().name(
                            Component.literal(
                                "Auto-Pause on Elder Guardian Kill"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Automatically pauses the timer when an Elder Guardian is killed."
                                )
                            )
                        ).binding(false, {
                            GlobalConfigManager.config.autoPauseOnElderGuardianKill
                        }, { newVal ->
                            GlobalConfigManager.config.autoPauseOnElderGuardianKill = newVal
                        }).controller { opt ->
                            TickBoxControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).option(
                            Option.createBuilder<Boolean>().name(
                            Component.literal(
                                "Auto-Pause on Warden Kill"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Automatically pauses the timer when a Warden is killed."
                                )
                            )
                        ).binding(false, {
                            GlobalConfigManager.config.autoPauseOnWardenKill
                        }, { newVal ->
                            GlobalConfigManager.config.autoPauseOnWardenKill = newVal
                        }).controller { opt ->
                            TickBoxControllerBuilder.create(
                                opt
                            )
                        }.build()
                        ).build()
                ).build()
            ).category(
                ConfigCategory.createBuilder().name(Component.literal("Position")).group(
                    OptionGroup.createBuilder().name(Component.literal("Layout"))
                        .option(
                            Option.createBuilder<TimerPosition>().name(
                                Component.literal(
                                    "Timer Position"
                                )
                            ).description(
                                OptionDescription.of(
                                    Component.literal(
                                        "Select where the timer should be displayed. Custom mode uses the percentage offsets below (0-100)."
                                    )
                                )
                            ).binding(TimerPosition.ABOVE_ACTION_BAR, {
                                GlobalConfigManager.config.timerPosition
                            }, { newVal ->
                                GlobalConfigManager.config.timerPosition = newVal
                            }).controller { opt ->
                                EnumControllerBuilder.create(
                                    opt
                                ).enumClass(
                                    TimerPosition::class.java
                                ).formatValue {
                                    Component.literal(
                                        it.displayName
                                    )
                                }
                            }.build()
                        ).option(
                            Option.createBuilder<Int>().name(
                            Component.literal(
                                "Custom Top Offset (%)"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Percentage from the top. 50 places it in the middle."
                                )
                            )
                        ).binding(0, {
                            GlobalConfigManager.config.customPosTop
                        }, { newVal ->
                            GlobalConfigManager.config.customPosTop = newVal
                        }).controller { opt ->
                            IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1)
                        }.build()
                        ).option(
                            Option.createBuilder<Int>().name(
                            Component.literal(
                                "Custom Left Offset (%)"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Percentage from the left. 50 places it in the middle. (Overrides Right offset)"
                                )
                            )
                        ).binding(0, {
                            GlobalConfigManager.config.customPosLeft
                        }, { newVal ->
                            GlobalConfigManager.config.customPosLeft = newVal
                        }).controller { opt ->
                            IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1)
                        }.build()
                        ).option(
                            Option.createBuilder<Int>().name(
                            Component.literal(
                                "Custom Right Offset (%)"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Percentage from the right. 50 places it in the middle. (Ignored if Left > 0)"
                                )
                            )
                        ).binding(0, {
                            GlobalConfigManager.config.customPosRight
                        }, { newVal ->
                            GlobalConfigManager.config.customPosRight = newVal
                        }).controller { opt ->
                            IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1)
                        }.build()
                        ).option(
                            Option.createBuilder<Int>().name(
                            Component.literal(
                                "Custom Bottom Offset (%)"
                            )
                        ).description(
                            OptionDescription.of(
                                Component.literal(
                                    "Percentage from the bottom. 50 places it in the middle. (Ignored if Top > 0)"
                                )
                            )
                        ).binding(0, {
                            GlobalConfigManager.config.customPosBottom
                        }, { newVal ->
                            GlobalConfigManager.config.customPosBottom = newVal
                        }).controller { opt ->
                            IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1)
                        }.build()
                        ).build()
                ).build()
            ).build().generateScreen(parent)
    }
}
