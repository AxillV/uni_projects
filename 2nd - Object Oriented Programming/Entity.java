abstract public class Entity

{
    //Variables protected so they can be initialized from Material or Service
    protected String name;
    protected String description;
    protected int id;

    Entity(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getEntityInfo() {
        return name + ": " + description + ". (" + id + ")";
    }
    
    abstract String getDetails();

    @Override
    public String toString() {
        return 
        "╔" + "═".repeat(9) + "╦" + "═".repeat(39)
        + "\n║ Info    ║ " + this.getEntityInfo()
        + "\n╠" + "═".repeat(9) + "╬" + "═".repeat(39)
        + "\n║ Details ║" + this.getDetails()
        + "\n╚" + "═".repeat(9) + "╩" + "═".repeat(39);
    }

}



