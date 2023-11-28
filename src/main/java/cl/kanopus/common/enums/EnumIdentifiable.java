package cl.kanopus.common.enums;

public interface EnumIdentifiable<K> {

    K getId();

    default String getLabel() {
        return toString();
    }

}
