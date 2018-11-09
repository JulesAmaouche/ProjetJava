package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    private Socket socket;
    private ServerVariables variables;
    private String username;
    private Topic topicClient;
    private int numTopic;

    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    public ClientHandler(Socket socket, ServerVariables variables) throws IOException {
        this.socket = socket;
        this.variables = variables;
        this.username = null;
        this.topicClient = null;
        this.numTopic = 0;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Topic getTopicClient() {
        return topicClient;
    }

    public void setTopicClient(Topic topicClient) {
        this.topicClient = topicClient;
    }

    @Override
    public void run() {
        try {

            //Gestion de l'autentification 
            // Réception du choix du client  
            System.out.println("Attend le choix d'autentification pour le client");

            // obliger de caster car choix 1 ou 2
            int choix = (int) ois.readObject();

            System.out.println("Choix de l'autentification reçu");
            // Gestion de la connection et de création du compte
            AuthenticationRequest auth;
            switch (choix) {
                //Connection
                case 1:
                    int tentative = 2;
                    boolean state;
                    do {
                        auth = (AuthenticationRequest) ois.readObject();
                        // verification dans le fichier texte
                        state = User.authentification(auth);

                        oos.writeObject(state);
                        tentative--;

                    } while (tentative >= 0 && state == false);

                    if (state == true) {
                        System.out.println("Utilisateur " + auth.getLogin() + " s'est bien authentifié.");
                        username = auth.getLogin();
                    } else {
                        System.err.println("Utilisateur inconnu déconnecté.");
                        socket.close();
                        return;
                    }

                    break;

                //Création d'un compte
                case 2:
                    auth = (AuthenticationRequest) ois.readObject();
                    User.createAccount(auth);
                    username = auth.getLogin();
                    System.out.println("Compte créer pour " + username);
                    break;

                default:
                    break;
            }

            //Gestion des topic 
            // Réception du choix du client
            System.out.println("Attend le choix de " + username + " pour la gestion du topic");
            choix = (int) ois.readObject();
            System.out.println("Choix du traitement des topic reçu");

            switch (choix) {
                //Affichage des Topic
                case 1:

                    oos.writeObject(variables.getTopics());
                    if (variables.getTopics().isEmpty()) {
                        System.err.println("Aucun Topic n'existe");
                        System.out.println("Création d'un topic");
                        createTopicServer();
                    } else {
                        //Recois le num du topic selectionner par le client
                        numTopic = (int) ois.readObject();
                        
                        // Donne le topic au client Handler
                        topicClient = variables.getTopic(numTopic);
                        // Envoi le topic demandé par le client
                        oos.writeObject(variables.getTopic(numTopic));
                        System.out.println("Affichage du topic " + variables.getTopic(numTopic).getName());
                    }
                    break;

                //Création d'un Topic 
                case 2:
                    createTopicServer();
                    break;

                default:
                    break;
            }

            variables.addClient(this);
            communication();

            // Gestion des messages
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Exeption");
        }
    }

    // Gestion de la creation du Topic coté server
    public void createTopicServer() throws IOException, ClassNotFoundException {
        // Reçois le nom du topic envoyé par le Client
        String topicName = (String) ois.readObject();
        
        //Création du topic
        Topic topic = new Topic(topicName);
        //Ajout du nouveau topic dans la liste des topics 
        variables.addTopic(topic);
        // recup de l'Id du topic
        numTopic = topic.getId();
        //le client handler récup le Topic du Client
        topicClient = topic;
        System.out.println("Topic " + topicName + " créer");
        
        //Envoi le topic au client
        oos.writeObject(topic);
    }

    public void communication() throws IOException, ClassNotFoundException {
        while (true) {

            String messageClient = (String) ois.readObject();
            
            if (messageClient.contains("/quit")) {
                System.out.println("L'utilisateur " + username + " s'est déconnecté");
                // supprime le client de la liste des clients
                variables.removeClient(this);
                socket.close();
                return;
            }

            //Ajoute le messageClient dans la liste de messages du topic
            variables.getTopic(numTopic).addMessage(new Message(messageClient));

           // transfer le message au client qui sont sur le même topic grace à l'id
            for (ClientHandler client : variables.getClients()) {
                if (client.getTopicClient().getId() == numTopic) {
                    client.oos.writeObject(messageClient);
                }
            }
        }
    }
}
