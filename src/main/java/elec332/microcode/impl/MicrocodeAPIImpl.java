package elec332.microcode.impl;

import elec332.microcode.MicrocodeAPI;
import elec332.microcode.api.IMicrocodeAPI;
import elec332.microcode.api.IMicrocodeHandler;
import elec332.microcode.api.IPROMData;
import elec332.microcode.api.IPROMHandler;

import java.lang.reflect.Field;

/**
 * Created by Elec332 on 22-5-2018
 */
enum MicrocodeAPIImpl implements IMicrocodeAPI {

    INSTANCE;

    @Override
    public IMicrocodeHandler createPredefinedHandler(int inputs, int outputs, int stages, IPROMData chip) {
        return null;
    }

    @Override
    public IMicrocodeHandler createPredefinedHandler(int inputs, int outputs, int stages, IPROMData chip, int chips) {
        return null;
    }

    @Override
    public IMicrocodeHandler createDynamicHandler(int inputs, IPROMData chip) {
        return null;
    }

    @Override
    public IMicrocodeHandler createDynamicHandler(int inputs, int stages, IPROMData chip) {
        return null;
    }

    @Override
    public IPROMHandler getPROMHandler() {
        return null;
    }

    static {
        initialize();
    }

    private static void initialize() {
        try {
            Field f = MicrocodeAPI.class.getDeclaredField("apiImpl");
            f.setAccessible(true);
            int i = f.getModifiers();
            Field modifier = f.getClass().getDeclaredField("modifiers");
            i &= -17;
            modifier.setAccessible(true);
            modifier.setInt(f, i);
            f.set(MicrocodeAPI.INSTANCE, INSTANCE);
            f.setAccessible(false);
        } catch (Exception e){
            System.out.println("Failed to init API implementation");
            throw new ExceptionInInitializerError(e);
        }
    }

}
