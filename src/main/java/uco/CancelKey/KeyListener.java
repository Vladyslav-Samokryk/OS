package CancelKey;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.util.Random;
import CancelKey.KeyListener;
import java.util.logging.*;
import org.jnativehook.NativeHookException;
import java.lang.*;

public class KeyListener implements NativeKeyListener {

    public int f_is;
    public int g_is;
    public int i;
    public KeyTwo listen = null;
    public int numer;

    public void nativeKeyPressed(NativeKeyEvent e) {
        // Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        // logger.setLevel(Level.OFF);
        // logger.setUseParentHandlers(false);
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                e1.printStackTrace();
            }
            System.out.println("If you want stop cancelled press: y");
            while (i < 60) {
                i++;
                try {
                    System.out.println(i + "...");
                    GlobalScreen.registerNativeHook();
                    listen = new KeyTwo();
                    GlobalScreen.addNativeKeyListener(listen);
                } catch (NativeHookException ex) {
                    numer++;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    System.err.format("IOException: %s%n", e1);
                }
            }
            if (listen.ret == 0) {
                System.out.println("Computation was cancelled.");
                System.exit(0);
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {}

    public void nativeKeyTyped(NativeKeyEvent e) {}

}