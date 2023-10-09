class Material extends Entity
{
    //LEVEL_1 : ena atomo
    //LEVEL_2 : 2-4 atoma 
    //LEVEL_3 : >=5
    
    final double LEVEL_1 , LEVEL_2, LEVEL_3;
    
    Material(String name, String description, int id, double LEVEL_1, double LEVEL_2, double LEVEL_3)
    {
        super(name, description, id);
        this.LEVEL_1=LEVEL_1;
        this.LEVEL_2=LEVEL_2;
        this.LEVEL_3=LEVEL_3;
    }

    /**
     * @return To level 1/2/3, analoga me to @param level pou eisagei o xrhsths
     */
    public double getLevelQuantity(int level){
        switch (level) {
            case 1:
                return LEVEL_1;   
            case 2:
                return LEVEL_2;  
            case 3:
                return LEVEL_3;  
            default:
                return -1;
        }
    }

    public String getDetails()
    {
        return 
            "\n║ Material║"
            + "\n╠" + "═".repeat(9) + "╬" + "═".repeat(39)
            + "\n║ Level 1 ║ " + LEVEL_1 
            + "\n║ Level 2 ║ " + LEVEL_2  
            + "\n║ Level 3 ║ " + LEVEL_3;       
    }

}