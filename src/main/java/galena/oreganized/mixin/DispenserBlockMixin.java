package galena.oreganized.mixin;

import galena.oreganized.content.block.GargoyleBlock;
import galena.oreganized.index.OTags;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(
            method = "getDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectGargoyleBehaviour(ItemStack stack, CallbackInfoReturnable<DispenseItemBehavior> cir) {
        if (stack.is(OTags.Items.GARGOYLE_SNACK)) cir.setReturnValue(GargoyleBlock.DISPENSE_ITEM_BEHAVIOR);
    }

}
