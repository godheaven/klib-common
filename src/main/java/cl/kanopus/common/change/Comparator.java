package cl.kanopus.common.change;

/**
 *
 * @author Pablo Diaz Saavedra
 * @param <T>
 * @email pabloandres.diazsaavedra@gmail.com
 *
 */
public class Comparator<T extends Object> {

    private ChangeAction action;
    private T value;

    public Comparator() {

    }

    public Comparator(ChangeAction action, T value) {
        this.action = action;
        this.value = value;
    }

    public ChangeAction getAction() {
        return action;
    }

    public void setAction(ChangeAction action) {
        this.action = action;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
