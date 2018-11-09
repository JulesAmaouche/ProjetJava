package server;

import java.util.ArrayList;

public class ServerVariables {
// Nous sert a stocker toute les variables du serveur 
// liste des topics et liste des client connecté
    
    private ArrayList<Topic> topics;
    private ArrayList<ClientHandler> clients;

    public ServerVariables() {
        this.topics = new ArrayList<>();
        this.clients = new ArrayList<>();
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList <Topic> topics) {
        this.topics = topics;
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ClientHandler> clients) {
        this.clients = clients;
    }

    public Topic getTopic(int i) {
        return this.topics.get(i);
    }

    public void addTopic(Topic t) {
        this.topics.add(t);
    }

    public void addClient(ClientHandler client) {
        this.clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        this.clients.remove(client);
    }

}
