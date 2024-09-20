package galena.oreganized.index;

import galena.oreganized.Oreganized;
import galena.oreganized.content.critera.DummyCriterionTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Oreganized.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OCriteriaTriggers {

    public static final DummyCriterionTrigger SEE_GARGOYLE_GARGLE = CriteriaTriggers.register(new DummyCriterionTrigger(Oreganized.modLoc("see_gargoyle_gargle")));
    public static final DummyCriterionTrigger KNOCKED_BANNER_OFF = CriteriaTriggers.register(new DummyCriterionTrigger(Oreganized.modLoc("knocked_banner_off")));
    public static final DummyCriterionTrigger PROFOUND_BRAIN_DAMAGE = CriteriaTriggers.register(new DummyCriterionTrigger(Oreganized.modLoc("profound_brain_damage")));

}
