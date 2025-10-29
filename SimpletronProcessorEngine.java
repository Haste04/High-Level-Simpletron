//Simpletron Processor Engine
import java.io.File;
import java.util.Scanner;

public class SimpletronProcessorEngine{
    private int pc = 0;           // Program counter
    private int accumulator = 0;  // Accumulator
    private String ir;            // Instruction Register
    private String opcode;        // Current operation code
    private String operand;       // Current operand
    private Memory memory;        // Memory object

    public SimpletronProcessorEngine(String filename) 
    {
        memory = new Memory(100);
        loadProgram(filename);
    }

    // Load program from file (e.g., test.sml)
    private void loadProgram(String filename) 
    {
    try (Scanner scanner = new Scanner(new File(filename))) 
    {
        while (scanner.hasNextLine()) 
        {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            // Skip non-data lines (like headers or labels)
            if (!Character.isDigit(line.charAt(0))) continue; 

            String[] parts = line.split("\\s+");
            if (parts.length < 2) continue; 

            try 
            {
                int address = Integer.parseInt(parts[0]);
                String codeStr = parts[1].replace("+", "").trim();
                int code = Integer.parseInt(codeStr);
                memory.addItem(address, String.format("%04d", code));
            } 
            catch (NumberFormatException e) 
            {
                System.out.println("Skipping malformed line: " + line);
            }
        }
    } 
    catch (Exception e) 
    {
        System.out.println("Error loading program: " + e.getMessage());
    }
}

    // Run the loaded Simpletron program
    public void run() 
    {
        while (pc < 100) 
        {
            ir = memory.getItem(pc);
            if (ir == null) break;
            ir = ir.replace("+", "").trim();
            while (ir.length() < 4) ir = "0" + ir;

            opcode = ir.substring(0, 2);
            operand = ir.substring(2);
 
            boolean continueExec = execute(opcode, operand);
            if (!continueExec) break;

            // increment PC only if not a jump instruction
            if (!opcode.equals("40")) 
            {
                pc++;
            }
        }

        System.out.println("\n-- PROGRAM HALTED --");
        dumpRegisters();
        memory.dump();
    }

    // Execute instruction
    private boolean execute(String opcode, String operand) 
    {
        int addr = Integer.parseInt(operand);
        String data;
        switch (opcode) 
        {
            case "10": // READ
                String varName = (addr == 1) ? "a" : (addr == 2) ? "b" : "value";
                System.out.print("Enter value for " + varName + ": ");
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
                return false; // stop program

            default:
                System.out.println("Unknown opcode: " + opcode);
        }
        return true;
    }

    // Display register contents
    private void dumpRegisters() 
    {
        System.out.println("REGISTERS:");
        System.out.println("Accumulator: " + accumulator);
        System.out.println("Program Counter: " + pc);
        System.out.println("Instruction Register: " + ir);
        System.out.println("Operation Code: " + opcode);
        System.out.println("Operand: " + operand);
    }

    // Main method
    public static void main(String[] args) 
    {
        if (args.length < 1) 
        {
        System.out.println("Usage: java SimpletronProcessorEngine <smlFile>");
        return;
        }

        String filename = args[0]; // e.g., "test.sml"
        SimpletronProcessorEngine s = new SimpletronProcessorEngine(filename);
        s.run();
    }
}
