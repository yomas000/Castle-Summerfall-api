package game;
// Doors, like the type you go through.
public class Door {
    private boolean open;
    private boolean barred;
    private boolean locked;
    public Door(boolean open, boolean barred, boolean locked) {
        this.open = open;
        setBarred(barred);
        setLocked(locked);
    }

    
    /** 
     * @return String
     */
    public String open() {
        if(!locked){
            open = true;
            return "You open the door";
        } else{
            return "The door appears to be locked.";
        }
    }

    public void close() {
        open = false;
    }

    
    /** 
     * @param barred
     */
    public void setBarred(boolean barred) {
        this.barred = barred;
    }

    
    /** 
     * @param locked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void unlock(){
        locked = false;
    }

    
    /** 
     * @return boolean
     */
    public boolean isOpen(){
        return open;
    }
}
