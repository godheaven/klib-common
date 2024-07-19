package cl.kanopus.common.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo Diaz Saavedra
 * @param <T>
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class Paginator<T> {

    private List<T> records = new ArrayList<>();
    private long totalRecords;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

}
