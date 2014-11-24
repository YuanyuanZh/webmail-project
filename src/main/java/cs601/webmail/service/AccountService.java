package cs601.webmail.service;

import cs601.webmail.entity.Account;

import java.util.List;

/**
 * Created by yuanyuan on 10/26/14.
 */
public interface AccountService {
    Account findById(Long id);

    /**
     * Find single account for user, cause user could have
     * more than one account by the original design.<p>
     *
     * If results was more than one, will return the first
     * one.
     *
     * @param userId User ID
     * @return Account object.
     * @since 0.3
     */
    Account findSingleByUserId (Long userId);

    void addAccount(Account account);
    List<String> emailAccount(Account account);
    void deleteAccount(Account account);
    boolean verifyAccount(String emailAddress, String pass,String popServer,int popPort);

    void save(Account account);
}
