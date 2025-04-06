package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.blocks.AcidBlock;
import com.fruityspikes.cosmic_voyage.server.blocks.AcidFluid;
import com.fruityspikes.cosmic_voyage.server.util.CVConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class CVFluidRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, CosmicVoyage.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, CosmicVoyage.MODID);
    public static final Map<String, DeferredHolder<FluidType, FluidType>> ACID_FLUID_TYPES = new HashMap<>();
    public static final Map<String, DeferredHolder<Fluid, Fluid>> ACID_FLUIDS_STILL = new HashMap<>();
    public static final Map<String, DeferredHolder<Fluid, FlowingFluid>> ACID_FLUIDS_FLOWING = new HashMap<>();

//    public static final Holder<FluidType> ACID_TYPE = FLUID_TYPES.register("acid", () -> new FluidType(FluidType.Properties.create()
//            .supportsBoating(true)
//            .canHydrate(true)
//            .addDripstoneDripping(0.25F, ParticleTypes.SCULK_SOUL, Blocks.POWDER_SNOW_CAULDRON, SoundEvents.END_PORTAL_SPAWN)));
    //public static final DeferredHolder<Fluid, FlowingFluid> ACID = FLUIDS.register("acid", () -> new BaseFlowingFluid.Source(AcidFluidProperties()));
    //public static final DeferredHolder<Fluid, Fluid> ACID_FLOWING = FLUIDS.register("acid_flowing", () -> new BaseFlowingFluid.Flowing(AcidFluidProperties()));
    //@SubscribeEvent
    public static void registerAcidTypes() {
        CVConstants.AcidColors.forEach((name, color) -> {
            ACID_FLUID_TYPES.put(name, FLUID_TYPES.register("acid_" + name, () -> new FluidType(FluidType.Properties.create()
                    .supportsBoating(true)
                    .canHydrate(true)
                    .addDripstoneDripping(0.25F, ParticleTypes.SCULK_SOUL, Blocks.POWDER_SNOW_CAULDRON, SoundEvents.END_PORTAL_SPAWN))));
            System.out.println("Registered " + name + " acid type!");
        });
    }
    //@SubscribeEvent
    public static void registerAcids() {
        CVConstants.AcidColors.forEach((name, color) -> {
            ACID_FLUIDS_STILL.put(name, FLUIDS.register("acid_" + name, () -> new AcidFluid.Source(AcidFluidProperties(name))));
            System.out.println("Registered " + name + " acid fluid!");
            ACID_FLUIDS_FLOWING.put(name, FLUIDS.register("acid_" + name + "_flowing", () -> new AcidFluid.Flowing(AcidFluidProperties(name))));
            System.out.println("Registered " + name + " acid flowing fluid!");
        });
    }

    public static BaseFlowingFluid.Properties AcidFluidProperties(String name) {
        return new AcidFluid.Properties(CVFluidRegistry.ACID_FLUID_TYPES.get(name)::value, CVFluidRegistry.ACID_FLUIDS_STILL.get(name), CVFluidRegistry.ACID_FLUIDS_FLOWING.get(name))
                .block(CVBlockRegistry.ACID_BLOCKS.get(name))
                .bucket(CVItemRegistry.ACID_BUCKETS.get(name));
    }
}
