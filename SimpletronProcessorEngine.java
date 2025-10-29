import java.io.*;
import java.util.*;

public class SimpletronProcessorEngine 
{

    private int pc = 0;           // Program Counter
    private int accumulator = 0;  // Accumulator
    private String ir;            // Instruction Register
    private String opcode;        // Current operation code
    private String operand;       // Current operand
    private Memory memory;        // Memory object (from your separate file)

    public SimpletronProcessorEngine() 
    {
        memory = new Memory(100);
    }

    public void runProgram(String inputFile) throws IOException 
    {
        String tempOutputFile = "temp.sml";  
        CompilerProgram.compile(inputFile, tempOutputFile);  
        List<String> machineCode = readLines(tempOutputFile); 
        loadMachineCode(machineCode);  
        run(); 

        new File(tempOutputFile).delete();
    }


    private void loadMachineCode(List<String> lines) 
    {
        int addr = 0;
        for (String line : lines) 
        {
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) 
            {
                String instr = parts[1];  // Extract the instruction (e.g., "4004" from "00 4004")
                memory.addItem(addr++, instr);
            }
        }
    }

    // Run the loaded program
    public void run() 
    {
        while (pc < 100) {
            ir = memory.getItem(pc);
            if (ir == null) break;
            ir = ir.replaceAll("[^0-9]", "");  // Remove ALL non-digit characters (spaces, +, etc.)
            while (ir.length() < 4) ir = "0" + ir;
            opcode = ir.substring(0, 2);
            operand = ir.substring(2);
            boolean continueExec = execute(opcode, operand);
            if (!continueExec) break;
            if (!opcode.equals("40")) pc++; // increment PC unless JUMP
        }
        System.out.println("\n-- PROGRAM HALTED --");
        dumpRegisters();
        memory.dump();
    }

    private boolean execute(String opcode, String operand) 
    {
        int addr = Integer.parseInt(operand);
        String data;
        switch (opcode) 
        {
            case "10": // READ
                System.out.print("Enter value for variable: ");
                data = new Scanner(System.in).nextLine();
                while (data.length() < 4) data = "0" + data;
                memory.addItem(addr, data);
                break;

            case "11": // WRITE
                data = memory.getItem(addr);
                System.out.println(">" + Integer.parseInt(data));
                break;

            case "20": // LOADM
                accumulator = Integer.parseInt(memory.getItem(addr));
                break;

            case "21": // STORE
                memory.addItem(addr, String.format("%04d", accumulator));
                break;

            case "30": // ADDM
                accumulator += Integer.parseInt(memory.getItem(addr));
                break;

            case "31": // SUBM
                accumulator -= Integer.parseInt(memory.getItem(addr));
                break;

            case "40": // JUMP
                pc = addr;
                break;

            case "43": // HALT
                return false;

            default:
                System.out.println("Unknown opcode: " + opcode);
        }
        return true;
    }

    private void dumpRegisters() 
    {
        System.out.println("REGISTERS:");
        System.out.println("Accumulator: " + accumulator);
        System.out.println("Program Counter: " + pc);
        System.out.println("Instruction Register: " + ir);
        System.out.println("Operation Code: " + opcode);
        System.out.println("Operand: " + operand);
    }

    private List<String> readLines(String filename) throws IOException 
    {
        List<String> lines = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename))) 
        {
            while (sc.hasNextLine()) lines.add(sc.nextLine());
        }
        return lines;
    }

    public static void main(String[] args) throws IOException 
    {
        if (args.length == 0) 
        {
            System.out.println("Usage: java SimpletronProcessorEngine <inputFile>");
            return;
        }
        String inputFile = args[0];
        SimpletronProcessorEngine engine = new SimpletronProcessorEngine();
        engine.runProgram(inputFile);
    }
}