import java.io.*;
import java.util.*;

public class FileHandler 
{
    public static List<String> readFile(String filename) throws IOException 
    {
        List<String> lines = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename))) 
        {
            while (sc.hasNextLine()) 
            {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith(";")) continue;

                // Remove inline comments
                int commentIndex = line.indexOf(';');
                if (commentIndex != -1)
                    line = line.substring(0, commentIndex).trim();

                if (!line.isEmpty())
                    lines.add(line);
            }
        }
        return lines;
    }

    public static void writeFile(String filename, List<String> lines) throws IOException 
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) 
        {
            for (String line : lines)
                pw.println(line);
        }
        System.out.println("File written successfully to " + filename);
    }

    public static void appendFile(String filename, String content) throws IOException 
    {
        try (FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) 
            {
            out.println(content);
        }
    }

    public static boolean exists(String filename) 
    {
        File file = new File(filename);
        return file.exists();
    }

    public static boolean delete(String filename) 
    {
        File file = new File(filename);
        return file.delete();
    }
}