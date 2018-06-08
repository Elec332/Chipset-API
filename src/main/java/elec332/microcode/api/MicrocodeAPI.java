package elec332.microcode.api;

/**
 * Created by Elec332 on 22-5-2018
 */
public class MicrocodeAPI {

    public static IMicrocodeAPI getMicrocodeAPI(){
        return handler;
    }

    private static final IMicrocodeAPI handler;

    static {
        handler = (instructionBits, stages) -> {
            throw new UnsupportedOperationException();
        };

        try {
            Class.forName("elec332.microcode.impl.MicrocodeAPIImpl");
        } catch (Exception e){
            //throw new RuntimeException("Failed to initialize Microcode-API", e);
        }
    }

}
