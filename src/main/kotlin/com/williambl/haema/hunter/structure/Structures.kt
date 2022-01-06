package com.williambl.haema.hunter.structure

import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.biome.v1.ModificationPhase
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.FeatureConfig

val vampireHunterOutpostFeature = VampireHunterOutpostFeature(DefaultFeatureConfig.CODEC)
val configuredVampireHunterOutpostFeature = vampireHunterOutpostFeature.configure(FeatureConfig.DEFAULT)

val smallVampireHunterOutpostFeature = SmallVampireHunterOutpostFeature(DefaultFeatureConfig.CODEC)
val configuredMountainVampireHunterOutpostFeature = smallVampireHunterOutpostFeature.configure(FeatureConfig.DEFAULT)

fun registerStructures() {
    FabricStructureBuilder.create(Identifier("haema:vampire_hunter_outpost"), vampireHunterOutpostFeature)
        .step(GenerationStep.Feature.SURFACE_STRUCTURES)
        .defaultConfig(120, 70, 74426467)
        .superflatFeature(configuredVampireHunterOutpostFeature)
        .adjustsSurface()
        .register()

    Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, Identifier("haema:configured_vampire_hunter_outpost"), configuredVampireHunterOutpostFeature)

    BiomeModifications.create(Identifier("haema:vampire_hunter_outpost_addition"))
        .add(
            ModificationPhase.ADDITIONS,
            BiomeSelectors.foundInOverworld()
        ) { context -> context.generationSettings.addBuiltInStructure(configuredVampireHunterOutpostFeature) }

    FabricStructureBuilder.create(Identifier("haema:small_vampire_hunter_outpost"), smallVampireHunterOutpostFeature)
        .step(GenerationStep.Feature.SURFACE_STRUCTURES)
        .defaultConfig(100, 60, 74426500)
        .superflatFeature(configuredMountainVampireHunterOutpostFeature)
        .adjustsSurface()
        .register()

    Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, Identifier("haema:configured_small_vampire_hunter_outpost"), configuredMountainVampireHunterOutpostFeature)

    BiomeModifications.create(Identifier("haema:small_vampire_hunter_outpost_addition"))
        .add(
            ModificationPhase.ADDITIONS,
            BiomeSelectors.foundInOverworld()
        ) { context -> context.generationSettings.addBuiltInStructure(configuredMountainVampireHunterOutpostFeature) }
}