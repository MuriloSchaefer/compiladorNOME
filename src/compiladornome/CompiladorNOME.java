/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author murilo
 */
public class CompiladorNOME {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        String filename = "/home/murilo/NetBeansProjects/CompiladorNOME/src/compiladornome/testes/teste1.txt";
        File file = new File(filename);
        if (!file.exists()) {
          System.out.println(filename + " does not exist.");
          return;
        }
        if (!(file.isFile() && file.canRead())) {
          System.out.println(file.getName() + " cannot be read from.");
          return;
        }
        try {
            List<String> pal_reservadas = Arrays.asList("int", "bool", "char", 
                  "and", "or", "not", "xor", "for", "while", "if", "else", "switch",
                  "case", "printf", "read", "true", "false");
            List<String> simbolos = Arrays.asList("{", "}", ")", "(", ";", "=", ":");
            AnalisadorLexico anaLex = new AnalisadorLexico(file, pal_reservadas, simbolos);
            List<Token> tokens = anaLex.analisar();
            
            System.out.println("tokens: "+tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
