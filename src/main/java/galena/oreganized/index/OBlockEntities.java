package galena.oreganized.index;

import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import galena.oreganized.Oreganized;
import galena.oreganized.content.block.HeavyDoorBlockEntity;
import galena.oreganized.content.entity.GargoyleBlockEntity;
import galena.oreganized.content.entity.SepulcherBlockEntity;
import galena.oreganized.content.entity.VigilCandleBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Oreganized.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OBlockEntities {
    public static final BlockEntitySubRegistryHelper HELPER = Oreganized.REGISTRY_HELPER.getBlockEntitySubHelper();

    public static final RegistryObject<BlockEntityType<GargoyleBlockEntity>> GARGOYLE = HELPER.createBlockEntity("gargoyle", GargoyleBlockEntity::new, () -> Set.of(OBlocks.GARGOYLE.get()));
    public static final RegistryObject<BlockEntityType<HeavyDoorBlockEntity>> HEAVY_DOOR = HELPER.createBlockEntity("heavy_door", HeavyDoorBlockEntity::new, () -> Set.of(OBlocks.LEAD_DOOR.get(), OBlocks.LEAD_TRAPDOOR.get()));

    public static final RegistryObject<BlockEntityType<VigilCandleBlockEntity>> VIGIL_CANDLE = HELPER.createBlockEntity("vigil_candle", VigilCandleBlockEntity::new, () -> OBlocks.vigilCandles().map(RegistryObject::get).collect(Collectors.toSet()));
    public static final RegistryObject<BlockEntityType<SepulcherBlockEntity>> SEPULCHER = HELPER.createBlockEntity("sepulcher", SepulcherBlockEntity::new, () -> Set.of(OBlocks.SEPULCHER.get()));

}
