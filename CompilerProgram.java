import java.io.*;
import java.util.*;

public class CompilerProgram 
{

    private static final Map<String, String> OPCODES = new HashMap<>();
    private static final Map<String, Integer> SYMBOL_TABLE = new LinkedHashMap<>();
    private static final Set<String> INSTRUCTIONS = new HashSet<>();

    static 
    {
        OPCODES.put("READ", "10");
        OPCODES.put("WRITE", "11");
        OPCODES.put("LOADM", "20");
        OPCODES.put("STORE", "21");
        OPCODES.put("ADDM", "30");
        OPCODES.put("SUBM", "31");
        OPCODES.put("MULM", "32");   
        OPCODES.put("DIVM", "33");
        OPCODES.put("JUMP", "40");
        OPCODES.put("JMP", "40");
        OPCODES.put("HALT", "43");  // Updated to match the table
        
        INSTRUCTIONS.addAll(OPCODES.keySet());
    }

    public static void main(String[] args) 
    {
        if (args.length < 2) 
        {
        System.out.println("Usage: java CompilerProgram <inputFile> <outputFile>");
        return;
        }

        String inputFile = args[0];   // "program.txt"
        String outputFile = args[1];  // "test.sml"

        try 
        {
            compile(inputFile, outputFile);
            System.out.println("Compilation successful! Output written to " + outputFile);
        } 
        catch (Exception e) 
        {
            System.out.println("Compilation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void compile(String inputFile, String outputFile) throws IOException 
    {
        List<String> lines = readLines(inputFile);
        List<String> instructions = new ArrayList<>();
        Set<String> variables = new LinkedHashSet<>();
        String jmpInstruction = null;  // Store the jmp line separately

        // First pass: separate jmp, variables, labels, and instructions
        int instructionAddress = 0;
        for (String line : lines) 
        {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.endsWith(":")) 
            {
                // Label → will be set after variables
                String label = line.substring(0, line.length() - 1).toLowerCase();
                // Don't set yet; will set to 1 + numVars
            } 
            else 
            {
                String firstWord = line.split("\\s+")[0].toUpperCase();
                if (INSTRUCTIONS.contains(firstWord)) 
                {
                    if (firstWord.equals("JMP") || firstWord.equals("JUMP")) 
                    {
                        jmpInstruction = line;  // Store jmp separately
                        instructionAddress++;  // Increment for jmp
                    } 
                    else 
                    {
                        instructions.add(line);
                        instructionAddress++;
                    }
                } 
                else 
                {
                    variables.add(line.toLowerCase());
                }
            }
        }

        // Assign variable addresses starting at 1
        int nextVarAddress = 1;
        for (String var : variables) 
        {
            SYMBOL_TABLE.put(var, nextVarAddress++);
        }

        // Set start label to 1 + numVars (address 4 for 3 vars)
        SYMBOL_TABLE.put("start", 1 + variables.size());

        // Second pass: generate machine code
        List<String> output = new ArrayList<>();

        // Output jmp at 00
        if (jmpInstruction != null) 
        {
            String[] parts = jmpInstruction.split("\\s+");
            String instr = parts[0].toUpperCase();
            String arg = parts[1].toLowerCase();
            String opcode = OPCODES.get(instr);
            String operand = SYMBOL_TABLE.containsKey(arg) ? String.format("%02d", SYMBOL_TABLE.get(arg)) : "00";
            output.add(String.format("00 %s%s", opcode, operand));
        }

        // Output variables at 01-03
        for (String var : variables) 
        {
            int varAddr = SYMBOL_TABLE.get(var);
            output.add(String.format("%02d 0000", varAddr));
        }

        // Output instructions starting at 04
        int addrCounter = 1 + variables.size();  // Start at 4
        for (String line : instructions) 
        {
            String[] parts = line.split("\\s+");
            String instr = parts[0].toUpperCase();
            String arg = (parts.length > 1) ? parts[1].toLowerCase() : "";

            String opcode = OPCODES.getOrDefault(instr, "??");
            String operand = "00";

            if (!arg.isEmpty() && SYMBOL_TABLE.containsKey(arg)) 
            {
                operand = String.format("%02d", SYMBOL_TABLE.get(arg));
            }

            output.add(String.format("%02d %s%s", addrCounter++, opcode, operand));
        }

        // Write output to file
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) 
        {
            for (String line : output) pw.println(line);
        }
    }

    private static List<String> readLines(String filename) throws IOException 
    {
        List<String> lines = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNextLine()) lines.add(sc.nextLine());
        }
        return lines;
    }
}