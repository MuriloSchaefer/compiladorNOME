/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author murilo
 */
public class AnalisadorLexico {
    private File file = null;
    
    public AnalisadorLexico(File file) throws FileNotFoundException, IOException{
        this.file = file;
        char current;
        FileInputStream fis = new FileInputStream(file);
        while (fis.available() > 0) {
          current = (char) fis.read();
          System.out.println(current);
        }
    }
}
