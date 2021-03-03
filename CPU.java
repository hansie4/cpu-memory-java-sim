
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class CPU {

    ////////////////////////// MEMORY CONSTANTS //////////////////////////
    final int USER_MEMORY_START = 0;
    final int USER_MEMORY_END = 999;
    final int SYSTEM_MEMORY_START = 1000;
    final int INTERUPT_CALL_EXECUTE_LOCATION = 1500;
    final int SYSTEM_MEMEORY_END = 1999;

    //////////////////////////// I/O OBJECTS ////////////////////////////
    Scanner input;
    PrintWriter output;

    //////////////////////////// REGISTERS ////////////////////////////
    int PC;
    int SP;
    int IR;
    int AC;
    int X;
    int Y;

    //////////////////////////// CPU FLAGS ////////////////////////////
    boolean isRunning = false;
    boolean isInKernalMode = false;

    //////////////////////////// TIMER VARIABLES ////////////////////////////
    int timer = 0;
    int interuptCount;

    //////////////////////////// DEBUG ////////////////////////////
    boolean debugMode;
    File debugFile;
    FileWriter debugFileWriter;
    PrintWriter debugPrintWriter;

    //////////////////////////// CONSTRUCTOR ////////////////////////////
    CPU(InputStream inputStream, OutputStream outputStream, int interuptCount, boolean debugMode) {
        this.input = new Scanner(inputStream);
        this.output = new PrintWriter(outputStream, true);

        this.PC = this.USER_MEMORY_START;
        this.SP = this.USER_MEMORY_END;

        this.interuptCount = interuptCount;

        if (debugMode) {
            this.debugMode = true;

            try {
                this.debugFile = new File("cpuDebug.txt");
                this.debugFileWriter = new FileWriter(this.debugFile);
                this.debugPrintWriter = new PrintWriter(this.debugFileWriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.debugMode = false;
        }
    }

    //////////////////////////// MAIN PROGRAM FUNCTION ////////////////////////////
    void run() {
        this.isRunning = true;
        while (this.isRunning) {

            // GET INSTRUCTION
            this.IR = this.readFromMemory(PC);

            // INCREMENT PC
            this.PC++;

            // EXECUTE INSTRUCTION
            this.decodeInstruction(this.IR);

            // INCREMENT TIMER
            this.timer++;

            // RUN INTERUPT SEQUENCE
            if (this.timer == this.interuptCount) {
                this.interupt(false);
                this.timer = 0;
            }

            // DEBUG
            if (this.debugMode) {
                this.printDebugInfo();
            }
        }
        this.closeStreams();
    }

    //////////////////////////// MEMORY FUNCTIONS ////////////////////////////
    int readFromMemory(int address) {
        output.println("read " + address);

        return Integer.parseInt(input.nextLine());
    }

    void writeToMemory(int data, int address) {
        output.println("write " + data + " " + address);
    }

    ///////////////////////// INSTRUCTION FUNCTIONS /////////////////////////
    // 1
    void loadValue() {
        int value = this.readFromMemory(this.PC);

        this.PC++;

        this.AC = value;
    }

    // 2
    void loadAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode && (address < this.USER_MEMORY_START || address > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            int value = this.readFromMemory(address);

            this.AC = value;
        }
    }

    // 3
    void loadIndAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode && (address < this.USER_MEMORY_START || address > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            int addressToLoadFrom = this.readFromMemory(address);

            if (!this.isInKernalMode
                    && (addressToLoadFrom < this.USER_MEMORY_START || addressToLoadFrom > this.USER_MEMORY_END)) {
                System.out.println("Memory violation: accessing system address " + addressToLoadFrom + " in user mode");
                this.isRunning = false;
                this.closeStreams();
                System.exit(1);
            } else {
                int value = this.readFromMemory(addressToLoadFrom);

                this.AC = value;
            }
        }
    }

    // 4
    void loadIdxXAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode
                && ((address + this.X) < this.USER_MEMORY_START || (address + this.X) > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + (address + this.X) + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            int value = this.readFromMemory((address + this.X));

            this.AC = value;
        }
    }

    // 5
    void loadIdxYAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode
                && ((address + this.Y) < this.USER_MEMORY_START || (address + this.Y) > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + (address + this.Y) + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            int value = this.readFromMemory((address + this.Y));

            this.AC = value;
        }
    }

    // 6
    void loadSpX() {
        if (!this.isInKernalMode && (((this.SP + 1) + this.X) < this.USER_MEMORY_START
                || ((this.SP + 1) + this.X) > this.USER_MEMORY_END)) {
            System.out.println(
                    "Memory violation: accessing system address " + ((this.SP + 1) + this.X) + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            int value = this.readFromMemory(((this.SP + 1) + this.X));

            this.AC = value;
        }
    }

    // 7
    void storeAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode && (address < this.USER_MEMORY_START || address > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            this.writeToMemory(this.AC, address);
        }
    }

    // 8
    void getRand() {
        this.AC = (int) ((Math.random() * 100) + 1);
    }

    // 9
    void putPort() {
        int port = this.readFromMemory(this.PC);

        this.PC++;

        if (port == 1) {
            System.out.print(((int) (this.AC)));
        } else if (port == 2) {
            System.out.print(((char) (this.AC)));
        } else {
            // ERROR CASE
        }
    }

    // 10
    void addX() {
        this.AC += this.X;
    }

    // 11
    void addY() {
        this.AC += this.Y;
    }

    // 12
    void subX() {
        this.AC -= this.X;
    }

    // 13
    void subY() {
        this.AC -= this.Y;
    }

    // 14
    void copyToX() {
        this.X = this.AC;
    }

    // 15
    void copyFromX() {
        this.AC = this.X;
    }

    // 16
    void copyToY() {
        this.Y = this.AC;
    }

    // 17
    void copyFromY() {
        this.AC = this.Y;
    }

    // 18
    void copyToSP() {
        this.SP = this.AC;
    }

    // 19
    void copyFromSP() {
        this.AC = this.SP;
    }

    // 20
    void jumpAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode && (address < this.USER_MEMORY_START || address > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            this.PC = address;
        }
    }

    // 21
    void jumpIfEqualAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode && (address < this.USER_MEMORY_START || address > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            if (this.AC == 0) {
                this.PC = address;
            }
        }
    }

    // 22
    void jumpIfNotEqualAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode && (address < this.USER_MEMORY_START || address > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            if (this.AC != 0) {
                this.PC = address;
            }
        }
    }

    // 23
    void callAddress() {
        int address = this.readFromMemory(this.PC);

        this.PC++;

        if (!this.isInKernalMode && (address < this.USER_MEMORY_START || address > this.USER_MEMORY_END)) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            this.isRunning = false;
            this.closeStreams();
            System.exit(1);
        } else {
            this.writeToMemory(this.PC, this.SP);

            this.SP--;

            this.PC = address;
        }
    }

    // 24
    void ret() {
        this.SP++;

        int returnAddress = this.readFromMemory(this.SP);

        this.writeToMemory(0, this.SP);

        this.PC = returnAddress;
    }

    // 25
    void incrementX() {
        this.X++;
    }

    // 26
    void decrementX() {
        this.X--;
    }

    // 27
    void push() {
        this.writeToMemory(this.AC, this.SP);

        this.SP--;
    }

    // 28
    void pop() {
        this.SP++;

        this.AC = this.readFromMemory(this.SP);

        this.writeToMemory(0, this.SP);
    }

    // 29
    void interupt(boolean systemCall) {
        if (!this.isInKernalMode) {
            this.isInKernalMode = true;

            // SWITCH SP TO SYSTEM STACK
            int tempSP = this.SP;
            this.SP = this.SYSTEM_MEMEORY_END;

            // SAVE OLD SP AND PC TO SYSTEM STACK
            this.writeToMemory(this.PC, this.SP);
            this.SP--;
            this.writeToMemory(tempSP, this.SP);
            this.SP--;

            // EXECUTE AT 1000 OR 1500
            if (systemCall) {
                this.PC = this.INTERUPT_CALL_EXECUTE_LOCATION;
            } else {
                this.PC = this.SYSTEM_MEMORY_START;
            }
        } else {
            // INTERUPT CALLS NOT ALLOWED WITHIN KERNAL MODE
        }
    }

    // 30
    void interuptReturn() {
        if (this.isInKernalMode) {

            // GET THE OLD SP AND PC FROM THE STACK
            this.SP++;
            int newSP = this.readFromMemory(this.SP);
            this.writeToMemory(0, this.SP);
            this.SP++;
            int newPC = this.readFromMemory(this.SP);
            this.writeToMemory(0, this.SP);

            // SWITCH BACK TO OLD PC
            this.SP = newSP;
            this.PC = newPC;

            this.isInKernalMode = false;
        } else {
            // SHOULDENT RETURN FROM SYSTEM CALL WHEN NOT IN KERNAL MODE
        }
    }

    // 50
    void end() {
        this.isRunning = false;
    }

    ////////////////////////// UTILITY FUNCTIONS //////////////////////////
    void decodeInstruction(int instructionNumber) {

        if (this.debugMode) {
            this.printInstruction(instructionNumber);
        }

        switch (instructionNumber) {
            case 1:
                this.loadValue();
                break;
            case 2:
                this.loadAddress();
                break;
            case 3:
                this.loadIndAddress();
                break;
            case 4:
                this.loadIdxXAddress();
                break;
            case 5:
                this.loadIdxYAddress();
                break;
            case 6:
                this.loadSpX();
                break;
            case 7:
                this.storeAddress();
                break;
            case 8:
                this.getRand();
                break;
            case 9:
                this.putPort();
                break;
            case 10:
                this.addX();
                break;
            case 11:
                this.addY();
                break;
            case 12:
                this.subX();
                break;
            case 13:
                this.subY();
                break;
            case 14:
                this.copyToX();
                break;
            case 15:
                this.copyFromX();
                break;
            case 16:
                this.copyToY();
                break;
            case 17:
                this.copyFromY();
                break;
            case 18:
                this.copyToSP();
                break;
            case 19:
                this.copyFromSP();
                break;
            case 20:
                this.jumpAddress();
                break;
            case 21:
                this.jumpIfEqualAddress();
                break;
            case 22:
                this.jumpIfNotEqualAddress();
                break;
            case 23:
                this.callAddress();
                break;
            case 24:
                this.ret();
                break;
            case 25:
                this.incrementX();
                break;
            case 26:
                this.decrementX();
                break;
            case 27:
                this.push();
                break;
            case 28:
                this.pop();
                break;
            case 29:
                this.interupt(true);
                break;
            case 30:
                this.interuptReturn();
                break;
            case 50:
                this.end();
                break;
            default:
                System.out.println("BAD INSTRUCTION: INSTRUCTION NUMBER " + instructionNumber);
        }
    }

    void closeStreams() {
        this.input.close();
        this.output.close();

        if (this.debugMode) {
            this.debugPrintWriter.close();
        }
    }

    void printDebugInfo() {
        this.debugPrintWriter.println("PC: " + this.PC);
        this.debugPrintWriter.println("SP: " + this.SP);
        this.debugPrintWriter.println("IR: " + this.IR);
        this.debugPrintWriter.println("AC: " + this.AC);
        this.debugPrintWriter.println("X: " + this.X);
        this.debugPrintWriter.println("Y: " + this.Y);
        this.debugPrintWriter.println("KernalMode: " + this.isInKernalMode);
    }

    void printInstruction(int instruction) {
        this.debugPrintWriter.println("------------INSTRUCTION RUN: " + instruction + "------------");
    }

    public static void main(String[] args) {
        try {
            // Running Memory
            Runtime runtime = Runtime.getRuntime();
            Process proccess = runtime.exec("java Memory.java " + args[0]);

            // Setting Up I/O
            InputStream inputStream = proccess.getInputStream();
            OutputStream outputStream = proccess.getOutputStream();

            // CPU Object
            CPU cpu = new CPU(inputStream, outputStream, Integer.parseInt(args[1]), true);

            // Running the program
            cpu.run();

            // Exiting Process
            proccess.waitFor();
            System.out.println("Process exited: " + proccess.exitValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
