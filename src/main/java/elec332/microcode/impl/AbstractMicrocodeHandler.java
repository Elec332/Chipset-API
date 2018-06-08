package elec332.microcode.impl;

import elec332.microcode.api.IInputBit;
import elec332.microcode.api.IMicrocodeBit;
import elec332.microcode.api.IMicrocodeHandler;
import elec332.microcode.api.IMicrocodeInstruction;
import elec332.promprogrammer.api.IPROMData;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 7-6-2018
 */
abstract class AbstractMicrocodeHandler implements IMicrocodeHandler {

    AbstractMicrocodeHandler(int instructionBits, int stages){
        this.stages = stages;
        this.instructionBits = instructionBits;
        this.provisionedBits = new HashMap<>();
        this.provisionedInputs = new HashMap<>();
        this.freeInputs = new ArrayList<>();
        this.nonStageInputBits = new ArrayList<>();
        this.nonStageInputs_ = Collections.unmodifiableList(this.nonStageInputBits);
        List<IInputBit> instructionInputs = new ArrayList<>();
        this.instructionInputs_ = Collections.unmodifiableList(instructionInputs);
        int stageBits = Integer.SIZE - Integer.numberOfLeadingZeros(stages);
        for (int i = 0; i < stageBits; i++) {
            provisionInputBit("Stage bit " + i, i);
        }
        for (int i = stageBits; i < stageBits + instructionBits; i++) {
            instructionInputs.add(provisionInputBit("Instruction bit " + i, i));
        }
        this.init = true;
        this.instructions = new HashMap<>();
        this.writeHandler = new PROMWriteHandler(this);
    }

    private final Map<Integer, MicroInstructionBuilder> instructions;

    private final int stages, instructionBits;
    private final Map<Integer, IMicrocodeBit> provisionedBits;
    private final Map<Integer, IInputBit> provisionedInputs;
    private final List<Integer> freeInputs;
    private final List<IInputBit> nonStageInputBits;
    private final List<IInputBit> nonStageInputs_;
    private final List<IInputBit> instructionInputs_;

    private final PROMWriteHandler writeHandler;

    private boolean frozen, init;
    private int inputs, outputs;

    @Override
    public void addInstruction(int instruction, Consumer<IMicrocodeInstruction.Builder> builder, IInputBit... inputz) {
        List<IInputBit> validBits = new ArrayList<>();
        if (instruction < -1){ //Plz...
            throw new IllegalArgumentException();
        } else if (instruction == -1){
            validBits.addAll(getInstructionBits());
        }
        List<IInputBit> inputs = Arrays.asList(inputz);
        int compiledInput = compile(inputs);
        validBits.addAll(getNonStageInputBits());
        validBits.removeAll(inputs);
        MicroInstructionBuilder mib = new MicroInstructionBuilder();
        builder.accept(mib);

        int n = validBits.size();
        for (int i = 0; i < Math.pow(2, n); i++) {
            List<IInputBit> data = new ArrayList<>();
            StringBuilder bin = new StringBuilder(Integer.toBinaryString(i));
            while (bin.length() < n) {
                bin.insert(0, "0");
            }
            char[] chars = bin.toString().toCharArray();
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == '1'){
                    data.add(validBits.get(j));
                }
            }
            int iD = compile(data) | compiledInput;
            instructions.computeIfAbsent(iD, integer -> new MicroInstructionBuilder()).merge(mib);
        }

    }

    @Override
    public int getRequiredChips(IPROMData chip) {
        return this.writeHandler.getRequiredChips(chip);
    }

    @Override
    public void writeData(IPROMData chip) {
        this.writeHandler.writeData(chip);
    }

    private int compile(List<IInputBit> bits){
        int ret = 0;
        for (IInputBit b : bits){
            ret |= 1 << b.getBitIndex();
        }
        return ret;
    }

    protected final void checkBitValidity(IMicrocodeBit bit){
        if (this.provisionedBits.get(bit.getBitIndex()) != bit){
            throw new IllegalArgumentException("Invalid bit!");
        }
    }

    protected final boolean isFrozen() {
        return this.frozen;
    }

    protected final List<Integer> getFreeInputs() {
        if (!isFrozen()){
            throw new IllegalArgumentException("Bit registry hasn't been frozen yet!");
        }
        return this.freeInputs;
    }

    protected final List<IInputBit> getNonStageInputBits() {
        if (!isFrozen()){
            throw new IllegalArgumentException("Bit registry hasn't been frozen yet!");
        }
        return this.nonStageInputs_;
    }

    protected final List<IInputBit> getInstructionBits() {
        if (!isFrozen()){
            throw new IllegalArgumentException("Bit registry hasn't been frozen yet!");
        }
        return this.instructionInputs_;
    }

    @Override
    public final int stages() {
        return this.stages;
    }

    @Override
    public final int getOutputs() {
        if (!isFrozen()){
            throw new IllegalArgumentException("Bit registry hasn't been frozen yet!");
        }
        return this.outputs;
    }

    @Override
    public final int getInputs() {
        if (!isFrozen()){
            throw new IllegalArgumentException("Bit registry hasn't been frozen yet!");
        }
        return this.inputs;
    }

    @Override
    public IMicrocodeBit provisionBit(String desc, int id) {
        if (isFrozen()){
            throw new IllegalArgumentException("Bit registry has been frozen!");
        }
        if (this.provisionedBits.containsKey(id)){
            throw new IllegalArgumentException("Bit " + id + " is already in use!");
        }
        if (id < -1){
            throw new IllegalArgumentException("Bit id cannot be negative!");
        } else if (id == -1){
            for (int i = 0; i < Byte.MAX_VALUE; i++) {
                if (!this.provisionedBits.containsKey(i)){
                    id = i;
                    System.out.println("Assigned ID " + id + " to instruction bit \"" + desc + "\"");
                    break;
                }
            }
        }
        IMicrocodeBit ret = new MicrocodeBit(id, desc);
        this.provisionedBits.put(id, ret);
        return ret;
    }

    @Override
    public IInputBit provisionInputBit(String desc, int id) {
        if (isFrozen()){
            throw new IllegalArgumentException("Bit registry has been frozen!");
        }
        if (this.provisionedBits.containsKey(id)){
            throw new IllegalArgumentException("Bit " + id + " is already in use!");
        }
        if (id < 0){
            throw new IllegalArgumentException("Bit id cannot be negative!");
        }
        IInputBit ret = new MicrocodeBit(id, desc);
        this.provisionedInputs.put(id, ret);
        if (this.init){
            this.nonStageInputBits.add(ret);
        }
        return ret;
    }

    @Override
    public void freezeBitRegistry() {
        this.frozen = true;
        this.inputs = provisionedInputs.entrySet().size();
        this.outputs = provisionedBits.entrySet().size();
        for (int i : provisionedInputs.keySet()){
            if (inputs < i){
                inputs = i;
            }
        }
        for (int i = 0; i < inputs; i++) {
            if (!provisionedInputs.keySet().contains(i)){
                freeInputs.add(i);
            }
        }
        for (int i : provisionedBits.keySet()){
            if (outputs < i){
                outputs = i;
            }
        }
        if (inputs > 31){
            throw new StackOverflowError("More than 31 inputs will overflow integers!");
        }
    }

}
