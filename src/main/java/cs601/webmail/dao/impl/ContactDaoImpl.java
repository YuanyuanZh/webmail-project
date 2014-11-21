package cs601.webmail.dao.impl;

import cs601.webmail.dao.BaseDao;
import cs601.webmail.dao.ContactDao;
import cs601.webmail.dao.DaoException;
import cs601.webmail.entity.Contact;
import cs601.webmail.frameworks.db.QueryRunner;
import cs601.webmail.frameworks.db.ResultSetHandler;
import cs601.webmail.util.Strings;
import cs601.webmail.frameworks.db.page.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanyuan on 11/19/14.
 */
public class ContactDaoImpl extends BaseDao implements ContactDao {

    @Override
    public Contact findById(Long id) {
        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select * from contacts where ID=?", new ResultSetHandler<Contact>() {
                @Override
                public Contact handle(ResultSet rs) throws SQLException {

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
    public List<Contact> findAll(Long userId) {
        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select * from contacts where user_id=?", new ResultSetHandler<List<Contact>>() {
                @Override
                public List<Contact> handle(ResultSet rs) throws SQLException {
                    List<Contact> list = new ArrayList<Contact>();

                    while (rs.next()) {
                        Contact c = handleRowMapping(rs);
                        list.add(c);
                    }

                    return list;
                }
            }, new Object[]{userId});
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Contact> findAll() {

        QueryRunner qr = getQueryRunner();

        try {
            return qr.query("select * from contacts", new ResultSetHandler<List<Contact>>() {
                @Override
                public List<Contact> handle(ResultSet rs) throws SQLException {
                    List<Contact> list = new ArrayList<Contact>();

                    while (rs.next()) {
                        Contact c = handleRowMapping(rs);
                        list.add(c);
                    }

                    return list;
                }
            });
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Page<Contact> findPageByConditions(PageRequest pageRequest, String condition) {
        if (pageRequest == null) {
            throw new IllegalArgumentException("pageRequest missed");
        }

        int position = (pageRequest.page - 1) * pageRequest.pageSize;
        int step = pageRequest.pageSize;

        QueryRunner qr = getQueryRunner();

        try {

            StringBuilder sb = new StringBuilder("select * from contacts");

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

            List<Contact> items = qr.query(sb.toString(), new ResultSetHandler<List<Contact>>() {
                @Override
                public List<Contact> handle(ResultSet rs) throws SQLException {

                    List<Contact> items = new ArrayList<Contact>();

                    while (rs.next()) {
                        Contact c = handleRowMapping(rs);
                        items.add(c);
                    }

                    return items;
                }
            });

            Page<Contact> pageResult = new Page<Contact>();

            int rows = count("contacts", condition);

            pageResult.setPosition(position);
            pageResult.setPageSize(step);
            pageResult.setPageList(items);
            pageResult.setTotal(rows);

            return pageResult;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Contact save(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException();
        }

        if (contact.getId() != null) {
            return doUpdate(contact);
        } else {
            return doInsert(contact);
        }
    }

    private Contact doInsert(Contact contact) {

        QueryRunner qr = new QueryRunner();

        String sql = "insert into contacts(user_id,email,full_name,address," +
                "zipcode,phone,flag_fav,flag_del) values(?,?,?,?, ?,?,?,?)";

        Object[] params = new Object[]{
                contact.getUserId(),
                contact.getEmail(),
                contact.getFullName(),
                contact.getAddress(),
                contact.getZipcode(),
                contact.getPhone(),

                contact.getFlagFav(),
                contact.getFlagDel()
        };

        try {
            qr.update(sql, params);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return null;
    }

    private Contact doUpdate(Contact contact) {

        QueryRunner qr = new QueryRunner();

        String sql = "update contacts set user_id=?,email=?,full_name=?,address=?," +
                "zipcode=?,phone=?,flag_fav=?,flag_del=? where id=" + contact.getId();

        Object[] params = new Object[]{
                contact.getUserId(),
                contact.getEmail(),
                contact.getFullName(),
                contact.getAddress(),
                contact.getZipcode(),
                contact.getPhone(),

                contact.getFlagFav(),
                contact.getFlagDel()
        };

        try {
            qr.update(sql, params);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return contact;
    }

    private Contact handleRowMapping(ResultSet rs) throws SQLException {
        Contact entity = new Contact();

        entity.setId(rs.getLong("id"));
        entity.setUserId(rs.getLong("user_id"));
        entity.setEmail(rs.getString("email"));
        entity.setFullName(rs.getString("full_name"));
        entity.setAddress(rs.getString("address"));
        entity.setZipcode(rs.getString("zipcode"));
        entity.setPhone(rs.getString("phone"));
        entity.setFlagFav(rs.getInt("flag_fav"));
        entity.setFlagDel(rs.getInt("flag_del"));
        entity.setAvatar(rs.getString("avatar"));

        return entity;
    }

}


