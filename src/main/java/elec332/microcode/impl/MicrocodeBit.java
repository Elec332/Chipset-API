package elec332.microcode.impl;

import elec332.microcode.api.IInputBit;
import elec332.microcode.api.IMicrocodeBit;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return getDescription();
    }

    static class Wildcard extends MicrocodeBit {

        Wildcard(List<IInputBit> bits) {
            super(0, "");
            this.bits = bits;
        }

        List<IInputBit> bits;

        @Override
        public int getBitIndex() {
            throw new UnsupportedOperationException("getGitIndex not supported in wildcards.");
        }

        @Override
        public int getValue() {
            throw new UnsupportedOperationException("getValue not supported in wildcards.");
        }

        @Override
        public String getDescription() {
            return String.join(" ", bits.stream().map(IInputBit::toString).collect(Collectors.toList()));
        }

    }

}
