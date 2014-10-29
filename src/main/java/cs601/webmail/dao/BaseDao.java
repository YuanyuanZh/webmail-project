package cs601.webmail.dao;


import java.sql.Connection;
import java.sql.DriverManager;


/**
 * Created by yuanyuan on 10/27/14.
 */
public abstract class BaseDao{
    private Connection c = null;

    public void connectToDB(){
        try {

            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:webmail.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }


    }

   /* private QueryRunner _queryRunner;*/

    /**
     * @deprecated  user EntityManager instead
     */
    //@Deprecated
    /*protected QueryRunner getQueryRunner() {
        if (_queryRunner  == null) {

            RequestContext context = RequestContext.getCurrentInstance();

            if (context == null) {
                SQLiteDataSource ds = new SQLiteDataSource();
                ds.setUrl("jdbc:sqlite:webmail.db");
                _queryRunner = new QueryRunner(ds);
            }
            else {
                // TODO need to add context support, not this mock block
                SQLiteDataSource ds = new SQLiteDataSource();
                ds.setUrl("jdbc:sqlite:webmail.db");
                _queryRunner = new QueryRunner(ds);
                System.out.println("[WARNING] no request context found");
            }
        }
        return _queryRunner;
    }*/




}