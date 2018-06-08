package elec332.microcode.api;

/**
 * Created by Elec332 on 22-5-2018
 */
public interface IMicrocodeAPI {

    public IMicrocodeHandler createDynamicHandler(int instructionBits, int stages);

}
