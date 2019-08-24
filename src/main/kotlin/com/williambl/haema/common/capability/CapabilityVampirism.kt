package com.williambl.haema.common.capability

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTPrimitive
import net.minecraft.nbt.NBTTagFloat
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable

interface ICapabilityVampirism {
    fun getBloodthirst(): Float
    fun setBloodthirst(input: Float)
    fun addBloodthirst(input: Float)
}

class CapabilityVampirismImpl: ICapabilityVampirism {

    private var bloodthirst: Float = 0.0f

    override fun getBloodthirst(): Float {
        return bloodthirst
    }

    override fun setBloodthirst(input: Float) {
        bloodthirst = input
        if (bloodthirst < 0.0f) bloodthirst = 0.0f
    }

    override fun addBloodthirst(input: Float) {
        bloodthirst += input
        if (bloodthirst < 0.0f) bloodthirst = 0.0f
    }
}

class VampirismStorage: Capability.IStorage<ICapabilityVampirism> {

    override fun readNBT(capability: Capability<ICapabilityVampirism>?, instance: ICapabilityVampirism?, side: EnumFacing?, nbt: NBTBase?) {
        instance?.setBloodthirst((nbt as NBTPrimitive).float)
    }

    override fun writeNBT(capability: Capability<ICapabilityVampirism>?, instance: ICapabilityVampirism?, side: EnumFacing?): NBTBase? {
        return instance?.getBloodthirst()?.let { NBTTagFloat(it) }
    }

}

class VampirismProvider : ICapabilitySerializable<NBTBase> {

    companion object {
        @CapabilityInject(ICapabilityVampirism::class)
        val vampirism: Capability<ICapabilityVampirism>? = null
    }

    private val instance: ICapabilityVampirism = vampirism!!.defaultInstance!!

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == vampirism)
            vampirism.cast(instance)
        else
            null
    }

    override fun deserializeNBT(nbt: NBTBase?) {
        vampirism!!.storage.readNBT(vampirism, instance, null, nbt)
    }

    override fun serializeNBT(): NBTBase {
        return vampirism!!.storage.writeNBT(vampirism, instance, null)!!
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == vampirism
    }

}