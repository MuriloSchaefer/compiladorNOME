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

//logging
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import compiladornome.MyLogger;
import java.util.Arrays;
import java.util.Hashtable;

/**
 *
 * @author schaefer
 */
public class AnalisadorSintatico {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    String[] cabecalhoTabela = null;
    List<String[]> tabela = null;
    String[] cabecalhoProducoes = null;
    List<String[]> producoes = null;
    //Stack<String> pilha = null;
    Stack<Token> pilha = null;
    
    List<Integer> lambda_inst = null;
    List<Integer> lambda_if = null;
    
    String codigoIntermediario = "";
    
    Hashtable<String, String> tabela_simbolos = new Hashtable<>();
        
    public AnalisadorSintatico(String csvTable, String csvProd) throws IOException {
        MyLogger.setup();
        LOGGER.setLevel(Level.INFO);
        this.tabela = readCSV(csvTable, true);
        LOGGER.finest("Tabela LR lida com sucesso");
        this.producoes = readCSV(csvProd, false);
        LOGGER.finest("Produções lidas com sucesso");
        this.pilha = new Stack<>();
        LOGGER.finest("Pilha foi inicializada");
        pushInt(0);
        LOGGER.finest("Adicionado o estado inicial na pilha"); 
        this.lambda_inst = Arrays.asList(3,7,58, 59, 54,39,4,6,27,10,111,55,53, 141, 5, 112, 114, 125,142,123,107, 88, 9,106);
        this.lambda_if = Arrays.asList(106);
    }
    
