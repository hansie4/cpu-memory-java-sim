import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Memory {

    //////////////////////////// MEMORY ARRAY ////////////////////////////
    int[] mainMemory = new int[2000];

    //////////////////////////// INPUT VARIABLE ////////////////////////////
    Scanner input;

    //////////////////////////// DEBUG ////////////////////////////
    boolean debugMode;
    File memoryDebugFile;
    FileWriter memoryDebugFileWriter;
    PrintWriter memoryDebugPrintWriter;

    //////////////////////////// CONSTRUCTOR ////////////////////////////
    Memory(String fileToLoadFrom, boolean debugMode) {
        this.loadMemory(fileToLoadFrom);

        this.input = new Scanner(System.in);

        if (debugMode) {
            this.debugMode = true;

            try {
                memoryDebugFile = new File("memDebug.txt");
                memoryDebugFileWriter = new FileWriter(this.memoryDebugFile);
                memoryDebugPrintWriter = new PrintWriter(this.memoryDebugFileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.debugMode = false;
        }
    }

    //////////////////////////// MAIN FUNCTION ////////////////////////////
    void runMemory() {
        if (this.debugMode) {
            this.debugMemory();
        }

        while (input.hasNext()) {
            String instruction = input.next();

            if (instruction.equals("read")) {
                this.read(input.nextInt());
            } else if (instruction.equals("write")) {
                int data = input.nextInt();
                int address = input.nextInt();
                this.write(data, address);

                if (this.debugMode) {
                    this.debugMemory();
                }
            }
        }

        this.close();
    }

    ///////////////////// INITIALIZE MEMORY FUNCTION ///////////////////////
    void loadMemory(String inputFileName) {
        try {
            File inputFile = new File(inputFileName);
            Scanner inputFileScanner = new Scanner(inputFile);

            int address = 0;

            while (inputFileScanner.hasNext()) {
                if (inputFileScanner.hasNextInt()) {
                    // Reads the instruction number into memory at the current address
                    this.mainMemory[address] = inputFileScanner.nextInt();
                    address++;
                    if (inputFileScanner.hasNext()) {
                        inputFileScanner.nextLine();
                    }
                } else {
                    // Changes the address to write to to the specified address
                    String newAddress = inputFileScanner.next();
                    if (newAddress.charAt(0) == '.') {
                        address = Integer.parseInt(newAddress.substring(1));
                    }

                    inputFileScanner.nextLine();
                }
            }

            inputFileScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////// READING AND WRITING FUNCTIONS ///////////////////
    void read(int address) {
        System.out.println(mainMemory[address]);
    }

    void write(int data, int address) {
        mainMemory[address] = data;
    }

    //////////////////////////// DEBUG MEMORY ////////////////////////////
    void debugMemory() {
        for (int i = 0; i < 100; i++) {
            this.memoryDebugPrintWriter.printf("||%-2d|", i);
            for (int j = 0; j < 20; j++) {
                this.memoryDebugPrintWriter.printf("|%3d", this.mainMemory[(i * 20) + j]);
            }
            this.memoryDebugPrintWriter.print("|\n");
        }
        this.memoryDebugPrintWriter
                .print("--------------------------------------------------------------------------------------\n");
    }

    //////////////////////////// CLOSING FUNCTIONS ////////////////////////////
    void close() {
        input.close();

        if (this.debugMode) {
            memoryDebugPrintWriter.close();
        }
    }

    public static void main(String[] args) {
        Memory mem = new Memory(args[0], false);

        mem.runMemory();

    }
}