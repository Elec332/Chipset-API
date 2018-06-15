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
    private int[] data_ = null;

    @Override
    public void addStage(IMicrocodeBit... bits) {
        checkCompile();
        IMicrocodeBit[] s = new MicrocodeBit[bits.length];
        System.arraycopy(bits, 0, s, 0, bits.length);
        data.add(s);
    }

    @Override
    public void setDescription(String desc) {
        this.desc = desc;
    }

    @Override
    public String getDescription() {
        return this.desc;
    }

    int getData(int stage){
        if (data_ == null){
            data_ = new int[data.size()];
            for (int i = 0; i < data.size(); i++) {
                int byt = 0;
                for (IMicrocodeBit mcb : data.get(i)){
                    byt |= 1 << mcb.getBitIndex();
                }
                data_[i] = byt;
            }
        }
        if (stage >= data_.length){
            return 0;
        }
        return data_[stage];
    }

    void merge(MicroInstructionBuilder other){
        checkCompile();
        data.addAll(other.data);
        setDescription(other.desc);
    }

    private void checkCompile(){
        if (data_ != null){
            throw new IllegalStateException("Data has already been compiled!");
        }
    }

}
