package cs601.webmail.frameworks.db.page;

import java.util.List;

/**
 * Created by yuanyuan on 10/29/14.
 */
public class Page<T> {

    private List<T> pageList;

    private int position;

    private int pageSize;

    private int total;

    public List<T> getPageList() {
        return pageList;
    }

    public void setPageList(List<T> pageList) {
        this.pageList = pageList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

