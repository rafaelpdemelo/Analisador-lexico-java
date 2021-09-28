/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisador.lexico.compilador;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import teste.analisador.lexico.exceptions.JLLexicalException;

/**
 *
 * @author j-vit
 */
public class JLScanner {

    private char[] content;
    private int pos;
    private int estado;

    public JLScanner(String filename) {
        try {
            String txtConteudo;
            txtConteudo = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            System.out.println("DEBUG--------");
            System.out.println(txtConteudo);
            System.out.println("-------------");
            content = txtConteudo.toCharArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Token nextToken() {
        Token token;
        char currentChar;
        String term = "";
        if (isEOF()) {
            return null;
        }
        estado = 0;
        while (true) {
            currentChar = nextChar();
            switch (estado) {
                case 0:
                    if (isChar(currentChar)) {
                        term += currentChar;
                        estado = 1;
                    } else if (isDigit(currentChar)) {
                        estado = 3;
                        term += currentChar;

                    } else if (isSpace(currentChar)) {
                        estado = 0;
                    } else if (isOperator(currentChar)) {
                        term += currentChar;
                        token = new Token();
                        token.setType(Token.TK_OPERATOR);
                        token.setText(term);
                        return token;

                    } else if (isSeparator(currentChar)) {
                        term += currentChar;
                        token = new Token();
                        token.setType(Token.TK_SEPARATOR);
                        token.setText(term);
                        return token;

                    } else if (isUnderline(currentChar)) {
                       // estado = 7;
                        term += currentChar;
                        token = new Token();
                        token.setType(Token.TK_UNDERLINE);
                        token.setText(term);
                        return token;
                    } else {
                        throw new JLLexicalException("Unrecognized SYMBOL");
                    }
                    break;
                case 1:
                    if (isChar(currentChar) || isDigit(currentChar)) {
                        estado = 1;
                        term += currentChar;
                    } else if (isSpace(currentChar) || isOperator(currentChar) || isEOF(currentChar)) {
                        if (!isEOF(currentChar)) {
                            back();
                        }
                        token = new Token();
                        if(isReserved(term)){
                            token.setType(Token.TK_RESERVED);
                        }else{
                            token.setType(Token.TK_IDENTIFIER);
                        }
                        
                        token.setText(term);
                        return token;
                    } else {
                        throw new JLLexicalException("MALFORMED IDENTIFIER");
                    }
                    break;
                case 2:
                   
                    if (isDigit(currentChar)) {
                        estado = 2;
                        term += currentChar;
                        
                    }else if (isPonctuation(currentChar)) {
                    	estado = 9;
                    
                    }
                    else if (!isChar(currentChar) || isEOF(currentChar)) {
                        if (!isEOF(currentChar)) {
                            back();
                            token = new Token();
                            token.setType(Token.TK_NUMBER);
                            token.setText(term);
                            return token;

                        } else{
                             throw new JLLexicalException("MALFORMED IDENTIFIER");
                        }   
                    }
                case 3:
                    if (isDigit(currentChar)) {
                        estado = 3;
                        term += currentChar;

                    } else if (!isChar(currentChar)) {
                        estado = 4;
                    } else {
                        throw new JLLexicalException("Unrecognized NUMBER");
                    }
                    break;
                case 4:
                    token = new Token();
                    token.setType(Token.TK_NUMBER);
                    token.setText(term);
                    back();
                    return token;
                case 5:
                    term += currentChar;
                    token = new Token();
                    token.setType(Token.TK_OPERATOR);
                    token.setText(term);
                    back();
                    return token;
                case 6:
                    if (isDigit(currentChar)) {
                        estado = 3;
                        term += currentChar;
                    } else if (!isChar(currentChar)) {
                        estado = 8;
                    } else {
                        throw new JLLexicalException("Unrecognized NUMBER");
                    }
                case 7:
                    term += currentChar;
                    token = new Token();
                    token.setType(Token.TK_UNDERLINE);
                    token.setText(term);
                    back();
                    return token;
                case 8:
                    term += currentChar;
                    token = new Token();
                    token.setType(Token.TK_SEPARATOR);
                    token.setText(term);
                    back();
                    return token;
                case 9: 
                	if (isDigit(currentChar)) {
                		term+=currentChar;
                		estado = 10;
                	}else {
                		throw new JLLexicalException("Unrecognized FLOAT_NUMBER");
                	}
                case 10:
                	if (isDigit(currentChar)) {
                		estado = 3;
                		term += currentChar;
                	}else if(!isChar(currentChar)) {
                		estado = 4;
                		System.out.println("FIM DO ARQUIVO CARALHO");
                	}else {
                		throw new JLLexicalException("Unrecognized FLOAT_NUMBER");
                	}
            }
        }
    }

    private boolean isPonctuation (char c ) {
    	return c == '.';
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isChar(char c) {
        return c >= 'a' && c <= 'z';
    }

    /* private boolean isFloat(char c){
        return c >= '0.0' && c <= '9.9';
    }
     */
    private boolean isOperator(char c) {
        return c == '>' || c == '<' || c == '=' || c == '!' || c == '+' || c == '<' || c == '=' || c == '-';
    }

    private boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private char nextChar() {
        return content[pos++];
    }

    private boolean isEOF(char c) {
        return c == '\0';
    }

    private boolean isEOF() {
        return pos >= content.length;
    }

    private boolean isUnderline(char c) {
        return c == '_';
    }

    private boolean isSeparator(char c) {
        return (c == '(' || c == ')' || c == '{' || c == '}' || c == ',' || c == ';');
    }

    private void back() {
        pos--;
    }
    
    private boolean isReserved(String c){
        return c.compareTo("double") == 0 ||
                c.compareTo("int") == 0||
                c.compareTo("float") == 0||
                c.compareTo("if") == 0||
                c.compareTo("else") == 0 ||
                c.compareTo("do") == 0||
                c.compareTo("while") == 0||
                c.compareTo("char") == 0||
                c.compareTo("main") == 0;
    }
}
