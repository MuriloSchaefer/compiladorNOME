/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author murilo
 */
public class AnalisadorLexico {
    private File file = null;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
    private List<String> pal_res = new ArrayList<>();
    private List<String> simbolos = new ArrayList<>();
    private int l=1, c=0;
    
    public AnalisadorLexico(File file) throws FileNotFoundException, IOException{
        this.file = file;        
        this.fis = new FileInputStream(file);
        this.bis = new BufferedInputStream(this.fis);
    }
    public AnalisadorLexico(File file, List<String> pal_res, List<String> simbolos) throws FileNotFoundException, IOException{
        this.file = file;        
        this.fis = new FileInputStream(file);
        this.bis = new BufferedInputStream(this.fis);
        this.pal_res = pal_res;
        this.simbolos = simbolos;
    }
    
    public List<Token> analisar() throws IOException{
        List<Token> tokens = new ArrayList<>();
        Token token = reconhecePalavra();

        while(token != null){
            tokens.add(token);
            token = reconhecePalavra();
        }
        if(token != null){
            System.out.println("Erro na leitura do token");
        } 
        return tokens;
    }
    
    private char lerChar() throws IOException{
        bis.mark(2);
        int lido = this.bis.read();
        char current = (char) lido;
        this.c++;
        
        if(lido == -1){
           return '\n';
        }
        while(current=='\n'){
            this.c = 0;
            this.l++;
            
            bis.mark(2);
            lido = this.bis.read();
            current = (char) lido;
        }
        return current;        
    }
    
    private Token reconhecePalavra() throws IOException{
        Token res = null;
        
        char current = lerChar();
        
        //ignora espaços e quebra de linha entre tokens
        while(current == ' ' && current != '\n'){
            current = lerChar();
        }
        if(current == '\n'){
            return res;
        }
        
        String letra = String.valueOf(current);
        if(letra.matches("#")){
            res = reconheceId();
        }else if(letra.matches("[0-9]")){
            bis.reset();
            this.c--;
            res = reconheceNumero();
        }else if(letra.matches("[a-z]")){
            bis.reset();
            this.c--;
            res = reconhecePalavraReservada();
        }else if(letra.matches("'")){
            res = reconheceChar();
        }else if(current == '+' || current == '-' || current == '/' || current == '*' ){
            bis.reset();
            this.c--;
            res = reconheceOpAritmetico();
        }else if(current == '<' || current == '>' || current == '=' || current == '!' ){
            bis.reset();
            this.c--;
            res = reconheceOpRelacional();
        }
        else {
            bis.reset();
            this.c--;
            res = reconheceSimbolo();
        }
        return res;
    }
    private Token reconheceSimbolo() throws IOException{
        Token res = new Token("Simb", null, this.c, this.l);
        char current = lerChar();
        res.setValor(String.valueOf(current));
        if(!this.simbolos.contains(String.valueOf(current))) 
            res.setErro("Simbolo não pertencente ao alfabeto");
        return res;
    }
    
    private Token reconheceOpAritmetico() throws IOException{
        Token res = new Token("Op_arit", null, this.c, this.l);
        char current = lerChar();
        res.setValor(String.valueOf(current));
        return res;
    }
    
    private Token reconheceOpRelacional() throws IOException{
        Token res = new Token("Op_rel", null, this.c, this.l);
        char x;
        int lido;
        String valor = "";
        char current = lerChar();
        switch(current){
            case '>':   valor += "G";
                        x = lerChar();
                        if(x == '='){
                            valor += "E";
                        }else{
                            bis.reset();
                            this.c--;
                        } break;
                
            case '<':   valor += "L";
                        x = lerChar();
                        if(x == '='){
                            valor += "E";
                        }else{
                            bis.reset();
                            this.c--;
                        } break;
            case '=':   valor += "E";
                        x = (char) this.bis.read();
                        if(!(x == '=')){
                            bis.reset();
                            this.c-=2;
                            return reconheceSimbolo();
                        } break;
            case '!':   valor += "D";
                        x = lerChar();
                        if(!(x == '=')){
                            bis.reset();
                            this.c--;
                            valor = null;
                        } break;
            default: valor = null;
        }
        res.setValor(valor);
        return res;
    }
    
    private Token reconheceChar() throws IOException {
        Token res = new Token("Char", null, this.c, this.l);
        
        String valor = "'";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          current = lerChar();
          if(!String.valueOf(current).matches("'"))
              valor += current;
          else
              fim=1;
          i++;
        }
        if(this.bis.available() == 0){
            res.setErro("Não foi encontrado o fechamento do char");
        }
        if(valor.length()!=2){
            //erro de tamanho de char
            res.setErro("Char deve conter apenas um caracter");
        }else{
            res.setValor(valor+"'");
        }
        return res;
    }
    
    private Token reconhecePalavraReservada() throws IOException{
        Token res = new Token("Pal_res", null, this.c, this.l);
        
        String valor = "";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          current = lerChar();
          if(String.valueOf(current).matches("[a-z]"))
              valor += current;
          else
              fim=1;
        }
        //Validar se esta entre o conjunto de pal_res
        if(this.pal_res.contains(valor)){
            res.setValor(valor);
        }else{
            //erro palavra_reservada não encontrada
            res.setErro("Palavra reservada não encontrada");
        }
        bis.reset();
        this.c--;
        return res;
    }
    
    private Token reconheceNumero() throws IOException{
        Token res = new Token("Num", null, this.c, this.l);
        
        String valor = "";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          current = lerChar();
          if(String.valueOf(current).matches("[0-9]"))
              valor += current;
          else
              fim=1;
        }
        bis.reset();
        this.c--;
        res.setValor(valor);
        return res;
    }
    
    private Token reconheceId() throws IOException{
        Token res = new Token("Id", null, this.c, this.l);
        String valor = "#";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          current = lerChar();
          if ((current >= 65 && current <= 90) || (current >= 97 && current <= 122)
                  || (i!=0 && current == '_')){
              valor += current; 
          } else{
              fim = 1;
          }
          i++;
        }
        bis.reset();
        this.c--;
        if(valor.length()>1)
            res.setValor(valor);
        else{
            //tratar erro de reconhecimento de id
            res.setErro("O identificador é nulo");
        }
        return res;
    }
    
    
}
