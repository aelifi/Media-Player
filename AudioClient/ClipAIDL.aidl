// ClipAIDL.aidl
package com.example.clipserver;

// Declare any non-default types here with import statements

interface ClipAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void playClip(String name);
    void pauseClip();
    void resumeClip();
    void stopClip();
    void stopService();
}