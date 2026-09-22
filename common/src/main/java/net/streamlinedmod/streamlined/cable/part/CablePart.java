package net.streamlinedmod.streamlined.cable.part;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.streamlinedmod.streamlined.block.Cable;
import org.jspecify.annotations.NonNull;

public abstract class CablePart {

    private final PartType type;

    protected final Cable host;
    protected final Direction side;

    protected CablePart(@NonNull PartType type, @NonNull Cable host, @NonNull Direction side) {
        this.type = type;
        this.host = host;
        this.side = side;
    }

    public @NonNull PartType type() {
        return type;
    }

    public @NonNull Cable host() {
        return host;
    }

    public @NonNull Direction side() {
        return side;
    }

    public boolean blocksConnection() {
        return true;
    }

    public void onPlaced(@NonNull ItemStack stack) {
    }

    public @NonNull InteractionResult onUse(@NonNull Player player) {
        return InteractionResult.PASS;
    }

    public long powerUsage() {
        return 0;
    }

    public @NonNull ItemStack toItem() {
        return new ItemStack(type.item());
    }

    public void save(@NonNull ValueOutput output) {
    }

    public void load(@NonNull ValueInput input) {
    }
}
