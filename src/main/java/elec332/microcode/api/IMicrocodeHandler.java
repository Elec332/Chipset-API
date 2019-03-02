package elec332.microcode.api;

import elec332.promprogrammer.api.IPROMData;
import elec332.promprogrammer.api.IPROMLink;

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

    /**
     * Writes the microcode to a PROM,
     * If more output bits are required than there are available,
     * additional bits will be reserved from the address lines to identify which chip it is.
     * (so chip 0 will handle IO 0-7, chip 1 8-15, ect.)
     *
     * @param link The PROM to write to
     */
    default public void writeData(IPROMLink link){
        writeData(link, -1);
    }

    /**
     * Writes the microcode to a PROM,
     * The chip-number defines which chip this is.
     * -1 means the chip will need to identified by the input pins.
     * If there are no spare input pins (or is chip identifier pins are undesirable)
     * the chip number can be defined which will save input pins.
     *
     * Chip 0 will handles IO 0->x-1, chip 1 handles IO x->2x-1, ect.
     *
     * @param link The PROM to write to
     * @param chipNumber The chip number
     */
    public void writeData(IPROMLink link, int chipNumber);

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
