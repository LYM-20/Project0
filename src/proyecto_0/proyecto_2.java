package proyecto_0;

import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

public class proyecto_2 {
	
	private String[] token;
	int n;
	List<String[]> variables;
	List<String> constants;
	boolean isVariable;
	boolean openProcess;
	boolean isVariableName;
	boolean isCondition;
	boolean isCommand;
	boolean isBlock;
	boolean loop;
	int estado;
	String txt;
	
	public proyecto_2(){
		token = new String[] {};
		variables = new ArrayList<String[]>( );
		constants = new ArrayList<String>( );
		txt = "";
		n = 0;
		isVariable = false;
		openProcess = false;
		isVariableName = false;
		isCondition = false;
		isCommand = false;
		isBlock = false;
		estado = 0;
		addConstants();
	}
	
	public void addConstants() {
		constants.add("Dim");
		constants.add("myXpos");
		constants.add("myYpos");
		constants.add("myChips");
		constants.add("myBalloons");
		constants.add("balloonsHere");
		constants.add("ChipsHere");
		constants.add("Spaces");
	}
	
	public void readArchive(String fileName) throws Exception {
		File file = new File(
	            fileName);
		BufferedReader br = new BufferedReader(new FileReader(file));

	    String st;
	    while ((st = br.readLine()) != null) {
	    	System.out.println(st);
	    	txt = txt.concat(st);
	    }
	    System.out.println(txt);

	}
	
