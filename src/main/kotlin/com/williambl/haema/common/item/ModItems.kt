package com.williambl.haema.common.item

import com.williambl.haema.common.util.addBlood
import com.williambl.haema.common.util.getVampirismCapability
import com.williambl.haema.common.util.giveVampiricStrength
import com.williambl.haema.objectholder.ModEffectHolder
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.DamageSource
import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@KotlinEventBusSubscriber(bus = KotlinEventBusSubscriber.Bus.MOD)
object ModItems {

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(
                object : Item(Properties().group(ItemGroup.MATERIALS)) {
                    @OnlyIn(Dist.CLIENT)
                    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
                        tooltip.add(StringTextComponent("An ancient bottle of blood from an extinct race...").applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.ITALIC))
                    }
                }.setRegistryName("vampire_blood_vial"),
                object : Item(Properties().group(ItemGroup.BREWING).maxStackSize(1)) {
                    @OnlyIn(Dist.CLIENT)
                    fun addInformation(stack: ItemStack, worldIn: IBlockReader?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
                        tooltip.add(StringTextComponent("Inject it and revive the ancient race of vampires!").applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.ITALIC))
                    }

                    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
                        if (worldIn.isRemote)
                            return ActionResult(ActionResultType.PASS, playerIn.getHeldItem(handIn))
                        playerIn.getVampirismCapability().ifPresent {
                            if (!it.isVampire()) {
                                if (playerIn.getActivePotionEffect(ModEffectHolder.weakness) != null) {
                                    it.setIsVampire(true)
                                    playerIn.addBlood(0.5f)
                                    playerIn.giveVampiricStrength(200, 5)
                                } else {
                                    playerIn.attackEntityFrom(DamageSource.MAGIC, 20.0f)
                                }
                            }
                            playerIn.getHeldItem(handIn).count = 0
                        }
                        return ActionResult(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn))
                    }
                }.setRegistryName("vampire_blood_syringe")
        )
    }
}