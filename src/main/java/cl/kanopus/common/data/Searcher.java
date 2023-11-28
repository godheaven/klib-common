package cl.kanopus.common.data;

import cl.kanopus.common.data.enums.SortOrder;

/**
 * This class stores information related to search engines. information is
 * transported to classes of the DAO type.
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 *
 * @param <T>
 */
public class Searcher<T> {

    private T id;
    private String text;
    private Integer offset = 0;
    private Integer limit = 10;
    private String sortField;
    private SortOrder sortOrder;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

}
