package net.streamlinedmod.streamlined.storage;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public record DriveContents(@NonNull List<StoredItem> items) {

    public static final DriveContents EMPTY = new DriveContents(List.of());

    public static final Codec<DriveContents> CODEC = StoredItem.CODEC.listOf().xmap(DriveContents::new, DriveContents::items);

    public static final StreamCodec<RegistryFriendlyByteBuf, DriveContents> STREAM_CODEC = StoredItem.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(DriveContents::new, DriveContents::items);

    public DriveContents {
        items = List.copyOf(items);
    }

    public long total() {
        long total = 0;

        for (var item : items) {
            total += item.count();
        }

        return total;
    }

    public int types() {
        return items.size();
    }

    public int indexOf(@NonNull ItemStack stack) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).matches(stack)) {
                return i;
            }
        }

        return -1;
    }

    public long count(int index) {
        return index < 0 ? 0 : items.get(index).count();
    }

    public @NonNull DriveContents add(int index, @NonNull ItemStack stack, long amount) {
        var copy = new ArrayList<>(items);

        if (index < 0) {
            copy.add(new StoredItem(StoredItem.key(stack), amount));
        } else {
            copy.set(index, copy.get(index).withCount(copy.get(index).count() + amount));
        }

        return new DriveContents(copy);
    }

    public @NonNull DriveContents remove(int index, long amount) {
        var copy = new ArrayList<>(items);
        long left = copy.get(index).count() - amount;

        if (left <= 0) {
            copy.remove(index);
        } else {
            copy.set(index, copy.get(index).withCount(left));
        }

        return new DriveContents(copy);
    }
}
