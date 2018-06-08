package elec332.microcode.impl;

import elec332.promprogrammer.api.IPROMData;

/**
 * Created by Elec332 on 7-6-2018
 */
class PROMWriteHandler {

    PROMWriteHandler(AbstractMicrocodeHandler handler){
        this.handler = handler;
    }

    private final AbstractMicrocodeHandler handler;

    public int getRequiredChips(IPROMData chip){
        int chips = 0;
        float outputs = handler.getOutputs();
        int stageBits = getChipBits(handler.stages()) + 1;
        int instBits = handler.getInstructionBits().size();
        while (true) {
            chips++;
            int cb = getChipBits(chips);
            int cbl = chip.getAddressBits().length;
            if (cb > cbl / 2f || cb + stageBits + instBits + 1 > cbl) {
                return -1;
            }
            float ip = stageBits * chips + instBits * chips + cb + handler.getNonStageInputBits().size();
            if (chips * cbl > ip) {
                int outputMul = (int) Math.floor(outputs / chip.getIOBits().length);
                return chips * outputMul;
            }
        }
    }

    private int getChipBits(int chips){
        return Integer.SIZE - Integer.numberOfLeadingZeros(chips) - 1;
    }

    public void writeData(IPROMData chip){
        int chips = getRequiredChips(chip);
        if (chips == -1){
            throw new IllegalArgumentException();
        }
        int cBits = getChipBits(chips);

    }

}
