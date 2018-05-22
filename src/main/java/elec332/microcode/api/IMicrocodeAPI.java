package elec332.microcode.api;

/**
 * Created by Elec332 on 22-5-2018
 */
public interface IMicrocodeAPI {

    public IMicrocodeHandler createPredefinedHandler(int inputs, int outputs, int stages, IPROMData chip);

    public IMicrocodeHandler createPredefinedHandler(int inputs, int outputs, int stages, IPROMData chip, int chips);

    public IMicrocodeHandler createDynamicHandler(int inputs, IPROMData chip);

    public IMicrocodeHandler createDynamicHandler(int inputs, int stages, IPROMData chip);

    public IPROMHandler getPROMHandler();

}
