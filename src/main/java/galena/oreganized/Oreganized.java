package galena.oreganized;

import com.google.common.collect.ImmutableBiMap;
import galena.oreganized.client.OreganizedClient;
import galena.oreganized.content.block.MoltenLeadCauldronBlock;
import galena.oreganized.data.OAdvancements;
import galena.oreganized.data.OBiomeTags;
import galena.oreganized.data.OBlockStates;
import galena.oreganized.data.OBlockTags;
import galena.oreganized.data.ODamageTags;
import galena.oreganized.data.OEntityTags;
import galena.oreganized.data.OFluidTags;
import galena.oreganized.data.OItemModels;
import galena.oreganized.data.OItemTags;
import galena.oreganized.data.OLang;
import galena.oreganized.data.OLootTables;
import galena.oreganized.data.ORecipes;
import galena.oreganized.data.ORegistries;
import galena.oreganized.data.OSoundDefinitions;
import galena.oreganized.index.OBlockEntities;
import galena.oreganized.index.OBlocks;
import galena.oreganized.index.OEffects;
import galena.oreganized.index.OEntityTypes;
import galena.oreganized.index.OFeatures;
import galena.oreganized.index.OFluids;
import galena.oreganized.index.OItems;
import galena.oreganized.index.OPaintingVariants;
import galena.oreganized.index.OParticleTypes;
import galena.oreganized.index.OPotions;
import galena.oreganized.index.OSoundEvents;
import galena.oreganized.index.OStructures;
import net.minecraft.DetectedVersion;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(Oreganized.MOD_ID)
public class Oreganized {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "oreganized";

