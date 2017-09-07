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
    private FileInputStream fis = null;
    
    public AnalisadorLexico(File file) throws FileNotFoundException, IOException{
        this.file = file;        
        this.fis = new FileInputStream(file);
    }
    
    public Token reconhecePalavra() throws IOException{
        char current;
        Token res = null;
        while (this.fis.available() > 0) {
          current = (char) this.fis.read();
          switch(current){
              case '#': res = reconheceId(); break; 
          }
        }
        return res;
    }
    
    public Token reconheceId() throws IOException{
        Token res = new Token("Id", null);
        String valor = "#";
        char current;
        int fim = 0;
        while (this.fis.available() > 0 && fim != 1) {
          current = (char) this.fis.read();
          switch(current){
              case ' ': fim = 1; break;
              default: valor += current; break;
          }
        }
        //voltar um caracter
        res.setValor(valor);
        return res;
    }
}
