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
    private int l=0, c=0;
    
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

        while(token != null && token.getValor() != null){
            tokens.add(token);
            token = reconhecePalavra();
        }
        if(token != null){
            System.out.println("Erro na leitura do token");
        } 
        return tokens;
    }
    
    private Token reconhecePalavra() throws IOException{
        char current;
        Token res = null;
        bis.mark(2);
        int lido = this.bis.read();
        current = (char) lido;
        this.c++;
        //ignora espaços e quebra de linha entre tokens
        while(current == ' ' || current == '\n' && lido != -1){
            bis.mark(2);
            lido = this.bis.read();
            current = (char) lido;
            if(current == '\n'){ this.l++; this.c=0;}else{this.c++;}
        }
        if(lido == -1){
            return res;
        }
        
        String letra = String.valueOf(current);
        if(letra.matches("#")){
            res = reconheceId();
        }else if(letra.matches("[0-9]")){
            bis.reset();
            res = reconheceNumero();
        }else if(letra.matches("[a-z]")){
            bis.reset();
            res = reconhecePalavraReservada();
        }else if(letra.matches("'")){
            res = reconheceChar();
        }else if(current == '+' || current == '-' || current == '/' || current == '*' ){
            bis.reset();
            res = reconheceOpAritmetico();
        }else if(current == '<' || current == '>' || current == '=' || current == '!' ){
            bis.reset();
            res = reconheceOpRelacional();
        }
        else {
            bis.reset();
            res = reconheceSimbolo();
        }
        return res;
    }
    private Token reconheceSimbolo() throws IOException{
        Token res = new Token("Simb", null);
        char current;
        current = (char) this.bis.read();
        if(this.simbolos.contains(String.valueOf(current))) 
            res.setValor(String.valueOf(current));
        return res;
    }
    
    private Token reconheceOpAritmetico() throws IOException{
        Token res = new Token("Op_arit", null);
        char current;
        current = (char) this.bis.read();
        res.setValor(String.valueOf(current));
        return res;
    }
    
    private Token reconheceOpRelacional() throws IOException{
        Token res = new Token("Op_rel", null);
        char current;
        char x;
        int lido;
        String valor = "";
        bis.mark(2);
        current = (char) this.bis.read();
        switch(current){
            case '>':   valor += "G";
                        bis.mark(2);
                        x = (char) this.bis.read();
                        if(x == '='){
                            valor += "E";
                        }else{
                            bis.reset();
                        } break;
                
            case '<':   valor += "L";
                        bis.mark(2);
                        x = (char) this.bis.read();
                        if(x == '='){
                            valor += "E";
                        }else{
                            bis.reset();
                        } break;
            case '=':   valor += "E";
                        x = (char) this.bis.read();
                        if(!(x == '=')){
                            bis.reset();
                            return reconheceSimbolo();
                        } break;
            case '!':   valor += "D";
                        bis.mark(2);
                        x = (char) this.bis.read();
                        if(!(x == '=')){
                            bis.reset();
                            valor = null;
                        } break;
            default: valor = null;
        }
        res.setValor(valor);
        return res;
    }
    
    private Token reconheceChar() throws IOException {
        Token res = new Token("Char", null);
        
        String valor = "'";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          current = (char) this.bis.read();
          if(!String.valueOf(current).matches("'"))
              valor += current;
          else
              fim=1;
          i++;
        }
        if(valor.length()!=2){
            //erro de tamanho de char
        }else{
            res.setValor(valor+"'");
        }
        return res;
    }
    
    private Token reconhecePalavraReservada() throws IOException{
        Token res = new Token("Pal_res", null);
        
        String valor = "";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          bis.mark(2);
          current = (char) this.bis.read();
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
        }
        bis.reset();
        return res;
    }
    
    private Token reconheceNumero() throws IOException{
        Token res = new Token("Num", null);
        
        String valor = "";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          bis.mark(2);
          current = (char) this.bis.read();
          if(String.valueOf(current).matches("[0-9]"))
              valor += current;
          else
              fim=1;
        }
        bis.reset();
        res.setValor(valor);
        return res;
    }
    
    private Token reconheceId() throws IOException{
        Token res = new Token("Id", null);
        String valor = "#";
        char current;
        int fim = 0;
        int i=0;
        while (this.bis.available() > 0 && fim != 1) {
          bis.mark(2);
          current = (char) this.bis.read();
          if ((current >= 65 && current <= 90) || (current >= 97 && current <= 122)
                  || (i!=0 && current == '_')){
              valor += current; 
          } else{
              fim = 1;
          }
          i++;
        }
        bis.reset();
        if(valor.length()>1)
            res.setValor(valor);
        else{
            //tratar erro de reconhecimento de id
        }
        return res;
    }
    
    
}
