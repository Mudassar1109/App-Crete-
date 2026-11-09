package com.termux.terminal;

/**
 * Native methods for creating and managing pseudoterminal subprocesses.
 * Includes graceful fallback detection when the native library is not packaged.
 */
final class JNI {

    private static boolean sNativeLoaded = false;

    static {
        try {
            System.loadLibrary("termux");
            sNativeLoaded = true;
        } catch (Throwable ignored) {
            sNativeLoaded = false;
        }
    }

    public static boolean isNativeLoaded() {
        return sNativeLoaded;
    }

    public static int createSubprocess(String cmd, String cwd, String[] args, String[] envVars, int[] processId, int rows, int columns, int cellWidth, int cellHeight) {
        if (!sNativeLoaded) {
            throw new UnsupportedOperationException("Native library libtermux is not available");
        }
        return nativeCreateSubprocess(cmd, cwd, args, envVars, processId, rows, columns, cellWidth, cellHeight);
    }

    public static void setPtyWindowSize(int fd, int rows, int cols, int cellWidth, int cellHeight) {
        if (!sNativeLoaded) return;
        try {
            nativeSetPtyWindowSize(fd, rows, cols, cellWidth, cellHeight);
        } catch (Throwable ignored) {}
    }

    public static int waitFor(int processId) {
        if (!sNativeLoaded) return 0;
        try {
            return nativeWaitFor(processId);
        } catch (Throwable ignored) {
            return 0;
        }
    }

    public static void close(int fileDescriptor) {
        if (!sNativeLoaded) return;
        try {
            nativeClose(fileDescriptor);
        } catch (Throwable ignored) {}
    }

    private static native int nativeCreateSubprocess(String cmd, String cwd, String[] args, String[] envVars, int[] processId, int rows, int columns, int cellWidth, int cellHeight);
    private static native void nativeSetPtyWindowSize(int fd, int rows, int cols, int cellWidth, int cellHeight);
    private static native int nativeWaitFor(int processId);
    private static native void nativeClose(int fileDescriptor);
}
