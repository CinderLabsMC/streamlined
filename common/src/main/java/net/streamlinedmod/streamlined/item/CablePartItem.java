package net.streamlinedmod.streamlined.item;

import net.streamlinedmod.streamlined.cable.part.PartType;
import org.jspecify.annotations.NonNull;

public class CablePartItem extends GeoModelItem {

    private final PartType part;

    public CablePartItem(@NonNull Properties properties, @NonNull PartType part) {
        super(properties);
        this.part = part;
    }

    public @NonNull PartType part() {
        return part;
    }
}
