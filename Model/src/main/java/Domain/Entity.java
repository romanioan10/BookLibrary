package Domain;

public abstract class Entity implements IEntitate<Integer> {
    protected int id;

    public Entity(int id) {
        this.id = id;
    }

    public Entity()
    {

    }

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }

    @Override
    public Integer getId() {
        return id;
    }

}
