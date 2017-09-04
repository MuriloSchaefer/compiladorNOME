/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

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
          AnalisadorLexico anaLex = new AnalisadorLexico(file);
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    
}
