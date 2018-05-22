package elec332.microcode;

import elec332.microcode.api.IMicrocodeAPI;
import elec332.microcode.api.IMicrocodeHandler;
import elec332.microcode.api.IPROMData;
import elec332.microcode.api.IPROMHandler;

/**
 * Created by Elec332 on 22-5-2018
 */
public enum MicrocodeAPI implements IMicrocodeAPI {

    INSTANCE;

    @Override
    public IMicrocodeHandler createPredefinedHandler(int inputs, int outputs, int stages, IPROMData chip) {
        return apiImpl.createPredefinedHandler(inputs, outputs, stages, chip);
    }

    @Override
    public IMicrocodeHandler createPredefinedHandler(int inputs, int outputs, int stages, IPROMData chip, int chips) {
        return apiImpl.createPredefinedHandler(inputs, outputs, stages, chip, chips);
    }

    @Override
    public IMicrocodeHandler createDynamicHandler(int inputs, IPROMData chip) {
        return apiImpl.createDynamicHandler(inputs, chip);
    }

    @Override
    public IMicrocodeHandler createDynamicHandler(int inputs, int stages, IPROMData chip) {
        return apiImpl.createDynamicHandler(inputs, stages, chip);
    }

    @Override
    public IPROMHandler getPROMHandler() {
        return apiImpl.getPROMHandler();
    }

    private static final IMicrocodeAPI apiImpl;

    static {
        try {
            apiImpl = null;
            Class.forName("elec332.microcode.impl.MicrocodeAPIImpl");
        } catch (Exception e){
            System.out.println("Failed to initialize Microcode-API");
            throw new RuntimeException("Failed to initialize Microcode-API", e);
        }
    }

}
