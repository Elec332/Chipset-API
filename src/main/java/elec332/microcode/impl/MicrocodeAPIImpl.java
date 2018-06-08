package elec332.microcode.impl;

import elec332.microcode.api.MicrocodeAPI;
import elec332.microcode.api.IMicrocodeAPI;
import elec332.microcode.api.IMicrocodeHandler;

import java.lang.reflect.Field;

/**
 * Created by Elec332 on 22-5-2018
 */
enum MicrocodeAPIImpl implements IMicrocodeAPI {

    INSTANCE;

    @Override
    public IMicrocodeHandler createDynamicHandler(int instructionBits, int stages) {
        return new DynamicMicrocodeHandler(instructionBits, stages);
    }

    static {
        initialize();
    }

    private static void initialize() {
        try {
            Field f = MicrocodeAPI.class.getDeclaredField("handler");
            f.setAccessible(true);
            int i = f.getModifiers();
            Field modifier = f.getClass().getDeclaredField("modifiers");
            i &= -17;
            modifier.setAccessible(true);
            modifier.setInt(f, i);
            f.set(null, INSTANCE);
            f.setAccessible(false);
        } catch (Exception e){
            System.out.println("Failed to init API implementation");
            throw new ExceptionInInitializerError(e);
        }
    }

}