    public String analisar(List<Token> tokens){
        
        int i = 0;
        LOGGER.info("Inicializando Analise");
        while(i < tokens.size()){
            
            LOGGER.info("<b>Verificando token "+ i+"</b>");
            LOGGER.info("Pilha: "+ prodLog(this.pilha.toString()));
            int s = popInt();
            pushInt(s);
            
            LOGGER.info("Topo da pilha: "+ s);
            String a = tokens.get(i).getAtributo();
            
            //valida vazios ----------------------
            if(s == 43){
                switch(a){
                    case("+")   : break;
                    case("-")   : break;
                    case("/")   : break;
                    case("*")   : break;
                    case("<")   : break;
                    case("<=")  : break;
                    case(">")   : break;
                    case(">=")  : break;
                    case("==")  : break;
                    case("!=")  : break;
                    default: a = "lambda"; break;
                }
            }            
            if(s == 11){
                switch(a){
                    case("}"): break;
                    case("break"): break;
                    default: a = "$"; break;
                }
            }    
            
            if(this.lambda_if.contains(s)){
                switch(a){
                    case("else"): break;
                    default: a="lambda"; break;
                }
            }
            
            if(this.lambda_inst.contains(s)){
                switch(a){
                    case("if"): break;
                    case("while"): break;
                    case("for"): break;
                    case("switch"): break;
                    case("int"): break;
                    case("char"): break;
                    case("bool"): break;
                    case("id"): break;
                    case("read"): break;
                    case("printf"): break;
                    case(")"): break;
                    default: a = "lambda"; break;
                }
            }
            //------------------------------------
            
            LOGGER.info("Token lido: "+ a);
            String celula = null;
            String[] parser = null;
            String saida = null;
            try{
                celula = cellValue(s, a, true);
                LOGGER.info("Valor na celula ["+ s + ", "+ a+"]: "+ celula);
                parser = parserCell(celula);
            } catch(ArrayIndexOutOfBoundsException e){
                
            }
            if(parser == null){
                LOGGER.warning("Parser nulo, chegamos em um estado de erro.");
                
                List<String> expectedValues = this.findExpected(s, true);
                if(a.equals("$")){
                    saida = "Chegamos ao final do seu arquivo e não encontramos um dos valores a seguir: " + expectedValues.toString();
                }else {
                    saida = "Erro na linha " + tokens.get(i).getL() + " e coluna " + (tokens.get(i).getC()+1);
                    saida += " valor lido: " + a + " quando se esperava um dos valores: ";
                    saida += expectedValues.toString();
                }
                
                //buscar valores esperados.
                return saida;
            }
            if (parser[0].equals("E")){
                LOGGER.info("Empilhando "+ a + " e " + parser[1]);
                this.pilha.push(tokens.get(i));
                
                this.pilha.push(new Token("", parser[1]));
                LOGGER.finest(a + " e " + parser[1] + " empilhado com sucesso");
                if((s != 43 && !this.lambda_inst.contains(s) && !this.lambda_if.contains(s)) || (!a.equals("lambda") && !a.equals("$"))){
                    i++;
                }
            } else if(parser[0].equals("R")){
                /*REALIZA AS AÇÕES SEMANTICAS ----------------------------------------
                    No for abaixo esto realizando modificações para tentar tratar os valores semanticos;
                 ------------------------------------------------------------------- */      
                
                LOGGER.info("Reduzindo pela produção " + parser[1]);
                String cell = cellValue(Integer.valueOf(parser[1]), "len", false);
                LOGGER.finest("tamanho da produção lida: " + cell);
                String prod = cellValue(Integer.valueOf(parser[1]), "A", false);
                LOGGER.info("Tamanho da produção "+ prodLog(prod) + " "+ parser[1] + ": " + cell);
                int tamanho = 2 * Integer.valueOf(cell);
                LOGGER.info("quantidade de elementos a serem desempilhados: "+ tamanho);
                Stack<Token> pilha_aux = new Stack<>();
                Token p;
                for(int j=0; j<tamanho; j++){
                    p = pilha.pop();  
                    if(j%2==1)
                       pilha_aux.push(p);
                    LOGGER.finest("desempilhando: "+ p);
                }
                LOGGER.finest("tamanho pilha auxiliar: "+ pilha_aux.size());
                
                LOGGER.info("pilha aux: "+ prodLog(pilha_aux.toString()));
                /* ANALISE SEMANTICA */
                Token t_prod = new Token("nao_terminal", prod);
                
                //--- listinha -----
                Token t_expr,t_char, t_tipo, t_id, t_decl2, t_decl, t_inst, t_aux, t_termo, t_binaria,t_op;
                
                switch(Integer.valueOf(parser[1])){
                    case 0:
                        t_inst = pilha_aux.pop();
                        return "Codigo intermediario gerado com sucesso: ESTOU IMPRESSIONANTE\n"+t_inst.getCodigo();
                    case 5: 
                        t_decl = pilha_aux.pop();
                        t_inst = pilha_aux.pop();
                        t_prod.setCodigo(t_decl.getCodigo()+"\n"+t_inst.getCodigo());
                        break;
                    case 9:break;
                    case 10:
                        t_tipo = pilha_aux.pop();
                        t_id = pilha_aux.pop();
                        Token token_decl2 = pilha_aux.pop();
                        
                        if(token_decl2.getTipo().equals("")){
                            if(tabela_simbolos.contains(t_id.getValor())){
                                LOGGER.severe(t_id.getValor() + " já existe na tabela de simbolos");
                                return t_id.getValor() + " já existe na tabela de simbolos";
                            }else {
                                t_prod.setTipo(t_tipo.getTipo());
                                tabela_simbolos.put(t_id.getValor(), t_tipo.getTipo());
                                LOGGER.info(t_id.getValor() + " adicionado na tabela de simbolos");
                            }
                            
                        }else{
                            if(!token_decl2.getTipo().equals(t_tipo.getTipo())){
                                LOGGER.severe("foi tentado atribuir um valor "+ token_decl2.getTipo()+" para uma variavel do tipo "+ t_tipo.getTipo());
                                return "foi tentado atribuir um valor "+ token_decl2.getTipo()+" para uma variavel do tipo "+ t_tipo.getTipo();
                            }else{
                                t_prod.setTipo(t_tipo.getTipo());
                                tabela_simbolos.put(t_id.getValor(), t_tipo.getTipo());
                                LOGGER.info(t_id.getValor() + " adicionado na tabela de simbolos");
                            }
                            t_prod.setCodigo(t_id.getValor()+token_decl2.getCodigo());
                            
                        }
                        break;
                    case 11: 
                        t_decl2 = pilha_aux.pop();
                        t_prod.setCodigo(t_decl2.getCodigo());
                        t_prod.setTipo(t_decl2.getTipo());
                        break;
                    case 12: break;
                    case 13: 
                        t_expr = pilha_aux.pop();
                        t_expr = pilha_aux.pop();
                        t_prod.setCodigo(" := " +t_expr.getCodigo());
                        t_prod.setTipo(t_expr.getTipo());
                        break;
                    case 14: 
                        t_char = pilha_aux.pop();
                        t_prod.setTipo("char");
                        t_prod.setCodigo(t_char.getValor());
                        break;
                    case 15: 
                        t_expr = pilha_aux.pop();
                        t_prod.setTipo(t_expr.getTipo());
                        t_prod.setCodigo(t_expr.getCodigo());
                        break;
                    case 16: 
                        Token aux = pilha_aux.pop();
                        t_prod.setCodigo(aux.getValor());
                        t_prod.setTipo(aux.getValor()); // onde esta o tipo
                        break;
                    case 17:
                        t_tipo = pilha_aux.pop();
                        t_prod.setTipo("char");
                        t_prod.setCodigo(t_tipo.getValor());
                        break;
                    case 18:
                        t_tipo = pilha_aux.pop();
                        t_prod.setTipo("bool");
                        t_prod.setCodigo(t_tipo.getValor());
                        break;
                    case 19: 
                        t_termo = pilha_aux.pop();
                        t_prod.setCodigo(t_termo.getCodigo());
                        t_prod.setTipo(t_termo.getTipo());
                        break;
                    case 20: 
                        t_termo = pilha_aux.pop();
                        t_binaria = pilha_aux.pop();
                        if(!t_binaria.getTipo().equals("") && !t_termo.getTipo().equals(t_binaria.getTipo())){
                            LOGGER.severe("os tipos da expressão '"+ t_termo.getValor() + t_binaria.getCodigo() +
                                            "' não são compativeis! NÃO é possível operar "+t_termo.getValor() + 
                                            " e "+t_binaria.getTipo());
                            return "os tipos da expressão '"+ t_termo.getValor() + t_binaria.getCodigo() +
                                            "' não são compativeis! NÃO é possível operar "+t_termo.getValor() + 
                                            " e "+t_binaria.getTipo();
                        }
                        t_prod.setTipo(t_termo.getTipo());
                        t_prod.setCodigo(t_termo.getCodigo()+ " " + t_binaria.getCodigo());
                        break;
                    case 21:
                        t_op = pilha_aux.pop();
                        t_termo = pilha_aux.pop();
                        
                        t_prod.setCodigo(t_op.getCodigo() + " "+ t_termo.getCodigo());
                        break;
                    case 22: break;
                    case 23: 
                        t_termo = pilha_aux.pop();
                        t_termo = pilha_aux.pop();
                        if(!tabela_simbolos.contains(t_termo.getValor())){
                            LOGGER.severe("Variável "+t_termo.getValor()+" não foi declarada.");
                            return "Variável "+t_termo.getValor()+" não foi declarada.";
                        }
                        if(!t_termo.getTipo().equals("bool")){
                          LOGGER.severe("Não é possível atribuir o tipo "+ t_termo.getTipo()+ " a uma variável do tipo bool");  
                          return "Não é possível atribuir o tipo "+ t_termo.getTipo()+ " a uma variável do tipo bool";
                        }
                        break;
                    case 24: 
                        t_termo = pilha_aux.pop();
                        t_termo = pilha_aux.pop();
                        t_prod.setTipo(t_termo.getTipo());
                        t_prod.setCodigo("- "+t_termo.getCodigo());
                        break;
                    case 26: 
                        t_termo = pilha_aux.pop();
                        t_prod.setCodigo(t_termo.getValor());
                        t_prod.setTipo("int");
                        break;
                    case 29: 
                        t_prod.setTipo("int");
                        t_prod.setCodigo(pilha_aux.pop().getValor());
                        break;
                    case 31: 
                        t_op = pilha_aux.pop();
                        t_prod.setCodigo(t_op.getCodigo());
                        t_prod.setTipo(t_op.getTipo());
                        break;
                    case 32:
                        t_op = pilha_aux.pop();
                        t_prod.setCodigo(t_op.getCodigo());
                        break;
                    case 36: 
                        t_op = pilha_aux.pop();
                        t_prod.setCodigo("<");
                        t_prod.setTipo("bool");
                        break;
                    case 42: 
                        t_op = pilha_aux.pop();
                        t_prod.setCodigo(t_op.getValor());
                        break;
                    case 62: break;
                    default: LOGGER.severe("nao entendi o que vc escreveu, você escreve de uma maneira burra cara. Que loucura"); return "deu pau"; 
                }
                /* ANALISE SEMANTICA */
                
                int s1 = popInt();
                pushInt(s1);
                LOGGER.info("Topo da pilha: "+ s1);
                this.pilha.push(t_prod);
                LOGGER.info("Empilhando produção "+ prodLog(t_prod.getValor())+": ");
                LOGGER.info("<span style=\"margin-left:2em\"> <b>Local: </b>" + t_prod.getLocal());
                LOGGER.info("<span style=\"margin-left:2em\"> <b>Tipo: </b>" + t_prod.getTipo());
                LOGGER.info("<span style=\"margin-left:2em\"> <b>codigo: </b>" + t_prod.getCodigo());
                LOGGER.info("<span style=\"margin-left:2em\"> <b>verdadeiro: </b>" + t_prod.getVerdadeiro());
                LOGGER.info("<span style=\"margin-left:2em\"> <b>falso: </b>" + t_prod.getFalso());
                LOGGER.info("<span style=\"margin-left:2em\"> <b>inicio: </b>" + t_prod.getInicio());
                LOGGER.info("<span style=\"margin-left:2em\"> <b>fim: </b>" + t_prod.getFim());
                
                String desvio = cellValue(s1, prod, true);
                LOGGER.info("Desvio["+ s1+", "+prodLog(prod)+"]: "+ desvio);
                this.pilha.push(new Token("", desvio));
                LOGGER.info("Empilhando desvio: "+ desvio);
            } else if(parser[0].equals("a")) {
                LOGGER.info("analise terminada, string aceita");
                return "aceita";
            } else {
                LOGGER.info("analise terminada, string recusada");
                return "recusada";
            }
        }
        LOGGER.info("analise terminada, string recusada");
        return "recusada";
    }
    
