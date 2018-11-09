package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.*;

public class Client {

    private static String serverIp = "127.0.0.1";
    private static int serverPort = 8000;
    private static Socket s;

    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    private static ArrayList<Topic> topics;

    private static String username;

    public static void main(String arg[]) throws IOException, ClassNotFoundException {

        s = new Socket(serverIp, serverPort);
        oos = new ObjectOutputStream(s.getOutputStream());
        ois = new ObjectInputStream(s.getInputStream());

        Scanner scan = new Scanner(System.in);

        System.out.println(" ____________________________________________");
        System.out.println("|           Bienvenue sur Chatime            |");
        System.out.println("|                                            |");
        System.out.println("|              [1] Se connecter              |");
        System.out.println("|              [2] S'inscrire                |");
        System.out.println("|____________________________________________|\n");

        // Demande du choix à l'utilisateur
        System.out.print("Entrer 1 ou 2 : ");
        int choix = scan.nextInt();

        // Blindage pour éviter les erreurs
        while (choix != 1 && choix != 2) {
            System.err.println("ERREUR: Entrer 1 ou 2 :");
            choix = scan.nextInt();
        }

        // Ici le client envoi le choix de l'utilisateur au serveur
        oos.writeObject(choix);

        // Le switch dirige l'utilisateur vers son choix 
        switch (choix) {
            case 1:

                int tentative = 2;
                boolean state;

                do {
                    System.out.println("Veuillez vous authentifier ");
                    System.out.println("Login : ");
                    String login = scan.next();
                    System.out.println("Password : ");
                    String password = scan.next();

                    AuthenticationRequest auth = new AuthenticationRequest(login, password);
                    oos.writeObject(auth);
                    username = auth.getLogin();

                    state = (boolean) ois.readObject();
                    if (state) {
                        System.out.println("Bienvenue " + username);
                    } else {
                        System.out.println("Non connecte");
                        System.out.println("Il vous reste " + tentative + " tentative(s)");
                        tentative--;
                    }

                } while (tentative >= 0 && state == false);

                if (state == false) {
                    s.close();
                    System.exit(0);
                }

                break;

            case 2:

                System.out.println("Veuillez entrer les paramétres suivants");
                System.out.println("Login : ");
                String login = scan.next();
                System.out.println("Password : ");
                String password = scan.next();

                AuthenticationRequest auth = new AuthenticationRequest(login, password);
                oos.writeObject(auth);
                username = auth.getLogin();

                System.out.println("Super !!! Le compte a bien été créé pour " + username);

                break;

            default:
                break;
        }
        //Gestion des topic

        System.out.println("__________________________________________");
        System.out.println("|           Gestion des topic            |");
        System.out.println("|                                        |");
        System.out.println("|          [1] Liste des topic           |");
        System.out.println("|          [2] Créer un topic            |");
        System.out.println("|________________________________________|\n");

        // Demande du choix à l'utilisateur
        System.out.print("Entrer 1 ou 2 : ");
        choix = scan.nextInt();

        // Blindage pour éviter les erreurs
        while (choix != 1 && choix != 2) {
            System.err.println("ERREUR: Entrer 1 ou 2 :");
            choix = scan.nextInt();
        }

        // Ici le client envoi le choix de l'utilisateur au serveur
        oos.writeObject(choix);

        switch (choix) {
            case 1:
                afficherListeTopic();
                break;

            case 2:
                createTopicClient();
                break;

            default:
                break;
        }

        System.out.println("Vous étes connecté au topic");

        //Le client reçois le topic
        Topic leTopic = (Topic) ois.readObject();
        
        // Affiche l'historique des messages qui sont dans le topic
        leTopic.showTopic();
        System.out.println("Pour quitter faire: /quit");
        System.out.println("Entrez vos messages");
        
        
        envoyerMessage();
        recevoirMessage();
    }

    public static void envoyerMessage() {
        Scanner scan = new Scanner(System.in);
        String debut = username + " => "; // utiliser au debut de l'affichage du message 

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //recupere le message taper par le client  
                    String msg = scan.nextLine();
                    try {
                        if (msg.contains("/quit")) {
                            oos.writeObject(msg);
                            System.out.println("Vous vous étes déconnecté");
                            s.close();
                            System.exit(0);
                        }
                        
                        // concatène le message et l'envoi au clientHandler
                        String message = debut+msg;
                        oos.writeObject(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sendMessage.start();
    }

    public static void recevoirMessage() throws IOException, ClassNotFoundException {
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // lit le message envoyé par un autre client 
                        String msg = (String) ois.readObject();
                        System.out.println(msg);
                    } catch (IOException e) {
                    } catch (ClassNotFoundException ex) {
                        System.err.println(ex);
                    }
                }
            }
        });
        readMessage.start();
    }

    public static void afficherListeTopic() throws IOException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);
        topics = (ArrayList<Topic>) ois.readObject();

        if (topics.isEmpty()) {
            System.err.println("Aucun Topic n'existe");
            createTopicClient();
        } else {
            listTopics(topics);
            System.out.println("Selectionnez un topic");
            int numTopic = scan.nextInt();
            oos.writeObject(numTopic);
        }

    }

    public static void createTopicClient() throws IOException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Merci d'entrer le nom de Topic souhaité");
        String topicName = scan.next();
        oos.writeObject(topicName);
    }

    //Listing des topic présent dans la liste topics reçu
    public static void listTopics(ArrayList<Topic> topics) {
        System.out.println("Liste des Topic:");
        int i = 0;
        for (Topic t : topics) {
            System.out.println(i + " - " + t.getName());
            i++;
        }
    }

}
