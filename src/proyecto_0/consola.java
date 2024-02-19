package proyecto_0;

import java.util.Scanner;

import proyecto_0.proyecto_2;

public class consola {

    private proyecto_2 proyectoE;

    public consola() {
        proyectoE = new proyecto_2();
    }

    private void execute() throws Exception {
    	Scanner sc= new Scanner(System.in); 
    	System.out.print("Ingrese el nombre del archivo: ");  
    	String fileName= sc.next();
    	proyectoE.readArchive(fileName);
        proyectoE.lexer();
    }

    public static void main(String[] args) throws Exception {
        consola c = new consola();
        c.execute();
    }
}

