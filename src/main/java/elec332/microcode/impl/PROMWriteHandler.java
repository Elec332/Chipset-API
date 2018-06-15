package elec332.microcode.impl;

import elec332.microcode.api.IMicrocodeHandler;
import elec332.promprogrammer.api.IPROMData;

/**
 * Created by Elec332 on 7-6-2018
 */
public class PROMWriteHandler {

    public PROMWriteHandler(IMicrocodeHandler handler){
        this.handler = (AbstractMicrocodeHandler) handler;
    }

    private final AbstractMicrocodeHandler handler;

    public int getRequiredChips(IPROMData chip){
        int chips = 0;
        int cbl = chip.getAddressBits().length;
        while (true) {
            chips++;
            if (getChipBits(chips) + handler.getInputs() > cbl) {
                return -1;
            }
            if (chips * chip.getIOBits().length >= handler.getOutputs()) {
                return chips;
            }
        }
    }

    private int getChipBits(int chips){
        return Integer.SIZE - Integer.numberOfLeadingZeros(chips) - 1;
    }

    public void writeData(IPROMData chip, int address){
        int chips = getRequiredChips(chip);
        if (chips == -1){
            throw new IllegalArgumentException();
        }
        int cBits = getChipBits(chips);
        getByte(address, cBits, chip.getAddressBits().length);
    }

    /**
     * First bits: Instruction bits
     * Last bits: Chip ID bits
     * First bits after address bits: Stage bits
     * Rest: Other inputs
     */
    public int getByte(int address, int chipBits, int chipAddrBits){
        address ^= (address >> chipAddrBits) << chipAddrBits; //Ugly way to only get the first x bytes, not needed, but cleaner for debug
        int nonInstructionBits = chipAddrBits - handler.getInstructionBitCount();
        int dataBits = nonInstructionBits - chipBits - handler.getStageBitCount();

        int instruction = address >> nonInstructionBits; //Isolate instruction bits
        int chip = address ^ ((address >> chipBits) << chipBits); //Isolate chip bits
        int data = (address ^ (instruction << nonInstructionBits)) >> chipBits; //Isolate bits for instruction and other inputs
        int stage = data >> dataBits;
        int otherData = data ^ (stage << dataBits);

        System.out.println("Address = "+Integer.toBinaryString(address));
        System.out.println("Instruction = "+Integer.toBinaryString(instruction));
        System.out.println("Stage = "+Integer.toBinaryString(stage));
        System.out.println("Data = "+Integer.toBinaryString(otherData));
        System.out.println("Chip = "+Integer.toBinaryString(chip));
        System.out.println();
        System.out.println("Instruction = "+instruction);
        System.out.println("Stage = "+stage);
        System.out.println("Chip = "+chip);
        System.out.println();
        int fullData = instruction;
        fullData |= otherData << (handler.getInstructionBitCount() + handler.getStageBitCount() + dataBits * chip);
        System.out.println(Integer.toBinaryString(fullData));
        System.out.println();
        System.out.println(handler.instructions.get(fullData).getDescription());
        int ret = handler.instructions.get(fullData).getData(stage);
        System.out.println("R = "+Integer.toBinaryString(ret));
        return ret;
    }

}
