package net.streamlinedmod.streamlined.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.streamlinedmod.streamlined.Streamlined;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class ServerItem extends GeoModelItem {

    public static final int DRIVE_SLOTS = 4;

    public ServerItem(@NonNull Properties properties) {
        super(properties);
    }

    public static @NonNull NonNullList<ItemStack> drives(@NonNull ItemStack server) {
        var drives = NonNullList.withSize(DRIVE_SLOTS, ItemStack.EMPTY);

        server.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(drives);

        return drives;
    }

    public static void setDrives(@NonNull ItemStack server, @NonNull List<ItemStack> drives) {
        server.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(drives));
    }

    public static int driveCount(@NonNull ItemStack server) {
        int count = 0;

        for (var drive : drives(server)) {
            if (drive.getItem() instanceof DriveItem) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip." + Streamlined.MOD_ID + ".server.drives", driveCount(stack), DRIVE_SLOTS).withStyle(ChatFormatting.GRAY));
    }
}
