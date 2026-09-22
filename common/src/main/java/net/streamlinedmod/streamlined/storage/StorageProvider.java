package net.streamlinedmod.streamlined.storage;

import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public interface StorageProvider {

    long insert(@NonNull ItemStack stack, long amount, boolean simulate);

    long extract(@NonNull ItemStack stack, long amount, boolean simulate);

    void collect(@NonNull Consumer<StoredItem> consumer);

    long powerUsage();
}
