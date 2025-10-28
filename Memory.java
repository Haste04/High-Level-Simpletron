/* Haggai P. Estavilla BSCSA - 3A */
import java.io.*;
import java.util.Scanner;
public class Memory 
{
    private int size = 100; 
    private String[] mem;    

    public Memory(int size) 
    {
        this.size = size;
        mem = new String[size];

        for (int i = 0; i < size; i++) 
        {
            mem[i] = "+0000";
        }
    }
    public Memory(String[] data) 
    {
        int datasize = data.length;
        mem = new String[datasize];

        for (int i = 0; i < datasize; i++) 
            mem[i] = data[i];
        
        for (int i = datasize; i < size; i++) 
            mem[i] = "+0000";
        
    }

    public void addItem(int address, String data) 
    {
        if (address >= 0 && address < mem.length) 
            mem[address] = "+" + data;
        
        else 
            System.out.println("Address out of bounds");
        
    }
    
    public String getItem(int address)
    {
        if (address >= 0 && address < mem.length) 
            return mem[address];
        else 
            return null;
    }

    public void dump() 
    {
        System.out.println("MEMORY:\n");


        System.out.print("        ");
        for (int col = 0; col < 10; col++) 
            System.out.printf("%10d", col);
        
        System.out.println();

        for (int i = 0; i < mem.length; i++) 
        {
   
            if (i % 10 == 0) 
            {
                System.out.printf("%02d      ", i);
            }

            System.out.printf("%10s", mem[i]);

      
            if ((i + 1) % 10 == 0) 
                System.out.println();
           
        }
        System.out.println(); 
    }


    public static void main(String[] args) 
    {
        try
        {
           File file = new File("test.sml");
           Scanner reader = new Scanner(file);
           StringBuilder sb = new StringBuilder();
           Memory m = new Memory(100);  

           while (reader.hasNextLine()) 
{
    String line = reader.nextLine().trim();
    
    if (line.isEmpty() || line.startsWith(";")) continue;
    
    int commentIndex = line.indexOf(';');
    if (commentIndex != -1) 
        line = line.substring(0, commentIndex).trim();
    
    if (!line.isEmpty() && Character.isDigit(line.charAt(0))) 
    {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) 
        {
            int address = Integer.parseInt(parts[0]);
            String code = parts[1];
            m.addItem(address, code);
        }
    }
}
           reader.close();
           m.dump();
        }
        catch(FileNotFoundException e)
        {
           System.out.println("An error has occured");
           e.printStackTrace();
        }
    }
}