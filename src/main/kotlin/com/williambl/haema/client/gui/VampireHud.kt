package com.williambl.haema.client.gui

import com.williambl.haema.Vampirable
import com.williambl.haema.VampireBloodManager
import com.williambl.haema.VampireBloodManager.Companion.goodBloodTag
import com.williambl.haema.VampireBloodManager.Companion.mediumBloodTag
import com.williambl.haema.VampireBloodManager.Companion.poorBloodTag
import com.williambl.haema.abilities.VampireAbility
import com.williambl.haema.client.BAT_FORM_KEY
import com.williambl.haema.client.ClientVampire
import com.williambl.haema.client.DASH_KEY
import com.williambl.haema.client.config
import com.williambl.haema.client.config.HudPlacement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult

object VampireHud : DrawableHelper() {
    fun render(matrixStack: MatrixStack, @Suppress("UNUSED_PARAMETER") tickDelta: Float) {
        if (config.vampireHudPlacement == HudPlacement.NONE) return

        matrixStack.push()

        val mc = MinecraftClient.getInstance()
        val width = mc.window.scaledWidth
        val height = mc.window.scaledHeight
        val textRenderer = mc.textRenderer
        val player = mc.player

        if (!(player as Vampirable).isVampire)
            return

        (player as Vampirable).checkBloodManager()

        val texts = mutableListOf<Text>()

        val dashLevel = (player as Vampirable).getAbilityLevel(VampireAbility.DASH)
        if (dashLevel > 0 && (player.hungerManager as VampireBloodManager).getBloodLevel() > 18f) {
            texts.add(createText(
                DASH_KEY.boundKeyLocalizedText.copy(),
                (player as Vampirable).isVampire && (player as ClientVampire).canDash(),
                TranslatableText("gui.haema.hud.vampiredash"))
            )
        }

        val invisLevel = (player as Vampirable).getAbilityLevel(VampireAbility.INVISIBILITY)
        if (invisLevel > 0 && (player.hungerManager as VampireBloodManager).getBloodLevel() >= 18f) {
            texts.add(createText(
                MinecraftClient.getInstance().options.keySneak.boundKeyLocalizedText.copy(),
                (player as Vampirable).isVampire && player.world.time-(player.hungerManager as VampireBloodManager).invisTicks >= 120 + invisLevel*20,
                TranslatableText("gui.haema.hud.invisibility"))
            )
        }

        val batLevel = (player as Vampirable).getAbilityLevel(VampireAbility.BAT_FORM)
        if (batLevel > 0) {
            texts.add(createText(
                BAT_FORM_KEY.boundKeyLocalizedText.copy(),
                (player as Vampirable).isVampire && (player as ClientVampire).canUseBatForm(),
                TranslatableText("gui.haema.hud.bat_form")
            ))
        }

        if (mc.crosshairTarget != null && mc.crosshairTarget!!.type == HitResult.Type.ENTITY) {
            val lookingAt = (mc.crosshairTarget as EntityHitResult).entity.type
            if (poorBloodTag.contains(lookingAt) || mediumBloodTag.contains(lookingAt) || goodBloodTag.contains(lookingAt)) {
                if (player.isSneaking) {
                    texts.add(
                        TranslatableText("gui.haema.hud.bloodquality").append(
                            when {
                                goodBloodTag.contains(lookingAt) -> TranslatableText("gui.haema.hud.bloodquality.good").formatted(
                                    Formatting.GREEN
                                )
                                mediumBloodTag.contains(lookingAt) -> TranslatableText("gui.haema.hud.bloodquality.medium").formatted(
                                    Formatting.YELLOW
                                )
                                else -> TranslatableText("gui.haema.hud.bloodquality.poor").formatted(Formatting.RED)
                            }
                        )
                    )
                }

                texts.add(
                    createText(
                        TranslatableText("key.sneak").append(" + ").append(mc.options.keyUse.boundKeyLocalizedText),
                        true,
                        TranslatableText("gui.haema.hud.feed")
                    )
                )
            }
        }

        texts.forEachIndexed { index, text ->
            drawCenteredText(
                    matrixStack,
                    MinecraftClient.getInstance().textRenderer,
                    text,
                    config.vampireHudPlacement.x(width, textRenderer.getWidth(text)),
                    config.vampireHudPlacement.y(height,index),
                    0xffffff
            )
        }
        matrixStack.pop()
    }

    private fun createText(key: MutableText, available: Boolean, description: Text): Text {
        val keyText = key.formatted(if (available) Formatting.GREEN else Formatting.RED)
        return LiteralText("[").append(keyText).append("] ").append(description)
    }
}