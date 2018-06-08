package elec332.microcode.impl;

import elec332.microcode.api.IInputBit;
import elec332.microcode.api.IMicrocodeBit;

/**
 * Created by Elec332 on 7-6-2018
 */
class MicrocodeBit implements IMicrocodeBit, IInputBit {

    MicrocodeBit(int bitIndex, String desc){
        this.bitIndex = bitIndex;
        this.value = 1 << bitIndex;
        this.desc = desc;
    }

    private final int bitIndex, value;
    private final String desc;

    @Override
    public int getBitIndex() {
        return this.bitIndex;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getDescription() {
        return this.desc;
    }

}
