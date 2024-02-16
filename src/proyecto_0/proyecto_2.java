package proyecto_0;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

public class proyecto_2 {
	
	private String[] token;
	int n;
	List<String[]> variables;
	boolean isVariable;
	boolean openProcess;
	boolean isVariableName;
	boolean isCondition;
	boolean isCommand;
	List<String> conditions;
	
	public proyecto_2(){
		token = new String[] {};
		variables = new ArrayList<String[]>( );
		n = 0;
		isVariable = false;
		openProcess = false;
		isVariableName = false;
		isCondition = false;
		isCommand = false;
		conditions = new ArrayList<String>( );
		conditions.add("can-move?");
	}
	
	public boolean execute() {
		
		String cadena = "defun foo (c p)";

		String formatoDeseadoClase = "defvar\\s+\\w+\\s+\\d+";
		String formatoDeseadoMetodo = "defun\\s+\\w+\\s+\\(.*\\)";
		
		Pattern formatoClase = Pattern.compile(formatoDeseadoClase);
		Pattern formatoMetodo = Pattern.compile(formatoDeseadoMetodo);
		Matcher cumple = formatoMetodo.matcher(cadena);
		if (cumple.matches())
			System.out.println("La cadena cumple");
		else
			System.out.println("La cadena no cumple");
		
		return false;
	}
	
	public void lexer() {
		String cadena = "(defvar hl 3)(if (not(can-put? balloons 2)) (face :north) (pick balloons 2) )";
		String cadenaConEspacios = cadena.replaceAll("([()])", " $1 ");
		System.out.println(cadenaConEspacios);
		token = cadenaConEspacios.split("\\s+");
		for (; this.n<token.length; n++){
			System.out.println(token[n]);
	        if(token[n].compareTo("(") == 0 && openProcess == false)
	        	openProcess = true;
	        if(token[n].compareTo("defvar") == 0 && openProcess == true) {
	        	validateVariableFormat();	
	        }
	        if((token[n].compareTo("if")== 0 && openProcess == true) || isCondition == true || isCommand == true) {
	        	validateConditionalSyntax();
	        }
	        
	    }
	}
	public void validateConditionalSyntax() {
		if(token[n + 1].compareTo("(") == 0 && isCondition == false && token[n].compareTo("if") == 0 && isCommand == false)
			isCondition = true;	
		else if(isCondition == true) {
			if (token[n-1].compareTo("(") == 0) {
				if (!validateConditions()) {
					isCondition = false;
					System.out.println("no cumple la condicion");
				}
			} else if (token[n].compareTo(")") == 0) {
				if(token[n + 1].compareTo("(") != 0) {
					if(token[n + 1].compareTo(")") == 0) {
						openProcess = false;
						isCondition = false;
						System.out.println("se cierra la condicion");
					} else {
						isCondition = false;
						System.out.println("no cumple la condicion");
					}
				} else if(token[n + 1].compareTo("(") == 0){
					isCondition = false;
					isCommand = true;
					System.out.println("empiezan los comandos");
				}
			}
		} else if(isCommand == true) {
			
			if (token[n].compareTo("(") == 0) {
				if (validateCommand()){
					System.out.println(n);
					if(token[n + 1].compareTo("(") != 0) {
						if(token[n + 1].compareTo(")") == 0) {
							openProcess = false;
							isCommand = false;
							System.out.println("se cierra los comandos y la condicion");
						}
					} else if(token[n + 1].compareTo("(") == 0){
						isCommand = true;
					}
				} else {
					System.out.println("la syntax es incorrecta");
				}
			}
		}
		else {
			System.out.println("no cumple la condicion");
		} 
	}
	