    public static ResourceLocation modLoc(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    public Oreganized() {

        final IEventBus bus = Bus.MOD.bus().get();
        final ModLoadingContext context = ModLoadingContext.get();
        bus.addListener(this::setup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::gatherData);
        bus.addListener(this::buildCreativeModeTabContents);
        //bus.addListener(this::addPackFinders);

        DeferredRegister<?>[] registers = {
                OBlockEntities.BLOCK_ENTITIES,
                OBlocks.BLOCKS,
                OEffects.EFFECTS,
                OEntityTypes.ENTITIES,
                OFluids.FLUIDS,
                OFluids.TYPES,
                OItems.ITEMS,
                OParticleTypes.PARTICLES,
                OPotions.POTIONS,
                OSoundEvents.SOUNDS,
                OStructures.STRUCTURES,
                OFeatures.FEATURES,
                OPaintingVariants.PAINTING_VARIANTS,
        };

        for (DeferredRegister<?> register : registers) {
            register.register(bus);
        }

        //CompatHandler.register();

        context.registerConfig(ModConfig.Type.COMMON, OreganizedConfig.COMMON_SPEC);
        //context.registerConfig(ModConfig.Type.CLIENT, OreganizedConfig.CLIENT_SPEC);
    }

    private void setup(FMLCommonSetupEvent event) {
        FluidInteractionRegistry.addInteraction(OFluids.MOLTEN_LEAD_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.WATER_TYPE.get(),
                fluidState -> OBlocks.LEAD_BLOCK.get().defaultBlockState()
        ));

        event.enqueueWork(() -> {

            Map<Item, CauldronInteraction> EMPTY = CauldronInteraction.EMPTY;
            Map<Item, CauldronInteraction> WATER = CauldronInteraction.WATER;
            Map<Item, CauldronInteraction> LAVA = CauldronInteraction.LAVA;
            Map<Item, CauldronInteraction> POWDER_SNOW = CauldronInteraction.POWDER_SNOW;
            Map<Item, CauldronInteraction> LEAD = MoltenLeadCauldronBlock.INTERACTION_MAP;

            EMPTY.put(OItems.MOLTEN_LEAD_BUCKET.get(), MoltenLeadCauldronBlock.FILL_MOLTEN_LEAD);
            WATER.put(OItems.MOLTEN_LEAD_BUCKET.get(), MoltenLeadCauldronBlock.FILL_MOLTEN_LEAD);
            LAVA.put(OItems.MOLTEN_LEAD_BUCKET.get(), MoltenLeadCauldronBlock.FILL_MOLTEN_LEAD);
            POWDER_SNOW.put(OItems.MOLTEN_LEAD_BUCKET.get(), MoltenLeadCauldronBlock.FILL_MOLTEN_LEAD);
            LEAD.put(OItems.MOLTEN_LEAD_BUCKET.get(), MoltenLeadCauldronBlock.FILL_MOLTEN_LEAD);

            EMPTY.put(OBlocks.LEAD_BLOCK.get().asItem(), MoltenLeadCauldronBlock.FILL_LEAD_BLOCK);
            WATER.put(OBlocks.LEAD_BLOCK.get().asItem(), MoltenLeadCauldronBlock.FILL_LEAD_BLOCK);
            LAVA.put(OBlocks.LEAD_BLOCK.get().asItem(), MoltenLeadCauldronBlock.FILL_LEAD_BLOCK);
            POWDER_SNOW.put(OBlocks.LEAD_BLOCK.get().asItem(), MoltenLeadCauldronBlock.FILL_LEAD_BLOCK);

            LEAD.put(Items.AIR, MoltenLeadCauldronBlock.EMPTY_LEAD_BLOCK);
            LEAD.put(Items.BUCKET, MoltenLeadCauldronBlock.EMPTY_MOLTEN_LEAD);

            CauldronInteraction.addDefaultInteractions(MoltenLeadCauldronBlock.INTERACTION_MAP);

            if (OreganizedConfig.stunningFromConfig()) {
                PotionBrewing.addMix(Potions.WATER, OItems.LEAD_INGOT.get(), OPotions.STUNNING.get());
                PotionBrewing.addMix(OPotions.STUNNING.get(), Items.REDSTONE, OPotions.LONG_STUNNING.get());
                PotionBrewing.addMix(OPotions.STUNNING.get(), Items.GLOWSTONE_DUST, OPotions.STRONG_STUNNING.get());
            }

            FireBlock fire = (FireBlock) Blocks.FIRE;
            fire.setFlammable(OBlocks.SHRAPNEL_BOMB.get(), 15, 100);
        });

        OBlocks.WAXED_BLOCKS = new ImmutableBiMap.Builder<Block, Block>()
                .put(OBlocks.WAXED_SPOTTED_GLANCE.get(), OBlocks.SPOTTED_GLANCE.get())
                .put(OBlocks.WAXED_WHITE_CONCRETE_POWDER.get(), Blocks.WHITE_CONCRETE_POWDER)
                .put(OBlocks.WAXED_ORANGE_CONCRETE_POWDER.get(), Blocks.ORANGE_CONCRETE_POWDER)
                .put(OBlocks.WAXED_MAGENTA_CONCRETE_POWDER.get(), Blocks.MAGENTA_CONCRETE_POWDER)
                .put(OBlocks.WAXED_LIGHT_BLUE_CONCRETE_POWDER.get(), Blocks.LIGHT_BLUE_CONCRETE_POWDER)
                .put(OBlocks.WAXED_YELLOW_CONCRETE_POWDER.get(), Blocks.YELLOW_CONCRETE_POWDER)
                .put(OBlocks.WAXED_LIME_CONCRETE_POWDER.get(), Blocks.LIME_CONCRETE_POWDER)
                .put(OBlocks.WAXED_PINK_CONCRETE_POWDER.get(), Blocks.PINK_CONCRETE_POWDER)
                .put(OBlocks.WAXED_GRAY_CONCRETE_POWDER.get(), Blocks.GRAY_CONCRETE_POWDER)
                .put(OBlocks.WAXED_LIGHT_GRAY_CONCRETE_POWDER.get(), Blocks.LIGHT_GRAY_CONCRETE_POWDER)
                .put(OBlocks.WAXED_CYAN_CONCRETE_POWDER.get(), Blocks.CYAN_CONCRETE_POWDER)
                .put(OBlocks.WAXED_PURPLE_CONCRETE_POWDER.get(), Blocks.PURPLE_CONCRETE_POWDER)
                .put(OBlocks.WAXED_BLUE_CONCRETE_POWDER.get(), Blocks.BLUE_CONCRETE_POWDER)
                .put(OBlocks.WAXED_BROWN_CONCRETE_POWDER.get(), Blocks.BROWN_CONCRETE_POWDER)
                .put(OBlocks.WAXED_GREEN_CONCRETE_POWDER.get(), Blocks.GREEN_CONCRETE_POWDER)
                .put(OBlocks.WAXED_RED_CONCRETE_POWDER.get(), Blocks.RED_CONCRETE_POWDER)
                .put(OBlocks.WAXED_BLACK_CONCRETE_POWDER.get(), Blocks.BLACK_CONCRETE_POWDER)
                .build();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        //CompatHandlerClient.setup(event);
        OreganizedClient.registerBlockRenderers();

        ItemProperties.register(OItems.SILVER_MIRROR.get(), new ResourceLocation("level"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 8;
            } else {
                return stack.getOrCreateTag().getInt("Level");
            }
        });
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> future = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();
        boolean client = event.includeClient();
        boolean server = event.includeServer();

