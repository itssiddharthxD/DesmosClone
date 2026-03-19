package com.desmosclone.main;

public class MathEvaluator {
	
    public static double evaluate(final String str, final double xValue) {
    	
        return new Object() {
        	
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
            	
                while (ch == ' ') nextChar();
                
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm();
                    else if (eat('%')) x %= parseFactor();// subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); 
                if (eat('-')) return -parseFactor(); 

                double x;
                int startPos = this.pos;
                
                if (eat('(')) { // parentheses
                	
                    x = parseExpression();
                    eat(')');
                    
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                	
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                    
                } else if (ch >= 'a' && ch <= 'z') { // functions and variables
                	
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    
                    String func = str.substring(startPos, this.pos);
                    
                    if (func.equals("x")) x = xValue;
                    else if(func.equals("e")) x = Math.E;
                    else if(func.equals("pi")) x = Math.PI;
                    
                    else {
                        x = parseFactor();
                        
                        if (func.equals("sqrt")) x = Math.sqrt(x);
                        else if (func.equals("cbrt")) x = Math.cbrt(x);
                        
                        else if (func.equals("sin")) x = Math.sin(x);
                        else if (func.equals("cos")) x = Math.cos(x);
                        else if (func.equals("tan")) x = Math.tan(x);
                        else if (func.equals("cot")) x = 1 / Math.tan(x);
                        else if (func.equals("sec")) x = 1 / Math.cos(x);
                        else if (func.equals("cosec")) x = 1 / Math.sin(x);
                        
                        else if (func.equals("log")) x = Math.log10(x); 
                        else if (func.equals("ln")) x = Math.log(x);    
                        else if (func.equals("mod")) x = Math.abs(x); 
                        
                        
                        else if (func.equals("exp")) x = Math.exp(x);
                        
                        else throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
                return x;
            }
        }.parse();
    }
}
