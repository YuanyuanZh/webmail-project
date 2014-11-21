package cs601.webmail.frameworks.db.page;

import cs601.webmail.frameworks.db.page.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyuan on 10/29/14.
 */
public class PageRequest {

    public static final int DEFAULT_PAGE_SIZE = 50;

    public int page = 1;

    public int pageSize = DEFAULT_PAGE_SIZE;

    public List<Order> orders = new ArrayList<Order>();

    public PageRequest() {
    }

    public PageRequest(Order... orders) {
        if (orders.length > 0) {
            for (int i = 0; i < orders.length; i++) {
                this.orders.add(orders[i]);
            }
        }
    }
}

