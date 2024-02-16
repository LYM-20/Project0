package proyecto_0;

import proyecto_0.proyecto_2;

public class consola {

    private proyecto_2 proyectoE;

    public consola() {
        proyectoE = new proyecto_2();
    }

    private void execute() {
        proyectoE.lexer();
    }

    public static void main(String[] args) {
        consola c = new consola();
        c.execute();
    }
}