	public void validateVariableFormat() {
		String[] tokenVariable = new String[2];
		if(isVariableName == false) {
			tokenVariable[0] = token[n + 1];
			isVariableName = true;
		}
		if(isVariableName == true) {
			if(Integer.valueOf(token[n + 2]) >= 0) {
				tokenVariable[1] = token[n + 2];
				variables.add(tokenVariable);
				if(token[n+3].compareTo(")") == 0) {
					isVariableName = false;
					openProcess = false;
					n = n+3;
					System.out.println("se valido el formato correctamente");
				} else
					System.out.println("no cumple con el formato");
			}else
				System.out.println("no cumple con el formato");
			
		}
	}
		
	
	public boolean validateConditions() {
		if(token[n].compareTo("can-move?") == 0 && token[n+2].compareTo(")") == 0) {
			if(token[n+1].compareTo(":north") ==  0|| token[n+1].compareTo(":south") ==  0
					|| token[n+1].compareTo(":west") ==  0|| token[n+1].compareTo(":east") ==  0) {
				n += 1;
				return true;
			}
		}else if((token[n].compareTo("facing?") == 0 && token[n+2].compareTo(")") == 0)) {
			if(token[n+1].compareTo(":north") ==  0|| token[n+1].compareTo(":south") ==  0
					|| token[n+1].compareTo(":west") ==  0|| token[n+1].compareTo(":east") ==  0) {
				n += 1;
				return true;
			}
		}else if((token[n].compareTo("can-put?") == 0 && token[n+3].compareTo(")") == 0)) {
			if(token[n+1].compareTo("balloons") ==  0|| token[n+1].compareTo("chips") ==  0) {
				if(Integer.valueOf(token[n + 2]) >= 0) {
					n = n+2;
					return true;
				}
			}
		} else if((token[n].compareTo("can-pick?") == 0 && token[n+3].compareTo(")") == 0)) {
			if(token[n+1].compareTo("balloons") ==  0|| token[n+1].compareTo("chips") ==  0) {
				if(Integer.valueOf(token[n + 2]) >= 0) {
					n = n+2;
					return true;
				}
			}
		} else if((token[n].compareTo("blocked?") == 0 && token[n+1].compareTo(")") == 0)) {
			return true;
		} else if(token[n].compareTo("not") == 0 && token[n+1].compareTo("(") == 0) {
			n += 2;
			if (validateConditions()) {
				if(token[n+2].compareTo(")") == 0) {
					n = n+1;
					return true;
				}		
			}
		}
		return false;
	}
	
	public boolean validateCommand() {
		System.out.println(n);
		if(token[n+4].compareTo(")") == 0 && token[n+3].compareTo(")") != 0) {
			if(token[n+1].compareTo("put") == 0) {
				if(token[n+2].compareTo("balloons") ==  0|| token[n+2].compareTo("chips") ==  0) {
					if(Integer.valueOf(token[n + 3]) >= 0) {
						n = n+4;
						return true;
					}
				}
			}
			else if(token[n+1].compareTo("pick") == 0) {
				if(token[n+2].compareTo("balloons") ==  0|| token[n+2].compareTo("chips") ==  0) {
					if(Integer.valueOf(token[n + 3]) >= 0) {
						n = n+4;
						return true;
					}
				}
			}
			else if(token[n+1].compareTo("move-face") == 0) {
				if(Integer.valueOf(token[n + 2]) >= 0) {
					if(token[n+3].compareTo(":north") ==  0|| token[n+3].compareTo(":south") ==  0
							|| token[n+3].compareTo(":west") ==  0|| token[n+3].compareTo(":east") ==  0) {
						n = n+4;
						return true;
					}
				}
			}
			else if(token[n+1].compareTo("move-dir") == 0) {
				if(Integer.valueOf(token[n + 2]) >= 0) {
					if(token[n+3].compareTo(":front") ==  0|| token[n+3].compareTo(":right") ==  0
							|| token[n+3].compareTo(":left") ==  0|| token[n+3].compareTo(":back") ==  0) {
						n = n+4;
						return true;
					}
				}
			}
			
		} else if(token[n+3].compareTo(")") == 0 && token[n+2].compareTo(")") != 0) {
			if(token[n+1].compareTo("turn") == 0) {
				if(token[n+2].compareTo(":left") ==  0|| token[n+2].compareTo(":right") ==  0
						|| token[n+2].compareTo(":around") ==  0) {
					n = n+3;
					return true;
				}
			} else if(token[n+1].compareTo("face") == 0) {
				System.out.println("validando");
				if(token[n+2].compareTo(":north") ==  0|| token[n+2].compareTo(":south") ==  0
						|| token[n+2].compareTo(":west") ==  0|| token[n+2].compareTo(":east") ==  0) {
					System.out.println("validado");
					n = n+3;
					return true;
				}
			}else if(token[n+1].compareTo("run-dirs") == 0) {
				if(token[n+2].compareTo(":left") ==  0|| token[n+2].compareTo(":right") ==  0
						|| token[n+2].compareTo(":around") ==  0 || token[n+2].compareTo(":front") ==  0) {
					n = n+3;
					return true;
				}
			}else if(token[n+1].compareTo("move") == 0) {
				if(Integer.valueOf(token[n + 2]) >= 0) {
					n = n+3;
					return true;
				}
			}else if(token[n+1].compareTo("skip") == 0) {
				if(Integer.valueOf(token[n + 2]) >= 0) {
					n = n+3;
					return true;
				}
			}
		} else if(token[n+2].compareTo(")") == 0) {
			System.out.println("v");
			if(token[n+1].compareTo("null") == 0) {
				System.out.println("null");
				n = n + 2;
				return true;
			}
		}
		return false;
		
	}
	
}
