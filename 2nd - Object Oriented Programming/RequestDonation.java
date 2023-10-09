import java.util.Comparator;

public class RequestDonation implements Comparator{
    
    private Entity entity;
    private double quantity;
    
    RequestDonation(Entity entity, double quantity) {
        this.entity = entity;
        this.quantity = quantity;
    }


    //Getters/Setters
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    //Overwrites
    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        //Auto-generated method stub
        //return super.equals(obj);
        
        if(this.entity.getId()==((RequestDonation) obj).entity.getId()){
            return true;
        } else {
            return false;
        }
    }
}