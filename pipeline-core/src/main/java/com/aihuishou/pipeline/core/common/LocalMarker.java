package com.aihuishou.pipeline.core.common;

import java.util.concurrent.atomic.AtomicBoolean;

public class LocalMarker implements Marker {

    private final AtomicBoolean marker = new AtomicBoolean();

    @Override
    public boolean mark() {
        return marker.compareAndSet(false, true);
    }

    @Override
    public boolean isMarked() {
        return marker.get();
    }

    @Override
    public void reset() {
        marker.set(false);
    }

}
