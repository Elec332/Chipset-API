package elec332.microcode.impl;

import elec332.microcode.api.IInputBit;
import elec332.microcode.api.IMicrocodeHandler;
import elec332.promprogrammer.api.IPROMData;

import java.util.Optional;
import java.util.function.IntUnaryOperator;

/**
 * Created by Elec332 on 7-6-2018
 */
public class PROMWriteHandler {

    public PROMWriteHandler(IMicrocodeHandler handler){
        this.handler = (AbstractMicrocodeHandler) handler;
    }

    private final AbstractMicrocodeHandler handler;
    private final boolean debug = false;

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
        return Integer.SIZE - Integer.numberOfLeadingZeros(chips - 1);
    }

    public IntUnaryOperator writeData(IPROMData chipType){
        return writeData(chipType, -1);
    }

    public IntUnaryOperator writeData(IPROMData chipType, int chipNr){
        int chips = getRequiredChips(chipType);
        if (chips == -1){
            throw new IllegalArgumentException();
        }
        int cBits = chipNr >= 0 ? 0 : getChipBits(chips);
        if (chipNr >= 0) {
            System.out.println("Required chip identifier bits: " + cBits);
        }
        int addrBitsTotal = chipType.getAddressBits().length;
        if (handler.getMaxInputBit() + cBits >= addrBitsTotal){
            throw new IllegalArgumentException();
        }
        return i -> getByte(i, cBits, addrBitsTotal, chipType.getIOBits().length, chipNr);
    }

    /**
     * First bits: Instruction bits
     * Last bits: Chip ID bits
     * First bits after address bits: Stage bits
     * Rest: Other inputs
     */
    private int getByte(int address, int chipBits, int chipAddrBits, int outputBits, int chipNr){
        address ^= (address >> chipAddrBits) << chipAddrBits; //Ugly way to only get the first x bytes, not needed, but cleaner for debug

        int instructionBits = handler.getInstructionBitCount();
        int stageBits = handler.getStageBitCount();
        int otherBits = instructionBits + stageBits;
        int dataBits = chipAddrBits - instructionBits - chipBits - stageBits;

        int offSet = 0;
        int instruction = address & createMask(instructionBits);
        offSet += instructionBits;
        int stage = (address >> offSet) & createMask(stageBits);
        offSet += stageBits;
        int data = (address >> offSet) & createMask(dataBits);
        offSet += dataBits;
        int chip = (address >> offSet) & createMask(chipBits);

        if (chipNr < 0){
            chipNr = chip;
        }

        int dataMask = 0;
        for (IInputBit inputBit : handler.getNonStageInputBits()){
            dataMask += (1 << (inputBit.getBitIndex() - otherBits));
        }
        data &= dataMask; //auto-wildcard unassigned input bits

        int instrData = instruction | data << otherBits;
        Optional<MicroInstructionBuilder> mib = Optional.ofNullable(handler.instructions.get(instrData));

        int ret = mib
                .map(ib -> ib.getData(stage))
                .orElse(0);
        ret >>= outputBits * chipNr;
        ret &= createMask(outputBits);

        if (debug && instruction == 0) {
            System.out.println("--------------------------------");
            System.out.println(7 - handler.getInstructionBitCount() - handler.getStageBitCount());
            System.out.println("Address = " + Integer.toBinaryString(address));
            System.out.println("Instruction = " + Integer.toBinaryString(instruction));
            System.out.println("Stage = " + Integer.toBinaryString(stage));
            System.out.println("Data = " + Integer.toBinaryString(data));
            System.out.println("Chip = " + Integer.toBinaryString(chip));
            System.out.println();
            System.out.println("Instruction = " + instruction);
            System.out.println("Stage = " + stage);
            System.out.println("Chip = " + chip);
            System.out.println();
            System.out.println(Integer.toBinaryString(instrData));
            System.out.println();
            System.out.println(mib.map(MicroInstructionBuilder::getDescription).orElse("null"));
            System.out.println("R = "+Integer.toBinaryString(ret));
            System.out.println("--------------------------------");
        }

        return ret;
    }

    private int createMask(int depth){
        int dataMask = 0;
        for (int i = 0; i < depth; i++) {
            dataMask |= (1 << i);
        }
        return dataMask;
    }

}
