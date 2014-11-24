package cs601.webmail.frameworks.db.page;

/**
 * Created by yuanyuan on 10/29/14.
 */
public class Order {

    private static final String DESC = "DESC";
    private static final String ASC = "ASC";

    private String column;

    private String direction;

    @Override
    public String toString() {
        return column + " " + direction;
    }

    public static Order desc(String column) {
        Order order = new Order();
        order.column = column;
        order.direction = DESC;
        return order;
    }

    public static Order asc(String column) {
        Order order = new Order();
        order.column = column;
        order.direction = ASC;
        return order;
    }

}
