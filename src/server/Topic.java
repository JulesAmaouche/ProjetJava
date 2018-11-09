package server;

import java.io.*;
import java.util.ArrayList;

public class Topic implements Serializable {

    private String name;
    private ArrayList<Message> messages;
    //gestion des id topic
    private static int nbId = 0;
    private int id;

    public Topic(String name) throws IOException {
        this.name = name;
        this.messages = new ArrayList<>();
        this.id = nbId;
        //incrémente l'Id des topics pour les topic a venir
        nbId++;
        Message titreDuTopic = new Message("  -------- Titre: "+name+" --------  ");
        messages.add(titreDuTopic);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public static int getNbId() {
        return nbId;
    }

    public int getId() {
        return id;
    }

    // Affiche le texte contenu dans le fichier du topic selectioner par le user
    public void showTopic() {
        for (Message m : messages) {
            System.out.println(m.getMessage());
            
        }
    }

    // Ajoute le message a la liste des messages du topic
    public void addMessage(Message message) {
        this.messages.add(message);

    }
}
