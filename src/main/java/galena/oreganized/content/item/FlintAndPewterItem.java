package galena.oreganized.content.item;

import galena.oreganized.content.block.LeadOreBlock;
import galena.oreganized.index.OParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class FlintAndPewterItem extends Item {

    public FlintAndPewterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        var pos = context.getClickedPos().relative(context.getClickedFace());

        level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.4F);

        var cloud = LeadOreBlock.spawnCloud(level, pos, 1F);
        cloud.setWaitTime(0);

        var vec = context.getClickLocation();

        for (int i = 0; i < 6; i++) {
            level.addParticle(
                    OParticleTypes.LEAD_BLOW.get(),
                    vec.x, vec.y, vec.z,
                    level.random.nextDouble() * 0.2 - 0.1, level.random.nextDouble() * 0.2 - 0.1, level.random.nextDouble() * 0.2 - 0.1
            );
        }

        if (player != null) {
            context.getItemInHand().hurtAndBreak(1, player, it -> {
                it.broadcastBreakEvent(context.getHand());
            });
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
