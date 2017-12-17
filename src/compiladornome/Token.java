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
    
    // ATRIBUTOS PARA ANALISE SEMANTICA
    
    private String local = "";
    private String codigo = "";
    private String verdadeiro = "";
    private String falso = "";
    private String tipo = "";
    private int indice = 0;
    private String inicio = "";
    private String fim = "";

    public Token(String atributo, String valor, int c, int l) {
        this.atributo = atributo;
        this.valor = valor;
        this.c = c;
        this.l = l;
    }
    
    public Token(String atributo, String valor) {
        this.atributo = atributo;
        this.valor = valor;
        this.c = 0;
        this.l = 0;
    }

    
    public void concatenarCodigo(String codigo){
        this.codigo.concat(codigo);
    }
    
    public void concatenarCodigo(String codigo, String codigo2){
        this.codigo.concat(" ");
        this.codigo.concat(codigo);
        this.codigo.concat(" ");
        this.codigo.concat(codigo2);
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
            return "[" + atributo + ", " + valor + "]";
        } else {
           if(this.valor!=null){
            return "[" + atributo + ", " + this.erro + ", Char: '" + this.valor +"', linha: "+ this.l + ", coluna: "+ this.c +"]"; 
           }
           return "[" + atributo + ", " + this.erro + ", linha: "+ this.l + ", coluna: "+ this.c +"]"; 
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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getVerdadeiro() {
        return verdadeiro;
    }

    public void setVerdadeiro(String verdadeiro) {
        this.verdadeiro = verdadeiro;
    }

    public String getFalso() {
        return falso;
    }

    public void setFalso(String falso) {
        this.falso = falso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFim() {
        return fim;
    }

    public void setFim(String fim) {
        this.fim = fim;
    }
    
    
}
