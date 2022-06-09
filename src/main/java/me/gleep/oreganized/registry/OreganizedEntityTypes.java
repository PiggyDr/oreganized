package me.gleep.oreganized.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import me.gleep.oreganized.Oreganized;
import me.gleep.oreganized.entities.projectiles.LeadBolt;

@Mod.EventBusSubscriber(modid = Oreganized.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OreganizedEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Oreganized.MOD_ID);

    // misc
    public static final RegistryObject<EntityType<LeadBolt>> LEAD_BOLT = ENTITIES.register("lead_bolt", () -> EntityType.Builder.<LeadBolt>of(LeadBolt::new, MobCategory.MISC).sized(0.25F, 0.25F).build("lead_bolt"));

    // normal
}
