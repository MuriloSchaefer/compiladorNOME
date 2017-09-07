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

    public Token(String atributo, String valor) {
        this.atributo = atributo;
        this.valor = valor;
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
        return "(" + atributo + ", " + valor + ")\n";
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
    
}
