package elec332.microcode.api;

import elec332.promprogrammer.api.IPROMData;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 22-5-2018
 */
public interface IMicrocodeHandler {

    public void freezeBitRegistry();

    public int getInputs();

    public int getOutputs();

    public int stages();

    default public IMicrocodeBit provisionBit(String desc){
        return provisionBit(desc, -1);
    }

    public int getRequiredChips(IPROMData chip);

    public void writeData(IPROMData chip);

    public IMicrocodeBit provisionBit(String desc, int id);

    public IInputBit provisionInputBit(String desc, int id);

    public void addInstruction(int instruction, Consumer<IMicrocodeInstruction.Builder> builder, IInputBit... inputs);

}