	public void lexer() throws Exception{
		txt = txt.concat(".");
		String cadenaConEspacios = txt.replaceAll("([()])", " $1 ");
		System.out.println(cadenaConEspacios);
		token = cadenaConEspacios.split("\\s+");
		for (; this.n<token.length; n++){
			System.out.println(token[n]);
	        if(token[n].compareTo("(") == 0 && openProcess == false) {
	        	if(!validateCommand() && !validateLoop())
	        		openProcess = true;
	        	else
	        		System.out.println("se valido el comando");
	        }
	        if(estado == 1) {
	        	System.out.println("la syntax es incorrecta");
	        	break;	
	        }
	        if(token[n].compareTo("defvar") == 0 && openProcess == true) {
	        	validateVariableFormat();	
	        }
	        if((token[n].compareTo("if")== 0 && openProcess == true || token[n].compareTo("if")== 0 && isBlock == true) || isCondition == true || isCommand == true ) {
	        	validateConditionalSyntax();
	        }
	        if(token[n].compareTo("(") == 0 && openProcess == true && isCommand == false && isCondition == false && token[n+1].compareTo("(") == 0) {
	        	System.out.println("abriendo el bloque");
	        	isBlock = true;
	        		
	        }
	        if((token[n].compareTo(")") == 0) && openProcess == true && isBlock == true && isCommand == false && isCondition == false) {
	        	isBlock = false;
	        	openProcess = false;
	        	System.out.println("cerrando el bloque");
	        }
	        else if(token[n].compareTo("(") == 0 && openProcess == true && isCommand == false && isBlock == true && isCondition == false && token[n+1].compareTo("(") != 0 
	        		&& token[n+1].compareTo("if") != 0 && token[n+1].compareTo("defvar") != 0) {
	        	if(validateCommand()) {
	        		System.out.println("se verifico los comandos dentro del bloque");
	        		isCommand = false;
	        	} else {
	        		System.out.println("los comandos dentro del bloque son erroneos");
	        		estado = 1;
	        	}
	        }
	        
	    }
	}
	public boolean validateLoop() {
		if(token[n+1].compareTo("repeat") == 0 && (constants.contains(token[n+2]) || token[n+2].matches("-?\\d+") || token[n+2].compareTo("Spaces") == 0)) {
			n = n+3;
			if (token[n].compareTo("(") == 0 && token[n + 1].compareTo("if") == 0) {
				n = n+1;
				openProcess = true;
				System.out.println("empiezan las condicionales dentro del loop");
				System.out.println(token[n]);
				
			}
			else {
				estado = 1;
			}
		}
		return false;
	}
	public void validateConditionalSyntax() {
		if(token[n + 1].compareTo("(") == 0 && isCondition == false && token[n].compareTo("if") == 0 && isCommand == false)
			isCondition = true;	
		else if(isCondition == true) {
			if (token[n-1].compareTo("(") == 0) {
				if (!validateConditions()) {
					isCondition = false;
					estado = 1;
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
						estado = 1;
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
							if (isBlock == false) {
								openProcess = false;
								isCommand = false;
								System.out.println("se cierra los comandos y la condicion");
								n ++;
							} else {
								isCommand = false;
								System.out.println("se cierra los comandos y la condicion pero no el bloque");
								n++;
							}
						}
					} else if(token[n + 1].compareTo("(") == 0){
						isCommand = true;
					}
				} else {
					estado = 1;
					System.out.println("la syntax es incorrecta");
				}
			}
		}
		else {
			estado = 1;
			System.out.println("no cumple la condicion");
		} 
	}
	
	public void validateVariableFormat() {
		if(isVariableName == false) {
			constants.add(token[n + 1]);
			isVariableName = true;
		}
		if(isVariableName == true) {
			if(token[n+2].matches("-?\\d+") || constants.contains(token[n+2])) {
				if(token[n+3].compareTo(")") == 0) {
					isVariableName = false;
					openProcess = false;
					n = n+3;
					System.out.println("se valido el formato correctamente");
				} else {
					estado = 1;
					System.out.println("no cumple con el formato");
				}
			}else {
				System.out.println("no cumple con el formato");
				estado = 1;
			}
			
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
			if(token[n+1].compareTo(":balloons") ==  0|| token[n+1].compareTo(":chips") ==  0) {
				if(token[n+2].matches("-?\\d+") || constants.contains(token[n+2])) {
					n = n+2;
					return true;
				}
			}
		} else if((token[n].compareTo("can-pick?") == 0 && token[n+3].compareTo(")") == 0)) {
			if(token[n+1].compareTo(":balloons") ==  0|| token[n+1].compareTo(":chips") ==  0) {
				if(token[n+2].matches("-?\\d+") || constants.contains(token[n+2])) {
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
				if(token[n+2].compareTo(":balloons") ==  0|| token[n+2].compareTo(":chips") ==  0) {
					if(token[n+3].matches("-?\\d+") || constants.contains(token[n+3])) {
						n = n+4;
						return true;
					}
				}
			}
			else if(token[n+1].compareTo("pick") == 0) {
				if(token[n+2].compareTo(":balloons") ==  0|| token[n+2].compareTo(":chips") ==  0) {
					if(token[n+3].matches("-?\\d+") || constants.contains(token[n+3])) {
						n = n+4;
						return true;
					}
				}
			}
			else if(token[n+1].compareTo("move-face") == 0) {
				if(token[n+2].matches("-?\\d+") || constants.contains(token[n+2])) {
					if(token[n+3].compareTo(":north") ==  0|| token[n+3].compareTo(":south") ==  0
							|| token[n+3].compareTo(":west") ==  0|| token[n+3].compareTo(":east") ==  0) {
						n = n+4;
						return true;
					}
				}
			}
			else if(token[n+1].compareTo("move-dir") == 0) {
				if(token[n+2].matches("-?\\d+") || constants.contains(token[n+2])) {
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
				if(token[n+2].compareTo(":north") ==  0|| token[n+2].compareTo(":south") ==  0
						|| token[n+2].compareTo(":west") ==  0|| token[n+2].compareTo(":east") ==  0) {
					n = n+3;
					return true;
				}
			}else if(token[n+1].compareTo("move") == 0) {
				if(token[n+2].matches("-?\\d+") || constants.contains(token[n+2])) {
					n = n+3;
					return true;
				}
			}else if(token[n+1].compareTo("skip") == 0) {
				if(Integer.valueOf(token[n + 2]) >= 0 || constants.contains(token[n+2])) {
					n = n+3;
					return true;
				}
			}
		
		} 
		else if(token[n+1].compareTo("run-dirs") == 0) {
			n++;
			for(; this.n<token.length; n++){
				if(token[n+1].compareTo(":left") ==  0|| token[n+1].compareTo(":right") ==  0
						|| token[n+1].compareTo(":back") ==  0 || token[n+1].compareTo(":front") ==  0);
				else
					break;				
			}
			if (token[n+1].compareTo(")") == 0) {
				n++;
				return true;
			} else
				return false;
				
		}else if(token[n+2].compareTo(")") == 0) {
			if(token[n+1].equals("null")) {
				n = n + 2;
				return true;
			}
		}
		return false;
		
	}
	
}
