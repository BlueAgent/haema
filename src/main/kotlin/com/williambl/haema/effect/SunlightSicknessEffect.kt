package com.williambl.haema.effect

import com.williambl.haema.VampireBloodManager
import com.williambl.haema.damagesource.SunlightDamageSource
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes

class SunlightSicknessEffect(type: StatusEffectCategory?, color: Int) : StatusEffect(type, color) {

    companion object {
        val instance: StatusEffect = SunlightSicknessEffect(StatusEffectCategory.HARMFUL, 245 shl 24 or 167 shl 16 or 66 shl 8)
                .addAttributeModifier(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        "c85d1cfe-2c10-4d25-b650-49c045979842",
                        -4.0,
                        EntityAttributeModifier.Operation.ADDITION
                )
    }

    override fun applyUpdateEffect(entity: LivingEntity?, amplifier: Int) {
        if (entity !is PlayerEntity)
            return

        if (entity.age % 10 == 0) {
            entity.damage(SunlightDamageSource.instance, 0.5f)
            (entity.hungerManager as VampireBloodManager).removeBlood(0.25)
            val pos = entity.pos
            val rand = entity.random
            for (i in 0..10) {
                entity.world.addParticle(ParticleTypes.FLAME, pos.x-0.5+rand.nextDouble(), pos.y+rand.nextDouble()*2, pos.z-0.5+rand.nextDouble(), 0.0, 0.0, 0.0)
            }
        }
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return true
    }
}