package cs601.webmail.dao.impl;

import cs601.webmail.Constants;
import cs601.webmail.dao.BaseDao;
import cs601.webmail.dao.DaoException;
import cs601.webmail.dao.MailDao;
import cs601.webmail.entity.Mail;
import cs601.webmail.db.Page;
import cs601.webmail.db.Order;
import cs601.webmail.db.PageRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanyuan on 10/29/14.
 */
public class MailDaojdbcImpl extends BaseDao implements MailDao {

    public Mail findByAId(Long msgid) {
        Connection conn =getConnection();
        PreparedStatement statement =null;
        ResultSet rs =null;

        try{
            statement =conn.prepareStatement("select * from emails where MSGID=?");
            statement.setLong(1,msgid);
            rs =statement.executeQuery();
        }catch (SQLException e){
            throw new DaoException(e);
        }finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

        return  null;
    }

    public List<Mail> findAll(){
        Connection conn =getConnection();
        PreparedStatement statement=null;
        ResultSet rs =null;
        try{
            statement =conn.prepareStatement("select * from emails");
            rs = statement.executeQuery();

            List<Mail> mails = new ArrayList<Mail>();

            while (rs.next()) {
                Mail mail = handleRowMapping(rs);
                mails.add(mail);
            }

            return mails;
        }catch(SQLException e){
            throw new DaoException(e);
        }finally {
            closeResultSetQuietly(rs);
            closeStatementQuietly(statement);
        }



    }
    private Mail handleRowMapping(ResultSet rs) throws SQLException {
        Mail mail = new Mail();

        mail.setId(rs.getLong("MSGID"));
        mail.setSubject(rs.getString("SUBJECT"));
        mail.setFrom(rs.getString("MFROM"));
        mail.setTo(rs.getString("MTO"));
        mail.setBody(rs.getString("CONTENT"));
        mail.setDate(rs.getString("DATE"));
        mail.setUserId(rs.getLong("USERSID"));
        mail.setAccountId(rs.getLong("ACCOUNTID"));
        mail.setMessageId(rs.getString("MESSAGE_ID"));
        mail.setContentType(rs.getString("CONTENT_TYPE"));
        mail.setUid(rs.getString("UID"));
        mail.setFlagNew(rs.getInt("FLAG_NEW"));
        mail.setFlagUnread(rs.getInt("FLAG_UNREAD"));
        mail.setFlagFav(rs.getInt("FLAG_FAV"));

        return mail;
    }
    @Override
    public Page<Mail> findByPage(PageRequest pageRequest, Long accountId, Long userId) {

        if (pageRequest == null) {
            throw new IllegalArgumentException("pageRequest missed");
        }

        int position = (pageRequest.page - 1) * pageRequest.pageSize;
        int step = pageRequest.pageSize;

        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {

            StringBuilder sb = new StringBuilder("select * from emails");

            if (pageRequest.orders != null && pageRequest.orders.size() > 0) {

                sb.append(" order by ");

                for (Iterator<Order> itr = pageRequest.orders.iterator(); itr.hasNext();) {

                    sb.append(itr.next());

                    if (itr.hasNext()) {
                        sb.append(",");
                    }
                }
            }

            sb.append(String.format(" limit %d,%d", position, step));

            if (Constants.DEBUG_MODE)
                System.out.println("[DEBUG] execute sql > " + sb.toString());

            statement = conn.prepareStatement(sb.toString());

            rs = statement.executeQuery();

            List<Mail> mails = new ArrayList<Mail>();

            while (rs.next()) {
                Mail mail = handleRowMapping(rs);
                mails.add(mail);
            }

            Page<Mail> pageResult = new Page<Mail>();

            int rows = count("emails");

            pageResult.setPosition(position);
            pageResult.setPageSize(step);
            pageResult.setPageList(mails);
            pageResult.setTotal(rows);

            return pageResult;

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }
    }


    //Page<Mail> findByPage(PageRequest pageRequest, Long accountId, Long userId);

    public Mail save(Mail mail){
        if(mail==null){
            throw new IllegalArgumentException();
        }
        Connection conn=getConnection();
        PreparedStatement statement =null;
        ResultSet rs =null;

        try{
            statement=conn.prepareStatement("insert into emails" +
                    "(SUBJECT, MFROM, MTO, CONTENT, DATE" +
                    ", USERSID, ACCOUNTID, MESSAGE_ID, CONTENT_TYPE, UID" +
                    ", FLAG_NEW, FLAG_UNREAD, FLAG_FAV)" +
                    " values (?, ?, ?, ?, ?,  ?, ?, ?, ?, ?,  ?, ?, ?)");
            statement.setString(1, mail.getSubject());
            statement.setString(2, mail.getFrom());
            statement.setString(3, mail.getTo());
            statement.setString(4, mail.getBody());
            statement.setString(5, mail.getDate());

            statement.setLong(6, mail.getUserId());
            statement.setLong(7, mail.getAccountId());
            statement.setString(8, mail.getMessageId());
            statement.setString(9, mail.getContentType());
            statement.setString(10, mail.getUid());

            statement.setInt(11, mail.getFlagNew());
            statement.setInt(12, mail.getFlagUnread());
            statement.setInt(13, mail.getFlagFav());

            int rows = statement.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Save entity failed.");
            }
        }catch(SQLException e){
            throw new DaoException();
        }finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);

        }
      return mail;
    }

    // UID
    public List<String> findAllMailUIDs(){
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = conn.prepareStatement("select MSGID,UID from emails");

            rs = statement.executeQuery();

            List<String> lines = new ArrayList<String>();

            while (rs.next()) {
                lines.add(String.format("%d %s", rs.getLong("MSGID"), rs.getString("UID")));
            }

            return lines;

        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }

    };

//    void insertMail(String MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid);

    public void removeByUID(String uid){

        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = conn.prepareStatement("delete from emails where UID=?");

            statement.setString(1, uid);

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new IllegalStateException("Operation failed.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
        }
    }

    };


