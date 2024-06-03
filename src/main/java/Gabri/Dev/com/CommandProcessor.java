package Gabri.Dev.com;

import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

class CommandProcessor {
    private String currentDirectory;

    // Constructor que inicializa el directorio actual
    public CommandProcessor(String initialDirectory) {
        File dir = new File(initialDirectory);
        if (dir.exists() && dir.isDirectory()) {
            currentDirectory = initialDirectory;
        } else {
            currentDirectory = System.getProperty("user.dir");
        }
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public String processCommand(String command) {
        String[] tokens = command.split("\\s+", 3);

        switch (tokens[0].toUpperCase(Locale.ROOT)) {
            case "CD":
                return changeDirectory(tokens);
            case "C":
                return createFile(tokens);
            case "O":
                return openFile(tokens);
            case "B":
                return search(tokens);
            case "D":
                return delete(tokens);
            case "ABOUTME":
                return aboutMe();
            case "HELP":
                return showHelp();
            default:
                return "Comando no reconocido. Usa 'HELP' para ver la lista de comandos.";
        }
    }

    private String changeDirectory(String[] tokens) {
        if (tokens.length < 2) {
            return "Uso incorrecto. Especifique una ruta válida.";
        }

        String newDirectory = tokens[1];
        File file = new File(newDirectory);

        // Si la ruta no es absoluta, la hacemos relativa al directorio actual
        if (!file.isAbsolute()) {
            newDirectory = Paths.get(currentDirectory, newDirectory).toString();
            file = new File(newDirectory);
        }

        if (!file.exists() || !file.isDirectory()) {
            return "La ruta especificada no es válida.";
        }

        currentDirectory = newDirectory;
        return "Directorio cambiado a: " + currentDirectory;
    }

    private String createFile(String[] tokens) {
        if (tokens.length < 2) {
            return "Uso incorrecto. Especifique un nombre de archivo.";
        }

        String fileName = tokens[1];
        File file = new File(currentDirectory, fileName);

        try {
            if (file.createNewFile()) {
                return "Archivo creado exitosamente.";
            } else {
                return "El archivo ya existe.";
            }
        } catch (IOException e) {
            return "Error al crear el archivo: " + e.getMessage();
        }
    }

    private String openFile(String[] tokens) {
        if (tokens.length < 3) {
            return "Uso incorrecto. Especifique un nombre de archivo y una aplicación.";
        }

        String fileName = tokens[1];
        String application = tokens[2];
        File file = new File(currentDirectory, fileName);

        if (!file.exists()) {
            return "El archivo especificado no existe.";
        }

        String command = null;

        switch (application) {
            case "code":
                command = "code " + file.getAbsolutePath();
                break;
            case "IJ":
                command = "idea " + file.getAbsolutePath();
                break;
            case "GX":
                command = "opera " + file.getAbsolutePath();
                break;
            default:
                return "Aplicación no reconocida.";
        }

        try {
            Runtime.getRuntime().exec(command);
            return "Archivo abierto con " + application + ".";
        } catch (IOException e) {
            return "Error al abrir el archivo: " + e.getMessage();
        }
    }

    private String search(String[] tokens) {
        if (tokens.length < 2) {
            return "Uso incorrecto. Especifique un término de búsqueda o URL.";
        }

        String query = tokens[1];
        String url;

        if (query.startsWith("http://") || query.startsWith("https://")) {
            url = query;
        } else {
            url = "https://www.google.com/search?q=" + query;
        }

        try {
            Desktop.getDesktop().browse(new URI(url));
            return "Búsqueda realizada en el navegador.";
        } catch (IOException | URISyntaxException e) {
            return "Error al realizar la búsqueda: " + e.getMessage();
        }
    }

    private String delete(String[] tokens) {
        if (tokens.length < 2) {
            return "Uso incorrecto. Especifique un archivo o carpeta para eliminar.";
        }

        File file = new File(tokens[1]);

        if (!file.exists()) {
            return "El archivo o carpeta especificado no existe.";
        }

        try {
            if (file.isDirectory()) {
                Files.walk(file.toPath())
                        .sorted((a, b) -> b.compareTo(a))
                        .map(java.nio.file.Path::toFile)
                        .forEach(File::delete);
            } else {
                Files.delete(file.toPath());
            }
            return "Archivo o carpeta eliminado exitosamente.";
        } catch (IOException e) {
            return "Error al eliminar el archivo o carpeta: " + e.getMessage();
        }
    }

    private String aboutMe() {
        return BLUE+ "Por si quieres apoyar el proyecto y/o realizar tus porpios comandos.\n" +
               "Aquí está el repositorio original del proyecto:  https://github.com/GabrielScipioni/BlackBox.git"+RESET;
    }
    // Códigos de formato ANSI para colores
    String RESET = "\u001B[0m";
    String BOLD = "\u001B[1m";
    String GREEN = "\u001B[32m";
    String BLUE = "\u001B[34m";

    private String showHelp() {

        // Construcción del mensaje de ayuda con formato
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append(GREEN).append("Lista de comandos:\n").append(RESET);
        helpMessage.append(BOLD).append("\n");
        helpMessage.append("CD /ruta/especifica      - Posiciona dentro de una carpeta.\n");
        helpMessage.append("C <nombre_archivo>       - Crea un nuevo archivo en tu posición.\n");
        helpMessage.append("O <nombre_archivo> <aplicación>\n");
        helpMessage.append("\n");
        helpMessage.append("                   - ").append(BLUE).append("code").append(RESET).append("                     - Abre con Visual Studio Code.\n");
        helpMessage.append("                   - ").append(BLUE).append("IJ").append(RESET).append("                       - Abre con IntelliJ.\n");
        helpMessage.append("                   - ").append(BLUE).append("GX").append(RESET).append("                       - Abre con OperaGX.\n");
        helpMessage.append("                   - ").append(BLUE).append("O /nombre/del/archivo").append(RESET).append("    - Abre una carpeta en una ruta específica.\n");
        helpMessage.append("                   - ").append(BLUE).append("O").append(RESET).append("                        - Abre la carpeta en la que estás.\n");
        helpMessage.append("\n");
        helpMessage.append("B <Palabra Clave>        - Realiza una búsqueda en el navegador.\n");
        helpMessage.append("B \"Url/De/la/pagina\"    - Entra a la página de la URL.\n");
        helpMessage.append("O <aplicación>           - Abre una aplicación específica.\n");
        helpMessage.append("D <nombre_archivo>       - Elimina un archivo dentro de la posición root.\n");
        helpMessage.append("D /ruta/especifica       - Elimina la carpeta que hace referencia al root.\n");
        helpMessage.append("ABOUTME                 - Te lleva al repositorio original de este proyecto.\n");
        helpMessage.append("").append(RESET);

        return helpMessage.toString();
    }
}
