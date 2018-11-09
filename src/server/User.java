package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class User {

    //Chemin du fichier d'autentification
    private static File accountFile = new File("C:\\Users\\Utilisateur\\Documents\\NetBeansProjects\\Projet\\auth.txt");

    // Identification du user
    public static boolean authentification(AuthenticationRequest authR) throws IOException {
        FileReader fileReader = new FileReader(accountFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        String[] parseLine;
        String[] parseAuth;

        while ((line = bufferedReader.readLine()) != null) {
            parseLine = line.split("/");
            for (int i = 0; i < parseLine.length; i++) {
                parseAuth = parseLine[i].split(",");
                if (parseAuth[0].equals(authR.getLogin()) && parseAuth[1].equals(authR.getPassword())) {
                    return true;
                }
            }            
        }
        return false;
    }
        //Création du compte
    public static void createAccount(AuthenticationRequest authR) throws IOException {
        FileWriter fileWriter = new FileWriter(accountFile, true);
        fileWriter.write(authR.getLogin() + "," + authR.getPassword()+"/");
        fileWriter.close();
    }
}