        generator.addProvider(client, new OBlockStates(output, helper));
        generator.addProvider(client, new OItemModels(output, helper));
        generator.addProvider(client, new OLang(output));
        generator.addProvider(client, new OSoundDefinitions(output, helper));

        generator.addProvider(server, new ORecipes(output));
        generator.addProvider(server, new OLootTables(output));
        OBlockTags blockTags = new OBlockTags(output, future, helper);
        generator.addProvider(server, blockTags);
        generator.addProvider(server, new OItemTags(output, future, blockTags.contentsGetter(), helper));
        generator.addProvider(server, new OEntityTags(output, future, helper));
        generator.addProvider(server, new OAdvancements(output, future, helper));
        generator.addProvider(server, new OFluidTags(output, future, helper));
        DatapackBuiltinEntriesProvider datapackProvider = new ORegistries(output, future);
        CompletableFuture<HolderLookup.Provider> lookupProvider = datapackProvider.getRegistryProvider();
        generator.addProvider(server, datapackProvider);
        generator.addProvider(server, new OBiomeTags(output, lookupProvider, helper));
        generator.addProvider(server, new ODamageTags(output, lookupProvider, helper));
        //generator.addProvider(server, new OPaintingVariantTags(output, lookupProvider, helper));
        //generator.addProvider(server, new OBiomeModifier.register(event));

