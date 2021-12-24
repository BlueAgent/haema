package com.williambl.haema.component

import com.williambl.haema.ability.VampireAbility
import com.williambl.haema.id
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import net.minecraft.util.Identifier

interface VampireComponent : ComponentV3 {

    var isVampire: Boolean
    var isPermanentVampire: Boolean
    var isKilled: Boolean

    var abilities: MutableMap<VampireAbility, Int>
    var ritualsUsed: MutableSet<Identifier>

    companion object {
        val entityKey: ComponentKey<VampireComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(id("vampire"), VampireComponent::class.java)
    }
}