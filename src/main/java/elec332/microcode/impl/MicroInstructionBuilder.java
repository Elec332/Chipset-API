package elec332.microcode.impl;

import elec332.microcode.api.IMicrocodeBit;
import elec332.microcode.api.IMicrocodeInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 7-6-2018
 */
class MicroInstructionBuilder implements IMicrocodeInstruction.Builder {

    MicroInstructionBuilder(){
        this.data = new ArrayList<>();
        this.desc = "";
    }

    private final List<IMicrocodeBit[]> data;
    private String desc;

    @Override
    public void addStage(IMicrocodeBit bit, IMicrocodeBit... bits) {
        IMicrocodeBit[] s = new MicrocodeBit[bits.length + 1];
        s[0] = bit;
        System.arraycopy(bits, 0, s, 1, bits.length);
        data.add(s);
    }

    @Override
    public void setDescription(String desc) {
        this.desc = desc;
    }

    void merge(MicroInstructionBuilder other){
        data.addAll(other.data);
        setDescription(other.desc);
    }

}
