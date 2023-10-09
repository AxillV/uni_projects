class Service extends Entity
{
    Service(String name, String description, int id) {
        super(name, description, id);
    }

    public String getDetails()
    {
        return "Service";
    }

    
}