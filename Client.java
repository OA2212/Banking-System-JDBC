public class Client {
    private String name;
    private int rank;

    public Client(String _name, int _rank) {
        this.name = _name;
        setRank(_rank);
    }

    public String getName() {
        return name;
    }

    //Set the rank of the client, ensuring it's within a valid range (0-10)
    public void setRank(int rank) {
        if (rank >= 0 && rank <= 10) {
            this.rank = rank;
        } else {
            throw new IllegalArgumentException("Rank must be between 0 and 10.");
        }
    }

    //Compare this client to another object for equality based on the name (case-insensitive)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Client client = (Client) obj;
        return name.equalsIgnoreCase(client.name);
    }

    //Generate a hash code for the client based on a case-insensitive version of the name
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    //Return a string representation of the client (currently only the name)
    @Override
    public String toString() {
        return getName();
    }
}
