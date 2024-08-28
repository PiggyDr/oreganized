package galena.oreganized.world;

import galena.oreganized.Oreganized;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Oreganized.MOD_ID)
public class ScaredOfGargoyleGoal extends Goal {

    public static final String AVOID_TAG_KEY = "ScaredByGargoyle";
    public static final int MAX_DISTANCE_SQR = 16 * 16;
    public static final int SPRINT_DISTANCE_SQR = 7 * 7;

    private final PathfinderMob mob;

    @SubscribeEvent
    public static void onMobSpawn(MobSpawnEvent event) {
        var entity = event.getEntity();
        if (entity.getMobType() == MobType.UNDEAD && entity instanceof PathfinderMob mob) {
            entity.goalSelector.addGoal(1, new ScaredOfGargoyleGoal(mob));
        }
    }

    @Nullable
    private Path path;
    private final PathNavigation navigation;

    @Nullable
    private Vec3 avoiding;

    public ScaredOfGargoyleGoal(PathfinderMob mob) {
        this.mob = mob;
        this.navigation = mob.getNavigation();
    }

    @Override
    public boolean canUse() {
        var data = mob.getPersistentData();
        if (!data.contains(AVOID_TAG_KEY)) return false;

        var toAvoid = NbtUtils.readBlockPos(data.getCompound(AVOID_TAG_KEY));
        avoiding = Vec3.atCenterOf(toAvoid);

        if (mob.distanceToSqr(avoiding) > MAX_DISTANCE_SQR) return false;

        Vec3 awayPos = DefaultRandomPos.getPosAway(mob, 16, 7, avoiding);
        if (awayPos == null) {
            return false;
        }

        if (avoiding.distanceToSqr(awayPos) < mob.distanceToSqr(avoiding)) {
            return false;
        }

        path = navigation.createPath(awayPos.x, awayPos.y, awayPos.z, 0);
        return path != null;
    }

    public boolean canContinueToUse() {
        return !navigation.isDone();
    }

    public void start() {
        navigation.moveTo(this.path, 1.0);
    }

    public void stop() {
        avoiding = null;
        mob.getPersistentData().remove(AVOID_TAG_KEY);
    }

    public void tick() {
        if (avoiding == null) return;

        if (this.mob.distanceToSqr(avoiding) < SPRINT_DISTANCE_SQR) {
            this.mob.getNavigation().setSpeedModifier(1.3);
        } else {
            this.mob.getNavigation().setSpeedModifier(1.0);
        }
    }

}
