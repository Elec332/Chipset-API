package elec332.microcode.api;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 22-5-2018
 */
public interface IMicrocodeHandler {

    public int getInputs();

    public int getOutputs();

    public int stages();

    public int chips();

    default public IMicrocodeBit provisionBit(String desc){
        return provisionBit(desc, -1);
    }

    public IMicrocodeBit provisionBit(String desc, int id);

    public IMicrocodeInstruction addInstruction(Consumer<IMicrocodeInstruction.Builder> builder);

}
