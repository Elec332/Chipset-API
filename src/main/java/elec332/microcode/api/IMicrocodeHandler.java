package elec332.microcode.api;

import elec332.promprogrammer.api.IPROMData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 22-5-2018
 */
public interface IMicrocodeHandler {

    public void freezeBitRegistry();

    public int getInputs();

    public int getOutputs();

    public int stages();

    public int getStageBitCount();

    public int getInstructionBitCount();

    public int getDataBitCount();

    default public IMicrocodeBit provisionBit(String desc){
        return provisionBit(desc, -1);
    }

    public int getRequiredChips(IPROMData chip);

    public void writeData(IPROMData chip);

    public IMicrocodeBit provisionBit(String desc, int id);

    public IInputBit provisionInputBit(String desc, int id);

    public IInputBit[] getInputBitsExcept(IInputBit... bits);

    public IInputBit getWildcard(IInputBit... exclusive);

    default public void addInstruction(int instruction, Consumer<IMicrocodeInstruction.Builder> builder, IInputBit input1, IInputBit input2, IInputBit[] inputs){
        List<IInputBit> vals = new ArrayList<>();
        vals.add(input1);
        vals.add(input2);
        vals.addAll(Arrays.asList(inputs));
        addInstruction(instruction, builder, vals.toArray(new IInputBit[0]));
    }

    default public void addInstruction(int instruction, Consumer<IMicrocodeInstruction.Builder> builder, IInputBit input, IInputBit[] inputs){
        List<IInputBit> vals = new ArrayList<>();
        vals.add(input);
        vals.addAll(Arrays.asList(inputs));
        addInstruction(instruction, builder, vals.toArray(new IInputBit[0]));
    }

    public void addInstruction(int instruction, Consumer<IMicrocodeInstruction.Builder> builder, IInputBit... inputs);

}