        generator.addProvider(server, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Oreganized resources"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion))
        )));
    }

    @SubscribeEvent
    public void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tab = event.getTabKey();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();
        if (tab == CreativeModeTabs.BUILDING_BLOCKS) {
            putBefore(entries, Items.DEEPSLATE, OBlocks.GLANCE);
            putAfter(entries, OBlocks.GLANCE.get(), OBlocks.SPOTTED_GLANCE);
            putAfter(entries, OBlocks.SPOTTED_GLANCE.get(), OBlocks.GLANCE_STAIRS);
            putAfter(entries, OBlocks.GLANCE_STAIRS.get(), OBlocks.GLANCE_SLAB);
            putAfter(entries, OBlocks.GLANCE_SLAB.get(), OBlocks.GLANCE_WALL);
            putAfter(entries, OBlocks.GLANCE_WALL.get(), OBlocks.CHISELED_GLANCE);
            putAfter(entries, OBlocks.CHISELED_GLANCE.get(), OBlocks.POLISHED_GLANCE);
            putAfter(entries, OBlocks.POLISHED_GLANCE.get(), OBlocks.POLISHED_GLANCE_STAIRS);
            putAfter(entries, OBlocks.POLISHED_GLANCE_STAIRS.get(), OBlocks.POLISHED_GLANCE_SLAB);
            putAfter(entries, OBlocks.POLISHED_GLANCE_SLAB.get(), OBlocks.GLANCE_BRICKS);
            putAfter(entries, OBlocks.GLANCE_BRICKS.get(), OBlocks.GLANCE_BRICK_STAIRS);
            putAfter(entries, OBlocks.GLANCE_BRICK_STAIRS.get(), OBlocks.GLANCE_BRICK_SLAB);
            putAfter(entries, OBlocks.GLANCE_BRICK_SLAB.get(), OBlocks.GLANCE_BRICK_WALL);
            putAfter(entries, OBlocks.GLANCE_BRICK_WALL.get(), OBlocks.WAXED_SPOTTED_GLANCE);
            putBefore(entries, Items.REDSTONE_BLOCK, OBlocks.SILVER_BLOCK);
            putBefore(entries, Items.NETHERITE_BLOCK, OBlocks.ELECTRUM_BLOCK);
            putAfter(entries, Items.WAXED_OXIDIZED_CUT_COPPER_SLAB, OBlocks.LEAD_BLOCK);
            putAfter(entries, OBlocks.LEAD_BLOCK.get(), OBlocks.CUT_LEAD);
            putAfter(entries, OBlocks.CUT_LEAD.get(), OBlocks.LEAD_BRICKS);
            putAfter(entries, OBlocks.LEAD_BRICKS.get(), OBlocks.LEAD_PILLAR);
        }
        if (tab == CreativeModeTabs.COLORED_BLOCKS) {
            putBefore(entries, Items.SHULKER_BOX, OBlocks.WHITE_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.WHITE_CRYSTAL_GLASS.get(), OBlocks.LIGHT_GRAY_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.LIGHT_GRAY_CRYSTAL_GLASS.get(), OBlocks.GRAY_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.GRAY_CRYSTAL_GLASS.get(), OBlocks.BLACK_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.BLACK_CRYSTAL_GLASS.get(), OBlocks.BROWN_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.BROWN_CRYSTAL_GLASS.get(), OBlocks.RED_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.RED_CRYSTAL_GLASS.get(), OBlocks.ORANGE_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.ORANGE_CRYSTAL_GLASS.get(), OBlocks.YELLOW_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.YELLOW_CRYSTAL_GLASS.get(), OBlocks.LIME_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.LIME_CRYSTAL_GLASS.get(), OBlocks.GREEN_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.GREEN_CRYSTAL_GLASS.get(), OBlocks.CYAN_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.CYAN_CRYSTAL_GLASS.get(), OBlocks.LIGHT_BLUE_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.LIGHT_BLUE_CRYSTAL_GLASS.get(), OBlocks.BLUE_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.BLUE_CRYSTAL_GLASS.get(), OBlocks.PURPLE_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.PURPLE_CRYSTAL_GLASS.get(), OBlocks.MAGENTA_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.MAGENTA_CRYSTAL_GLASS.get(), OBlocks.PINK_CRYSTAL_GLASS);
            putAfter(entries, OBlocks.PINK_CRYSTAL_GLASS.get(), OBlocks.WHITE_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.WHITE_CRYSTAL_GLASS_PANE.get(), OBlocks.LIGHT_GRAY_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.LIGHT_GRAY_CRYSTAL_GLASS_PANE.get(), OBlocks.GRAY_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.GRAY_CRYSTAL_GLASS_PANE.get(), OBlocks.BLACK_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.BLACK_CRYSTAL_GLASS_PANE.get(), OBlocks.BROWN_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.BROWN_CRYSTAL_GLASS_PANE.get(), OBlocks.RED_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.RED_CRYSTAL_GLASS_PANE.get(), OBlocks.ORANGE_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.ORANGE_CRYSTAL_GLASS_PANE.get(), OBlocks.YELLOW_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.YELLOW_CRYSTAL_GLASS_PANE.get(), OBlocks.LIME_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.LIME_CRYSTAL_GLASS_PANE.get(), OBlocks.GREEN_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.GREEN_CRYSTAL_GLASS_PANE.get(), OBlocks.CYAN_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.CYAN_CRYSTAL_GLASS_PANE.get(), OBlocks.LIGHT_BLUE_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.LIGHT_BLUE_CRYSTAL_GLASS_PANE.get(), OBlocks.BLUE_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.BLUE_CRYSTAL_GLASS_PANE.get(), OBlocks.PURPLE_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.PURPLE_CRYSTAL_GLASS_PANE.get(), OBlocks.MAGENTA_CRYSTAL_GLASS_PANE);
            putAfter(entries, OBlocks.MAGENTA_CRYSTAL_GLASS_PANE.get(), OBlocks.PINK_CRYSTAL_GLASS_PANE);
        }
        if (tab == CreativeModeTabs.NATURAL_BLOCKS) {
            putAfter(entries, Items.DEEPSLATE_COPPER_ORE, OBlocks.LEAD_ORE);
            putAfter(entries, OBlocks.LEAD_ORE.get(), OBlocks.DEEPSLATE_LEAD_ORE);
            putAfter(entries, Items.DEEPSLATE_GOLD_ORE, OBlocks.SILVER_ORE);
            putAfter(entries, OBlocks.SILVER_ORE.get(), OBlocks.DEEPSLATE_SILVER_ORE);
            putAfter(entries, Items.RAW_COPPER_BLOCK, OBlocks.RAW_LEAD_BLOCK);
            putAfter(entries, Items.RAW_GOLD_BLOCK, OBlocks.RAW_SILVER_BLOCK);
        }
        if (tab == CreativeModeTabs.REDSTONE_BLOCKS) {
            putBefore(entries, Items.NOTE_BLOCK, OBlocks.EXPOSER);
            putAfter(entries, Items.TNT_MINECART, OItems.SHRAPNEL_BOMB_MINECART);
            putAfter(entries, Items.TNT, OBlocks.SHRAPNEL_BOMB);
        }
        if (tab == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            putBefore(entries, Items.NETHERITE_SHOVEL, OItems.ELECTRUM_SHOVEL);
            putAfter(entries, OItems.ELECTRUM_SHOVEL.get(), OItems.ELECTRUM_PICKAXE);
            putAfter(entries, OItems.ELECTRUM_PICKAXE.get(), OItems.ELECTRUM_AXE);
            putAfter(entries, OItems.ELECTRUM_AXE.get(), OItems.ELECTRUM_HOE);
            putBefore(entries, Items.MILK_BUCKET, OItems.MOLTEN_LEAD_BUCKET);
            putBefore(entries, Items.SPYGLASS, OItems.SILVER_MIRROR);
            putAfter(entries, Items.TNT_MINECART, OItems.SHRAPNEL_BOMB_MINECART);
            putBefore(entries, Items.MUSIC_DISC_5, OItems.MUSIC_DISC_STRUCTURE);
        }
        if (tab == CreativeModeTabs.COMBAT) {
            putBefore(entries, Items.NETHERITE_SWORD, OItems.ELECTRUM_SWORD);
            putBefore(entries, Items.NETHERITE_HELMET, OItems.ELECTRUM_HELMET);
            putAfter(entries, OItems.ELECTRUM_HELMET.get(), OItems.ELECTRUM_CHESTPLATE);
            putAfter(entries, OItems.ELECTRUM_CHESTPLATE.get(), OItems.ELECTRUM_LEGGINGS);
            putAfter(entries, OItems.ELECTRUM_LEGGINGS.get(), OItems.ELECTRUM_BOOTS);
            putAfter(entries, Items.TNT, OBlocks.SHRAPNEL_BOMB);
        }
        if (tab == CreativeModeTabs.INGREDIENTS) {
            putAfter(entries, Items.RAW_COPPER, OItems.RAW_LEAD);
            putAfter(entries, Items.RAW_GOLD, OItems.RAW_SILVER);
            putAfter(entries, Items.IRON_NUGGET, OItems.LEAD_NUGGET);
            putAfter(entries, Items.GOLD_NUGGET, OItems.SILVER_NUGGET);
            putBefore(entries, Items.IRON_INGOT, OItems.ELECTRUM_NUGGET);
            putAfter(entries, Items.COPPER_INGOT, OItems.LEAD_INGOT);
            putAfter(entries, Items.GOLD_INGOT, OItems.SILVER_INGOT);
            putBefore(entries, Items.NETHERITE_SCRAP, OItems.ELECTRUM_INGOT);
            putBefore(entries, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, OItems.ELECTRUM_UPGRADE_SMITHING_TEMPLATE);
        }
    }

    private static void putAfter(MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, ItemLike after, Supplier<? extends ItemLike> supplier) {
        ItemLike key = supplier.get();
        entries.putAfter(new ItemStack(after), new ItemStack(key), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private static void putBefore(MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, ItemLike after, Supplier<? extends ItemLike> supplier) {
        ItemLike key = supplier.get();
        entries.putBefore(new ItemStack(after), new ItemStack(key), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
