package com.kaasbrot.boehlersyugiohapp;

import android.os.Handler;

import java.util.HashSet;

public class CooldownTracker {

    private HashSet<String> trackings = new HashSet<>();
    private Handler handler = new Handler();

    /**
     * Track string. Returns false if cooldown for specified name has not run out yet. I.e., if
     * "coinButton" returns false, this means the 500ms hasn't passed yet and the action should
     * probably be cancelled.
     *
     * @param name Unique name to be tracked. Make sure to avoid duplicates.
     * @param delayMillis How many ms should be waited before the cooldown runs out.
     * @return true if name was successfully added to the list of tracked cooldowns.
     */
    public boolean tryAndStartTracker(String name, long delayMillis) {
        if(trackings.contains(name))
            return false;

        trackings.add(name);
        handler.postDelayed(() -> trackings.remove(name), delayMillis);
        return true;
    }

    /**
     * See {@link CooldownTracker#tryAndStartTracker(String, long)}. Defaults to 500ms.
     *
     * @param name Unique name to be tracked. Make sure to avoid duplicates.
     * @return true if name was successfully added to the list of tracked cooldowns.
     */
    public boolean tryAndStartTracker(String name) {
        return tryAndStartTracker(name, 500);
    }
}
