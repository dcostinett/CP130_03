package edu.uw.danco;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: dcostinett
 * Date: 4/23/13
 * Time: 9:17 PM
 */
public class FileAccountDaoImpl implements AccountDao {
    /** The logger */
    private static final Logger LOGGER = Logger.getLogger(FileAccountDaoImpl.class.getName());

    @Override
    public Account getAccount(String accountName) {
        return null;
    }

    @Override
    public void setAccount(Account account) throws AccountException {

    }

    @Override
    public void deleteAccount(String accountName) throws AccountException {

    }

    @Override
    public void reset() throws AccountException {

    }

    @Override
    public void close() throws AccountException {

    }
}
