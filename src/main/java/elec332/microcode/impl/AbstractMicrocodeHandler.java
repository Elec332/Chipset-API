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
        this.stageBits = Integer.SIZE - Integer.numberOfLeadingZeros(stages);
        for (int i = 0; i < instructionBits; i++) {
            instructionInputs.add(provisionInputBit("Instruction bit " + i, i));
        }
        for (int i = instructionBits; i < instructionBits + stageBits; i++) {
            provisionInputBit("Stage bit " + (i - instructionBits), i);
        }

        this.init = true;
        this.instructions = new HashMap<>();
        this.writeHandler = new PROMWriteHandler(this);
    }

    protected final Map<Integer, MicroInstructionBuilder> instructions;

    private final int stages, instructionBits, stageBits;
    private final Map<Integer, IMicrocodeBit> provisionedBits;
    private final Map<Integer, IInputBit> provisionedInputs;
    @SuppressWarnings("all")
    private final List<Integer> freeInputs;
    private final List<IInputBit> nonStageInputBits;
    private final List<IInputBit> nonStageInputs_;
    private final List<IInputBit> instructionInputs_;

    private final PROMWriteHandler writeHandler;
    private IInputBit wildcard;
    private IInputBit[] wildcardArr;

    private boolean frozen, init;
    private int inputs, outputs;

    @Override
    public void addInstruction(int instruction, Consumer<IMicrocodeInstruction.Builder> builder, IInputBit... inputz) {
        List<IInputBit> validBits = new ArrayList<>();
        if (instruction < -1){ //Plz...
            throw new IllegalArgumentException();
        } else if (instruction == -1){
            validBits.addAll(instructionInputs_);
        }
        List<IInputBit> inputs = new ArrayList<>(Arrays.asList(inputz));
        Arrays.stream(inputz).forEach(inputBit -> {
            if (inputBit instanceof MicrocodeBit.Wildcard){
                validBits.addAll(((MicrocodeBit.Wildcard) inputBit).bits);
            }
        });
        validBits.removeAll(inputs);
        if (instruction >= 0){
            int inst = instruction;
            for (int i = 0; i < instructionBits; i++) {
                if ((inst & 1) == 1){
                    inputs.add(instructionInputs_.get(i));
                }
                inst = inst >> 1;
            }
        }
        inputs.removeIf(inputBit -> inputBit instanceof MicrocodeBit.Wildcard);
        int compiledInput = compile(inputs);

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
            if (instruction>=0)
            System.out.println(data);
            int iD = compile(data) | compiledInput;
            if (instruction>=0)
            System.out.println("add builder to "+Integer.toBinaryString(iD));
            instructions.computeIfAbsent(iD, integer -> new MicroInstructionBuilder()).merge(mib);
        }

    }

    @Override
    public final int getDataBitCount() {
        return getNonStageInputBits().size();
    }

    @Override
    public int getStageBitCount() {
        return stageBits;
    }

    @Override
    public int getInstructionBitCount() {
        return instructionBits;
    }


    @Override
    public int getRequiredChips(IPROMData chip) {
        return this.writeHandler.getRequiredChips(chip);
    }

    @Override
    public void writeData(IPROMData chip) {
        this.writeHandler.writeData(chip, -1);
    }

    private int compile(List<IInputBit> bits){
        int ret = 0;
        for (IInputBit b : bits){
            ret |= 1 << b.getBitIndex();
        }
        return ret;
    }

    @Override
    public IInputBit[] getInputBitsExcept(IInputBit... exclusive) {
        checkFreeze(true);
        if (exclusive.length == 0){
            return wildcardArr;
        }
        return getWC(exclusive).toArray(new IInputBit[0]);
    }

    private List<IInputBit> getWC(IInputBit... exclusive){
        Arrays.stream(exclusive).forEach(inputBit -> {
            if (provisionedInputs.get(inputBit.getBitIndex()) != inputBit){
                throw new IllegalArgumentException();
            }
        });
        List<IInputBit> rl = new ArrayList<>(getNonStageInputBits());
        rl.removeAll(Arrays.asList(exclusive));
        return rl;
    }

    @Override
    public IInputBit getWildcard(IInputBit... exclusive) {
        checkFreeze(true);
        if (exclusive.length == 0){
            return wildcard;
        }
        return new MicrocodeBit.Wildcard(getWC(exclusive));
    }

    private List<IInputBit> getNonStageInputBits() {
        checkFreeze(true);
        return this.nonStageInputs_;
    }

    @Override
    public final int stages() {
        return this.stages;
    }

    @Override
    public final int getOutputs() {
        checkFreeze(true);
        return this.outputs;
    }

    @Override
    public final int getInputs() {
        checkFreeze(true);
        return this.inputs;
    }

    @Override
    public IMicrocodeBit provisionBit(String desc, int id) {
        checkFreeze(false);
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
        checkFreeze(false);
        if (this.provisionedInputs.containsKey(id)){
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
        wildcard = new MicrocodeBit.Wildcard(nonStageInputBits);
        wildcardArr = nonStageInputBits.toArray(new IInputBit[0]);
    }

    private void checkFreeze(boolean needsFreeze){
        if (!frozen == needsFreeze){
            if (needsFreeze) {
                throw new IllegalArgumentException("Bit registry hasn't been frozen yet!");
            } else {
                throw new IllegalArgumentException("Bit registry has been frozen!");
            }
        }
    }

}
