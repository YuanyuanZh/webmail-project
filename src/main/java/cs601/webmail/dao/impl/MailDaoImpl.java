package cs601.webmail.dao.impl;

import cs601.webmail.dao.BaseDao;
import cs601.webmail.dao.DaoException;
import cs601.webmail.dao.MailDao;
import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.db.DBUtils;
import cs601.webmail.frameworks.db.QueryRunner;
import cs601.webmail.frameworks.db.ResultSetHandler;
import cs601.webmail.frameworks.db.page.Order;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;
import cs601.webmail.util.Strings;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanyuan on 10/29/14.
 */
public class MailDaoImpl extends BaseDao implements MailDao {

    /**
     * @param id PK
     * @return Mail entity if found. otherwise, return NULL
     */
    @Override
    public Mail findById(Long id) {

        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select * from emails where MSGID=?", new ResultSetHandler<Mail>() {
                @Override
                public Mail handle(ResultSet rs) throws SQLException {

                    if (rs.next()) {
                        return handleRowMapping(rs);
                    }

                    return null;
                }
            }, new Object[]{id});

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Mail findByUID(String uid) {

        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select * from emails where UID=?", new ResultSetHandler<Mail>() {
                @Override
                public Mail handle(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        return handleRowMapping(rs);
                    }

                    return null;
                }
            }, new Object[]{uid});

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Deprecated
    public List<Mail> findAll() {

        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select * from emails", new ResultSetHandler<List<Mail>>() {
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
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Mail handleRowMapping(ResultSet rs) throws SQLException {
        Mail mail = new Mail();

        mail.setId(rs.getLong("MSGID"));
        mail.setSubject(rs.getString("SUBJECT"));
        mail.setFrom(rs.getString("MFROM"));
        mail.setTo(rs.getString("MTO"));
        mail.setContent(rs.getString("CONTENT"));
        mail.setDate(rs.getString("DATE"));
        mail.setUserId(rs.getLong("USERSID"));
        mail.setAccountId(rs.getLong("ACCOUNTID"));
        mail.setMessageId(rs.getString("MESSAGE_ID"));
        mail.setContentType(rs.getString("CONTENT_TYPE"));
        mail.setUid(rs.getString("UID"));
        mail.setFlagNew(rs.getInt("FLAG_NEW"));
        mail.setFlagUnread(rs.getInt("FLAG_UNREAD"));
        mail.setFlagFav(rs.getInt("FLAG_FAV"));
        mail.setFlagDel(rs.getInt("FLAG_DEL"));
        mail.setOwnerAddress(rs.getString("OWNER_ADDRESS"));
        mail.setFolder(rs.getString("FOLDER"));

        return mail;
    }

    @Override
    public Page<Mail> findByPage(PageRequest pageRequest, Long accountId, Long userId) {

        if (pageRequest == null) {
            throw new IllegalArgumentException("pageRequest missed");
        }

        int position = (pageRequest.page - 1) * pageRequest.pageSize;
        int step = pageRequest.pageSize;

        QueryRunner qr = getQueryRunner();

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

    @Override
    public Mail save(Mail mail) {

        if (mail == null) {
            throw new IllegalArgumentException();
        }

        if (mail.getId() != null) {
            return doUpdate(mail);
        } else {
            return doInsert(mail);
        }
    }

    private Mail doUpdate(Mail mail) {

        QueryRunner qr = new QueryRunner();

        String sql = "update emails set SUBJECT=?,MFROM=?,MTO=?,CONTENT=?,DATE=?," +
                "USERSID=?,ACCOUNTID=?,MESSAGE_ID=?,CONTENT_TYPE=?,UID=?," +
                "FLAG_NEW=?,FLAG_UNREAD=?,FLAG_FAV=?, FLAG_DEL=?," +
                "OWNER_ADDRESS=?, FOLDER=? where MSGID=" + mail.getId();
        Object[] params = new Object[]{
                mail.getSubject(),
                mail.getFrom(),
                mail.getTo(),
                mail.getContent(),
                mail.getDate(),

                mail.getUserId(),
                mail.getAccountId(),
                mail.getMessageId(),
                mail.getContentType(),
                mail.getUid(),

                mail.getFlagNew(),
                mail.getFlagUnread(),
                mail.getFlagFav(),
                mail.getFlagDel(),
                mail.getOwnerAddress(),
                mail.getFolder()
        };

        try {
            qr.update(sql, params);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return mail;
    }

    private Mail doInsert(Mail mail) {
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = conn.prepareStatement("insert into emails" +
                    "(SUBJECT, MFROM, MTO, CONTENT, DATE" +
                    ", USERSID, ACCOUNTID, MESSAGE_ID, CONTENT_TYPE, UID" +
                    ", FLAG_NEW, FLAG_UNREAD, FLAG_FAV, FLAG_DEL, OWNER_ADDRESS, FOLDER)" +
                    " values (?, ?, ?, ?, ?,  ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?)");

            statement.setString(1, mail.getSubject());
            statement.setString(2, mail.getFrom());
            statement.setString(3, mail.getTo());
            statement.setString(4, mail.getContent());
            statement.setString(5, mail.getDate());

            statement.setLong(6, mail.getUserId());
            statement.setLong(7, mail.getAccountId());
            statement.setString(8, mail.getMessageId());
            statement.setString(9, mail.getContentType());
            statement.setString(10, mail.getUid());

            statement.setInt(11, mail.getFlagNew());
            statement.setInt(12, mail.getFlagUnread());
            statement.setInt(13, mail.getFlagFav());
            statement.setInt(14, mail.getFlagDel());
            statement.setString(15, mail.getOwnerAddress());
            statement.setString(16, mail.getFolder());

            int rows = statement.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Save entity failed.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            closeStatementQuietly(statement);
            closeResultSetQuietly(rs);
            DBUtils.closeConnectionQuietly(conn);
        }

        return findByUID(mail.getUid());
    }

   @Deprecated
    public List<String> findAllMailUIDs() {

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
    }

    @Override
    public List<String> findMailUIDs(Long accountId) {
        QueryRunner qr = getQueryRunner();

        String sql = String.format("select MSGID,UID from emails where ACCOUNTID = %d", accountId);

        try {
            return qr.query(sql, new ResultSetHandler<List<String>>() {
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
    }

    @Deprecated
    public void removeByUID(String uid) {

        QueryRunner qr = getQueryRunner();

        try {
            qr.update("delete from emails where UID=?", new Object[]{uid});

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Page<Mail> findPageByConditions(PageRequest pageRequest, String condition) {

        if (pageRequest == null) {
            throw new IllegalArgumentException("pageRequest missed");
        }

        int position = (pageRequest.page - 1) * pageRequest.pageSize;
        int step = pageRequest.pageSize;

        QueryRunner qr = getQueryRunner();

        try {

            StringBuilder sb = new StringBuilder("select * from emails");

            // append conditions
            if (Strings.haveLength(condition)) {
                sb.append(" where ").append(condition);
            }

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

            int rows = count("emails", condition);

            pageResult.setPosition(position);
            pageResult.setPageSize(step);
            pageResult.setPageList(mails);
            pageResult.setTotal(rows);

            return pageResult;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
