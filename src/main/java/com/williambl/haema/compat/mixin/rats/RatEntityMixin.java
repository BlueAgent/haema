package com.williambl.haema.compat.mixin.rats;

import com.williambl.haema.Vampirable;
import com.williambl.haema.compat.rats.VampiRatAttackGoal;
import com.williambl.haema.component.VampireComponent;
import com.williambl.haema.damagesource.SunlightDamageSource;
import com.williambl.haema.effect.VampiricStrengthEffect;
import com.williambl.haema.util.HaemaGameRulesKt;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RatEntity.class)
public abstract class RatEntityMixin extends TameableEntity implements Vampirable {

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    protected RatEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    void addVampireGoal(CallbackInfo ci) {
        goalSelector.add(3, new VampiRatAttackGoal((RatEntity) (Object) this, 1.0, true));

        targetSelector.add(8, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, (livingEntity) ->
                this.isVampire() && !(livingEntity instanceof Vampirable) && !this.hasStatusEffect(VampiricStrengthEffect.Companion.getInstance())
        ));
        targetSelector.add(9, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, (livingEntity) ->
                this.isVampire() && livingEntity instanceof Vampirable && !((Vampirable) livingEntity).isVampire()
        ));
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    void vampireTick(CallbackInfo ci) {
        if (isVampire()) {
            if (world.isDay() && !world.isRaining() && world.isSkyVisible(getBlockPos()) && world.getGameRules().getBoolean(HaemaGameRulesKt.getVampiresBurn())) {
                if (age % 10 == 0) {
                    damage(SunlightDamageSource.Companion.getInstance(), 0.2f);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, getX() - 0.5, getY(), getZ() - 0.5, 20, 0.2, 0.2, 0.2, 0.1);
                }
            }

            if (getTarget() instanceof Vampirable && ((Vampirable) getTarget()).isVampire()) {
                setTarget(null);
            }
        }
    }

    @Override
    public void setVampire(boolean value) {
        if (!hasCustomName())
            setCustomName(new LiteralText(random.nextFloat() < 0.02 ? (random.nextBoolean() ? "Count D-Rat-Cula" : "Capri-Sun") : "VampiRat").formatted(Formatting.DARK_RED));
        VampireComponent.Companion.getEntityKey().get(this).setVampire(value);
    }

    @Override
    public void checkBloodManager() {
    }

    @Override
    public void removeBloodManager() {
    }
}
