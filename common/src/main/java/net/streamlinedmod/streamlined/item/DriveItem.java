package net.streamlinedmod.streamlined.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.storage.DriveContents;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class DriveItem extends GeoModelItem {

    private final long capacity;
    private final int maxTypes;

    public DriveItem(@NonNull Properties properties, long capacity, int maxTypes) {
        super(properties);
        this.capacity = capacity;
        this.maxTypes = maxTypes;
    }

    public static @NonNull DriveContents contents(@NonNull ItemStack drive) {
        return drive.getOrDefault(ModComponents.DRIVE_CONTENTS.get(), DriveContents.EMPTY);
    }

    public long capacity() {
        return capacity;
    }

    public int maxTypes() {
        return maxTypes;
    }

    public @NonNull DriveState state(@NonNull ItemStack drive) {
        var contents = contents(drive);

        if (contents.total() >= capacity) {
            return DriveState.FULL;
        }

        if (contents.types() >= maxTypes) {
            return DriveState.TYPES_FULL;
        }

        return contents.types() == 0 ? DriveState.EMPTY : DriveState.USED;
    }

    public boolean contains(@NonNull ItemStack drive, @NonNull ItemStack stack) {
        return contents(drive).indexOf(stack) >= 0;
    }

    public long insert(@NonNull ItemStack drive, @NonNull ItemStack stack, long amount, boolean simulate) {
        var contents = contents(drive);
        long space = capacity - contents.total();

        if (space <= 0 || amount <= 0) {
            return 0;
        }

        int index = contents.indexOf(stack);

        if (index < 0 && contents.types() >= maxTypes) {
            return 0;
        }

        long inserted = Math.min(space, amount);

        if (!simulate) {
            drive.set(ModComponents.DRIVE_CONTENTS.get(), contents.add(index, stack, inserted));
        }

        return inserted;
    }

    public long extract(@NonNull ItemStack drive, @NonNull ItemStack stack, long amount, boolean simulate) {
        var contents = contents(drive);
        int index = contents.indexOf(stack);
        long extracted = Math.min(contents.count(index), amount);

        if (extracted <= 0) {
            return 0;
        }

        if (!simulate) {
            drive.set(ModComponents.DRIVE_CONTENTS.get(), contents.remove(index, extracted));
        }

        return extracted;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        var contents = contents(stack);

        tooltip.accept(Component.translatable("tooltip." + Streamlined.MOD_ID + ".drive.items", contents.total(), capacity).withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip." + Streamlined.MOD_ID + ".drive.types", contents.types(), maxTypes).withStyle(ChatFormatting.GRAY));
    }
}
