package Gabri.Dev.com;

import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandProcessor commandProcessor = new CommandProcessor("C:\\Users");

        // Código ANSI para el color rosa
        String PINK = "\u001B[35m";
        String RESET = "\u001B[0m";

        System.out.println("Bienvenido a la BlackBox.");

        while (true) {
            System.out.print(PINK + commandProcessor.getCurrentDirectory() + "> " + RESET);
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                System.out.println("Saliendo del programa...");
                break;
            }

            String output = commandProcessor.processCommand(input);
            System.out.println(output);
        }

        scanner.close();
    }
}
