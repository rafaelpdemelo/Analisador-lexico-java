/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teste.analisador.lexico.main;

import analisador.lexico.compilador.JLScanner;
import analisador.lexico.compilador.Token;
import teste.analisador.lexico.exceptions.JLLexicalException;

/**
 *
 * @author j-vit
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
        JLScanner sc = new JLScanner("C:\\Users\\Desktop\\Desktop\\input.jl");
        Token token = null;
        do{
            token = token = sc.nextToken();
            if(token != null){
                System.out.println(token);
            }
        }while(token != null);
        
    }catch(JLLexicalException ex){
            System.out.println("Lexical ERROR " + ex.getMessage());
    }
        catch (Exception ex){
            System.out.println("Generic ERROR");
        }
        
    }
    
}
