/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

/**
 *
 * @author murilo
 */
public class Token {
    private String atributo;
    private String valor;
    private String erro = "";
    private int l, c;

    public Token(String atributo, String valor, int c, int l) {
        this.atributo = atributo;
        this.valor = valor;
        this.c = c;
        this.l = l;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        String res = "";
        if(this.erro == ""){
            return "(" + atributo + ", " + valor + ")\n";
        } else {
           if(this.valor!=null){
            return "(" + atributo + ", " + this.erro + ", Char: '" + this.valor +"', linha: "+ this.l + ", coluna: "+ this.c +")\n"; 
           }
           return "(" + atributo + ", " + this.erro + ", linha: "+ this.l + ", coluna: "+ this.c +")\n"; 
        }
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }
    
    
}
