/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.exit;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
        String filename = "";
        /*JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "NOME files", "nome");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(chooser);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           filename = chooser.getSelectedFile().getPath();
        } else{
            System.out.println("Nenhum arquivo fonte selecionado");
            exit(1);
        }*/
        filename = "/home/schaefer/Área de trabalho/Unioeste/4 ano/comp/CompiladorNOME/src/compiladornome/testes/teste1.nome";
        String filenameTokens = "/home/schaefer/Área de trabalho/Unioeste/4 ano/comp/CompiladorNOME/src/compiladornome/testes/tokens";
        String csvTable = "/home/schaefer/Área de trabalho/Unioeste/4 ano/comp/CompiladorNOME/src/compiladornome/testes/tabelaLR.csv";
        String csvProd = "/home/schaefer/Área de trabalho/Unioeste/4 ano/comp/CompiladorNOME/src/compiladornome/testes/producoes.csv";
        
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
            //exportarTokens(filenameTokens, tokens);
            AnalisadorSintatico anaSin = new AnalisadorSintatico(csvTable, csvProd);
            //System.out.println("teste: "+ anaSin.cellValue(14, "id", true));
            System.out.println(anaSin.analisar(tokens));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void exportarTokens(String filename, List<Token> tokens){
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true))))   {
            int len = tokens.size();
            for(int i = 0; i<len; i++){
                out.print(tokens.get(i));
                if(i != len -1){
                    out.print(";");
                }
            }
         }catch (IOException e) {
            System.out.println(e);
         }
    }
    
}