    public String[] parserCell(String celula){
        if(celula == null || celula.equals("")){
            LOGGER.warning("celula vazia, parser retornando nulo");
            return null;
        }
        String[] res = new String[2];
        res[0] = celula.substring(0, 1);
        res[1] = celula.substring(1);
        LOGGER.finest("parser: ["+ res[0]+ ", "+ res[1]+"]");
        return res;
    }
    
    public int popInt(){
        LOGGER.finest("desempilhando valor inteiro");
        Token aux = pilha.pop();
        int value = Integer.valueOf(aux.getValor());
        LOGGER.finest("valor inteiro desempilhado: "+ value);
        return value;
    }
    
    public void pushInt(int value){
        Token aux = new Token("", String.valueOf(value));
        LOGGER.finest("empilhando valor inteiro");
        pilha.push(aux);
        LOGGER.finest("valor inteiro empilhado: "+ value);
    }
    
    public int getIndexTable(String chave, boolean isTable){
        String str = isTable!=true?"produções" : "estados";
        LOGGER.finest("Buscando indice no cabeçalho de " + str);
        int len = this.cabecalhoTabela.length;
        LOGGER.finest("tamanho do cabeçalho: "+ len);
        String[] tabela = null;
        if(isTable){
            tabela = this.cabecalhoTabela;
        } else {
            tabela = this.cabecalhoProducoes;
        }
        for(int i=0; i<len; i++){
            LOGGER.finest("comparando valor desejado com indice: "+ i);
            if(tabela[i].equals(chave)){
                LOGGER.finest("cabeçalho encontrado, indice: "+ i);
                return i;
            }
        }
        LOGGER.finest("cabeçalho não encontrado encontrado, indice: -1");
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
    
    public String cellValue(int state, String name, boolean isTable) throws ArrayIndexOutOfBoundsException{
        int index = getIndexTable(name, isTable);
        if(index < 0 || state < 0)
            return null;
        if(isTable){
            return this.tabela.get(state)[index];
        } else {
            return this.producoes.get(state)[index];
        }
    }
    
    public List<String> findExpected(int state, boolean isTable){
        List<String> expectedValues = new ArrayList();
        String[] vetor = this.tabela.get(state);
        for(int i=1; i< vetor.length; i++){
            if(vetor[i] != null && !this.cabecalhoTabela[i].equals("lambda")){
                expectedValues.add(this.cabecalhoTabela[i]);
            }
        }
        return expectedValues;
    }
    
    public String prodLog(String prod){
        String str = prod.replaceAll("<", "&lt;");
        return str = str.replaceAll(">", "&gt;");
    }
    
    public List<String[]> readCSV(String filename, boolean isTable){
        
        
        /*
        LOGGER.severe("Info Log");
        LOGGER.warning("Info Log");
        LOGGER.info("Info Log");
        LOGGER.finest("Really not important");*/
        
        String arquivoCSV = filename;
        BufferedReader br = null;
        String linha = "";
        String csvDivisor = ",";
        List<String[]> Tabela = null;
        try {

            br = new BufferedReader(new FileReader(arquivoCSV));
            LOGGER.info("Arquivo "+ filename + " aberto com sucesso");
            Tabela = new ArrayList<>();
            LOGGER.finest("Tabela inicializada");
            //GAMBIARRA LIXO PQ JAVA É LIXO
            if(isTable){
                this.cabecalhoTabela = br.readLine().split(csvDivisor);
                LOGGER.finest("Lido o cabeçalho da tabela");
            } else {
                this.cabecalhoProducoes = br.readLine().split(csvDivisor);
                LOGGER.finest("Lido o cabeçalho das produções");
            }
            while ((linha = br.readLine()) != null) {

                String[] line = linha.split(csvDivisor);
                String str = "Linha lida: [";
                for(String s : line){
                    str += s + ",";
                }
                str += "]";
                LOGGER.finest(str);
                Tabela.add(line);
                LOGGER.finest("Linha adicionada na tabela de estados");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.severe("Arquivo: " + filename + " não encontrado");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Falha na abertura do arquivo: " + filename);
        } finally {
            if (br != null) {
                try {
                    br.close();
                    LOGGER.finest("Arquivo fechado com sucesso");
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.warning("Falha no fechamento do arquivo");
                }
            }
        }
        return Tabela;
    }
}
