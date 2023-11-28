package cl.kanopus.common.change;

public interface Change {

    ChangeAction getAction();

    void setAction(ChangeAction action);

    long getId();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}
