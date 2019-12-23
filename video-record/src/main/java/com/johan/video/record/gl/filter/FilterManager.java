package com.johan.video.record.gl.filter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johan on 2019/11/26.
 */

public class FilterManager {

    private List<Filter> filters;

    public FilterManager() {
        this.filters = new LinkedList<>();
    }

    public void add(Filter filter) {
        filters.add(filter);
    }

    public boolean addBefore(Filter filter, Class<? extends Filter> filterClass) {
        int index = -1;
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).getClass() == filterClass) {
                index = i;
            }
        }
        if (index == -1) return false;
        filters.add(index, filter);
        return true;
    }

    public <F extends Filter> F findFilter(Class<? extends Filter> filterClass) {
        for (Filter filter : filters) {
            if (filter.getClass() == filterClass) {
                return (F) filter;
            }
        }
        return null;
    }

    public void remove(Class filterClass) {
        Filter filter = findFilter(filterClass);
        if (filter == null) return;
        filters.remove(filter);
    }

    public List<Filter> getFilters() {
        return filters;
    }

}
