package galena.oreganized.world.event;

import galena.oreganized.Oreganized;
import galena.oreganized.OreganizedConfig;
import galena.oreganized.content.block.MoltenLeadCauldronBlock;
import galena.oreganized.content.entity.GargoyleBlockEntity;
import galena.oreganized.content.item.ScribeItem;
import galena.oreganized.index.OBlocks;
import galena.oreganized.index.OEffects;
import galena.oreganized.index.OFluids;
import galena.oreganized.index.OItems;
import galena.oreganized.index.OTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Oreganized.MOD_ID)
public class PlayerEvents {

    @SubscribeEvent
    public static void blockToolInteractions(final BlockEvent.BlockToolModificationEvent event) {
        ToolAction action = event.getToolAction();
        BlockState state = event.getState();
        if (event.isSimulated()) return;

        // Removing Wax ('Unwaxing' - Using an Axe on a waxed block).
        if (action.equals(ToolActions.AXE_WAX_OFF)) {
            Block unWaxedBlock = OBlocks.WAXED_BLOCKS.get(state.getBlock());
            if (unWaxedBlock == null) return;
            event.setFinalState(unWaxedBlock.defaultBlockState());
        }
    }

    /**
     * Use if interaction is not defined in {@link ToolActions}
     **/
    @SubscribeEvent
    public static void blockItemInteractions(final PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        ItemStack itemStack = event.getItemStack();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        // Waxing (Using Honeycomb on a waxable block).
        if (itemStack.is(Items.HONEYCOMB) && OBlocks.WAXED_BLOCKS.inverse().get(state.getBlock()) != null) {

            if (player instanceof ServerPlayer)
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, itemStack);

            player.swing(event.getHand());
            if (!player.isCreative()) event.getItemStack().shrink(1);
            Block waxedBlock = OBlocks.WAXED_BLOCKS.inverse().get(state.getBlock());
            if (!world.isClientSide() && waxedBlock != null) world.setBlock(pos, waxedBlock.defaultBlockState(), 11);
            world.levelEvent(player, 3003, pos, 0);
        }

        if (itemStack.is(Items.MUSIC_DISC_11) && state.is(OBlocks.MOLTEN_LEAD_CAULDRON.get())) {
            if (!state.getValue(MoltenLeadCauldronBlock.AGE).equals(3)) return;
            ItemStack newDisc = new ItemStack(OItems.MUSIC_DISC_STRUCTURE.get());

            player.swing(hand);
            if (!player.isCreative()) itemStack.shrink(1);
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!world.isClientSide()) player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));

            if (itemStack.isEmpty()) {
                player.setItemInHand(hand, newDisc);
                return;
            }
            if (!player.getInventory().add(newDisc)) {
                player.drop(newDisc, false);
                //return;
            }
        }
    }

    @SubscribeEvent
    public static void finishUsingItem(final LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack itemStack = event.getItem();
        if (itemStack.isEdible()) {
            boolean leadPoisoning = entity.isInFluidType(OFluids.MOLTEN_LEAD_TYPE.get());
            if (entity instanceof Player player) {
                for (int i = 0; i < 9; i++) {
                    if (player.getInventory().items.get(i).is(OTags.Items.LEAD_SOURCE))
                        leadPoisoning = true;
                }
            }
            if ((entity.getOffhandItem().is(OTags.Items.LEAD_SOURCE) || leadPoisoning) && OreganizedConfig.COMMON.leadPoisining.get()) {
                if (OreganizedConfig.stunningFromConfig())
                    entity.addEffect(new MobEffectInstance(OEffects.STUNNING.get(), 40 * 20));
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
            }
        }
    }

    @SubscribeEvent
    public static void tickPlayer(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        var data = event.player.getPersistentData();
        if (data.contains(GargoyleBlockEntity.GROWL_COOLDOWN_TAG, 99)) {
            var cooldown = data.getInt(GargoyleBlockEntity.GROWL_COOLDOWN_TAG);
            if (cooldown > 0) {
                data.putInt(GargoyleBlockEntity.GROWL_COOLDOWN_TAG, cooldown - 1);
            } else {
                data.remove(GargoyleBlockEntity.GROWL_COOLDOWN_TAG);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(final BlockEvent.BreakEvent event) {
        var stack = event.getPlayer().getMainHandItem();

        if (stack.getItem() instanceof ScribeItem scribe && scribe.dropsLikeSilktouch(stack, event.getState())) {
            event.setExpToDrop(0);
        }
    }
}
