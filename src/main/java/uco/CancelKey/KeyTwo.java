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

public class KeyTwo implements NativeKeyListener {

    public int f_is;
    public int g_is;
    public int ret = 1;

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_0) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                e1.printStackTrace();
            }
            ret = 0;
            System.out.println("You stoped cancelation.");
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {}

    public void nativeKeyTyped(NativeKeyEvent e) {}

}