/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

/**
 *
 * @author schaefer
 */
public enum Atributo{
    LOCAL(1),TIPO(2),INICIO(3),FIM(4), CODIGO(5), VERDADEIRO(6), FALSO(7), OUTRO(8);

    public int valor;
    Atributo(int valor) {
        this.valor = valor;
    }
    public int getValor(){
        return valor;
    }
}
