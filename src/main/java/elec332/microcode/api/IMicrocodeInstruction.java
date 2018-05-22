package elec332.microcode.api;

import java.util.List;

/**
 * Created by Elec332 on 22-5-2018
 */
public interface IMicrocodeInstruction {

    public List<IMicrocodeBit[]> getBits();

    public String getDescription();

    public interface Builder {

        public void addStage(IMicrocodeBit bit, IMicrocodeBit... bits);

    }

}
