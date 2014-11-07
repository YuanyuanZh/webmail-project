package cs601.webmail.dao.impl;

import cs601.webmail.dao.BaseDao;
import cs601.webmail.dao.DaoException;
import cs601.webmail.dao.MailDao;
import cs601.webmail.frameworks.db.*;
import cs601.webmail.entity.Mail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanyuan on 10/29/14.
 */
public class MailDaojdbcImpl extends BaseDao implements MailDao {

    public Mail findById(Long id) {

        QueryRunner qr=getQueryRunner();


        try{

            return qr.query("select * from emails where MSGID=?", new ResultSetHandler<Mail>() {
                @Override
                public Mail handle(ResultSet rs) throws SQLException {
                    if (rs.next()){
                        return handleRowMapping(rs);
                    }
                    return null;
                }
            },new Object[]{id});


        }catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public Mail findByUID(String uid){
        QueryRunner qr=getQueryRunner();
        try{
            return qr.query("select * from emails where UID=?",new ResultSetHandler<Mail>() {
                @Override
                public Mail handle(ResultSet rs) throws SQLException {
                    if(rs.next()){
                        return handleRowMapping(rs);
                    }
                    return null;
                }
            },new Object[]{uid});

        }catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public List<Mail> findAll(Long accountid,Long userid){
        QueryRunner qr=getQueryRunner();
        try{
            return qr.query("select * from emails where ACCOUNTID=? and USERSID=?",new ResultSetHandler<List<Mail>>() {
                @Override
                public List<Mail> handle(ResultSet rs) throws SQLException {
                    List<Mail> mails=new ArrayList<Mail>();
                    if(rs.next()){
                        Mail mail= handleRowMapping(rs);
                        mails.add(mail);
                    }
                    return null;
                }
            },new Object[]{accountid,userid});

        }catch (SQLException e){
            throw new DaoException(e);
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

        QueryRunner qr=getQueryRunner();

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

            List<Mail> mails = qr.query(sb.toString(), new ResultSetHandler<List<Mail>>() {
                @Override
                public List<Mail> handle(ResultSet rs) throws SQLException {

                    List<Mail> mails = new ArrayList<Mail>();

                    while (rs.next()) {
                        Mail mail = handleRowMapping(rs);
                        mails.add(mail);
                    }

                    return mails;
                }
            });

            Page<Mail> pageResult = new Page<Mail>();

            int rows = count("emails");

            pageResult.setPosition(position);
            pageResult.setPageSize(step);
            pageResult.setPageList(mails);
            pageResult.setTotal(rows);

            return pageResult;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    //Page<Mail> findByPage(PageRequest pageRequest, Long accountId, Long userId);

    public Mail save(Mail mail){
        if(mail==null){
            throw new IllegalArgumentException();}

        QueryRunner qr=getQueryRunner();
        try{
            int rows= qr.update("insert into emails" +
                    "(SUBJECT, MFROM, MTO, CONTENT, DATE" +
                    ", USERSID, ACCOUNTID, MESSAGE_ID, CONTENT_TYPE, UID" +
                    ", FLAG_NEW, FLAG_UNREAD, FLAG_FAV)" +
                    " values (?, ?, ?, ?, ?,  ?, ?, ?, ?, ?,  ?, ?, ?)"
                    ,new Object[]{mail.getSubject(),mail.getFrom(),mail.getTo(),mail.getBody(),mail.getDate(),mail.getUserId()
                     ,mail.getAccountId(),mail.getMessageId(),mail.getContentType(),mail.getUid(),mail.getFlagNew(),mail.getFlagFav()});
            if (rows != 1) {
                throw new IllegalStateException("Save entity failed.");
            }
        }catch (SQLException e){
            throw new DaoException(e);
        }
      return mail;
    }

    // UID
    public List<String> findAllMailUIDs(){
        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select MSGID,UID from emails", new ResultSetHandler<List<String>>() {
                @Override
                public List<String> handle(ResultSet rs) throws SQLException {
                    List<String> lines = new ArrayList<String>();

                    while (rs.next()) {
                        lines.add(String.format("%d %s", rs.getLong("MSGID"), rs.getString("UID")));
                    }

                    return lines;
                }
            });
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    };

//    void insertMail(String MSGID,String SUBJECT,String MFROM,String MTO,String CONTENT,String DATE,Integer uid,Integer aid);

    public void removeByUID(String uid){

        QueryRunner qr = getQueryRunner();

        try {
            int rows = qr.update("delete from emails where UID=?", new Object[]{uid});

            if (rows == 0) {
                throw new IllegalStateException("Operation failed.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    };


