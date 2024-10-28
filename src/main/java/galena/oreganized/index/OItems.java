package galena.oreganized.index;

import com.teamabnormals.blueprint.common.item.BlueprintRecordItem;
import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import galena.oreganized.Oreganized;
import galena.oreganized.compat.farmers_delight.FarmersDelightCompat;
import galena.oreganized.content.item.BushHammerItem;
import galena.oreganized.content.item.ElectrumArmorItem;
import galena.oreganized.content.item.FlintAndPewterItem;
import galena.oreganized.content.item.LeadBoltItem;
import galena.oreganized.content.item.MinecartShrapnelBombItem;
import galena.oreganized.content.item.OSmithingTemplateItem;
import galena.oreganized.content.item.ScribeItem;
import galena.oreganized.content.item.SilverMirrorItem;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

import static galena.oreganized.ModCompat.FARMERS_DELIGHT_ID;

@SuppressWarnings("Convert2MethodRef")
@Mod.EventBusSubscriber(modid = Oreganized.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OItems {
    public static final ItemSubRegistryHelper HELPER = Oreganized.REGISTRY_HELPER.getItemSubHelper();

    public static Supplier<? extends Item> compat(String modid, Function<Item.Properties, ? extends Item> supplier, Item.Properties properties) {
        if (ModList.get().isLoaded(modid)) return () -> supplier.apply(properties);
        return () -> new Item(properties);
    }

    // Discs
    public static final RegistryObject<RecordItem> MUSIC_DISC_STRUCTURE = HELPER.createItem("music_disc_structure", () -> new BlueprintRecordItem(13, OSoundEvents.MUSIC_DISC_STRUCTURE, (new Item.Properties().stacksTo(1).rarity(Rarity.RARE)), 2980 / 20));
    public static final RegistryObject<RecordItem> MUSIC_DISC_AFTERLIFE = HELPER.createItem("music_disc_afterlife", () -> new BlueprintRecordItem(13, OSoundEvents.MUSIC_DISC_AFTERLIFE, (new Item.Properties().stacksTo(1).rarity(Rarity.RARE)), 155));

    // Crafting Materials
    public static final RegistryObject<Item> RAW_SILVER = HELPER.createItem("raw_silver", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILVER_INGOT = HELPER.createItem("silver_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILVER_NUGGET = HELPER.createItem("silver_nugget", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RAW_LEAD = HELPER.createItem("raw_lead", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LEAD_INGOT = HELPER.createItem("lead_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LEAD_NUGGET = HELPER.createItem("lead_nugget", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ELECTRUM_INGOT = HELPER.createItem("electrum_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_NUGGET = HELPER.createItem("netherite_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ELECTRUM_NUGGET = HELPER.createItem("electrum_nugget", () -> new Item(new Item.Properties()));

    // Tools
    public static final RegistryObject<Item> BUSH_HAMMER = HELPER.createItem("bush_hammer", () -> new BushHammerItem(OItemTiers.LEAD, 2.5F, -2.8F, (new Item.Properties()).stacksTo(1)));
    public static final RegistryObject<Item> SCRIBE = HELPER.createItem("scribe", () -> new ScribeItem(new Item.Properties().durability(250)));

    public static final RegistryObject<Item> ELECTRUM_SWORD = HELPER.createItem("electrum_sword", () -> new SwordItem(OItemTiers.ELECTRUM, 3, -2.4F, (new Item.Properties())));
    public static final RegistryObject<Item> ELECTRUM_SHOVEL = HELPER.createItem("electrum_shovel", () -> new ShovelItem(OItemTiers.ELECTRUM, 1.5F, -3.0F, (new Item.Properties())));
    public static final RegistryObject<Item> ELECTRUM_PICKAXE = HELPER.createItem("electrum_pickaxe", () -> new PickaxeItem(OItemTiers.ELECTRUM, 1, -2.8F, (new Item.Properties())));
    public static final RegistryObject<Item> ELECTRUM_AXE = HELPER.createItem("electrum_axe", () -> new AxeItem(OItemTiers.ELECTRUM, 5.0F, -3.0F, (new Item.Properties())));
    public static final RegistryObject<Item> ELECTRUM_HOE = HELPER.createItem("electrum_hoe", () -> new HoeItem(OItemTiers.ELECTRUM, -3, 0.0F, (new Item.Properties())));

    public static final RegistryObject<Item> ELECTRUM_KNIFE = HELPER.createItem("electrum_knife", compat(FARMERS_DELIGHT_ID, it -> FarmersDelightCompat.KNIFE_FACTORY.apply(it), new Item.Properties()));
    public static final RegistryObject<Item> ELECTRUM_SHIELD = HELPER.createItem("electrum_shield", () -> new ShieldItem(new Item.Properties().durability(363)));
    public static final RegistryObject<Item> ELECTRUM_MACHETE = HELPER.createItem("electrum_machete", () -> new SwordItem(OItemTiers.ELECTRUM, 2, -2.4F, new Item.Properties()));

    // Misc Tools
    public static final RegistryObject<Item> SILVER_MIRROR = HELPER.createItem("silver_mirror", () -> new SilverMirrorItem(new Item.Properties().stacksTo(1)));
    //public static final RegistryObject<Item> BROKEN_SILVER_MIRROR = HELPER.createItem("broken_silver_mirror", () -> new OItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MOLTEN_LEAD_BUCKET = HELPER.createItem("molten_lead_bucket", () -> new BucketItem(OFluids.MOLTEN_LEAD, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

    // Armor
    public static final RegistryObject<Item> ELECTRUM_UPGRADE_SMITHING_TEMPLATE = HELPER.createItem("electrum_upgrade_smithing_template",
            OSmithingTemplateItem::createElectrumUpgradeTemplate);
    public static final RegistryObject<ArmorItem> ELECTRUM_HELMET = HELPER.createItem("electrum_helmet",
            () -> new ElectrumArmorItem(ArmorItem.Type.HELMET));
    public static final RegistryObject<ArmorItem> ELECTRUM_CHESTPLATE = HELPER.createItem("electrum_chestplate",
            () -> new ElectrumArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<ArmorItem> ELECTRUM_LEGGINGS = HELPER.createItem("electrum_leggings",
            () -> new ElectrumArmorItem(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<ArmorItem> ELECTRUM_BOOTS = HELPER.createItem("electrum_boots",
            () -> new ElectrumArmorItem(ArmorItem.Type.BOOTS));

    public static final RegistryObject<Item> LEAD_BOLT = HELPER.createItem("lead_bolt", () -> new LeadBoltItem(new Item.Properties()));

    public static final RegistryObject<Item> FLINT_AND_PEWTER = HELPER.createItem("flint_and_pewter", () -> new FlintAndPewterItem(new Item.Properties().durability(64)));

    // Transportation
    public static final RegistryObject<Item> SHRAPNEL_BOMB_MINECART = HELPER.createItem("shrapnel_bomb_minecart", () -> new MinecartShrapnelBombItem(AbstractMinecart.Type.TNT, OEntityTypes.SHRAPNEL_BOMB_MINECART));

    // Misc
    public static final RegistryObject<SpawnEggItem> HOLLER_SPAWN_EGG = HELPER.createItem("holler_spawn_egg", () -> new ForgeSpawnEggItem(OEntityTypes.HOLLER, 0x84EED2, 0x24352F, new Item.Properties()));
}