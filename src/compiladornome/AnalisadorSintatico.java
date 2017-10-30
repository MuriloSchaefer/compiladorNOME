/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author schaefer
 */
public class AnalisadorSintatico {
    String[] cabecalhoTabela = null;
    List<String[]> tabela = null;
    String[] cabecalhoProducoes = null;
    List<String[]> producoes = null;
    Stack<String> pilha = null;
    
    public AnalisadorSintatico(String csvTable, String csvProd) {
        this.tabela = readCSV(csvTable, true);
        this.producoes = readCSV(csvProd, false);
        this.pilha = new Stack<>();
        pushInt(0);
    }
    
    public boolean analisar(List<Token> tokens){
        int i = 0;
        while(i < tokens.size()){
            int s = popInt();
            pushInt(s);
            String a = tokens.get(i).getAtributo();
                System.out.println("S: "+ s + "| a: "+ a);
            String celula = cellValue(s, a, true);
            String[] parser = parserCell(celula);
            if(parser == null){
                return false;
            }
            if (parser[0].equals("E")){
                this.pilha.push(a);
                this.pilha.push(parser[1]);
                i++;
            } else if(parser[0].equals("R")){
                String cell = cellValue(Integer.valueOf(parser[1])-1, "len", false);
                String prod = cellValue(Integer.valueOf(parser[1])-1, "A", false);
                int tamanho = 2 * Integer.valueOf(cell);
                for(int j=0; j<tamanho; j++){
                    pilha.pop();
                }
                int s1 = popInt();
                pushInt(s1);
                this.pilha.push(prod);
                System.out.println("S1: "+ s1 + "| prod: "+ prod);
                String desvio = cellValue(s1, prod, true);
                this.pilha.push(desvio);
            } else if(parser[0].equals('a')) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public String[] parserCell(String celula){
        if(celula.equals("")){
            return null;
        }
        String[] res = new String[2];
        res[0] = celula.substring(0, 1);
        res[1] = celula.substring(1);
        return res;
    }
    
    public int popInt(){
        return Integer.valueOf(pilha.pop());
    }
    
    public void pushInt(int value){
        pilha.push(String.valueOf(value));
    }
    
    public int getIndexTable(String chave, boolean isTable){
        int len = this.cabecalhoTabela.length;
        String[] tabela = null;
        if(isTable){
            tabela = this.cabecalhoTabela;
        } else {
            tabela = this.cabecalhoProducoes;
        }
        for(int i=0; i<len; i++){
            if(tabela[i].equals(chave)){
                return i;
            }
        }
        return -1;
    }
    
    public String cellValue(int state, int index, boolean isTable){
        if(index < 0 || state < 0)
            return null;
        if(isTable){
            return this.tabela.get(state)[index];
        } else {
            return this.producoes.get(state)[index];
        }
    }
    
    public String cellValue(int state, String name, boolean isTable){
        int index = getIndexTable(name, isTable);
        if(index < 0 || state < 0)
            return null;
        if(isTable){
            return this.tabela.get(state)[index];
        } else {
            return this.producoes.get(state)[index];
        }
    }
    
    public List<String[]> readCSV(String filename, boolean isTable){
        String arquivoCSV = filename;
        BufferedReader br = null;
        String linha = "";
        String csvDivisor = ",";
        List<String[]> Tabela = null;
        try {

            br = new BufferedReader(new FileReader(arquivoCSV));
            Tabela = new ArrayList<>();
            
            //GAMBIARRA LIXO PQ JAVA É LIXO
            if(isTable){
                this.cabecalhoTabela = br.readLine().split(csvDivisor);
            } else {
                this.cabecalhoProducoes = br.readLine().split(csvDivisor);
            }
            while ((linha = br.readLine()) != null) {

                String[] line = linha.split(csvDivisor);
                Tabela.add(line);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Tabela;
    }
}
