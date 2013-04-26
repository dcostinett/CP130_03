package edu.uw.danco.dao;

import com.sun.istack.internal.Nullable;
import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: dcostinett
 * Date: 4/23/13
 * Time: 9:17 PM
 */
public class FileAccountDaoImpl implements AccountDao {
    /** The logger */
    private static final Logger LOGGER = Logger.getLogger(FileAccountDaoImpl.class.getName());

    /** Name of folder in which to save the files */
    private static final String ACCOUNTS_FOLDER = "accounts";

    private static final int BUFF_SIZE = 1024;


    /**
     * Restore an account from a zip file
     * @param accountName - the name of the desired account
     * @return - the account if located, otherwise null
     */
    @Override
    public Account getAccount(String accountName) {
        BeanFactory beanFactory = new FileSystemXmlApplicationContext("context.xml");
        Account acct = null;

        File file = new File(String.format("%s/%s.zip", ACCOUNTS_FOLDER, accountName));
        if (file.exists()) {
            acct = beanFactory.getBean(Account.class);

        }

        return null;
    }


    /**
     * Write out an account to separate files representing the account, address and cc info
     * @param account - the account
     * @throws AccountException - if unable to create the file
     */
    @Override
    public void setAccount(Account account) throws AccountException {
        try {
            FileOutputStream fos = new FileOutputStream(
                    String.format("%s/%s.zip", ACCOUNTS_FOLDER, account.getName()));
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ZipOutputStream zipped = new ZipOutputStream(bos);
            addEntry(zipped, "account", getAccountProperties(account));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to open zip file for " + account.getName(), e);
            throw new AccountException(e);
        }
    }


    /**
     * Deletes a specific account file representation
     * @param accountName
     * @throws AccountException
     */
    @Override
    public void deleteAccount(String accountName) throws AccountException {
        File f = new File(String.format("%s/%s.zip", ACCOUNTS_FOLDER, accountName));
        if (f.exists()) {
            f.delete();
        } else {
            LOGGER.log(Level.SEVERE, "Unable to delete account " + accountName);
            throw new AccountException("Unable to delete account " + accountName);
        }
    }


    /**
     * Deletes all the files in the default store
     * @throws AccountException
     */
    @Override
    public void reset() throws AccountException {
        File dir = new File(ACCOUNTS_FOLDER);
        for (File f : dir.listFiles()) {
            f.delete();
        }
    }

    @Override
    public void close() throws AccountException {
        //no op
    }


    /**
     * Helper method to zip up the account file along with the address & cc info
     * */
    private void addEntry(ZipOutputStream zip, String name, Properties props) throws IOException {
        File f = new File(name);
        props.store(new FileOutputStream(f), name);
        byte[] buff = new byte[BUFF_SIZE];
        int bytes = 0;
        FileInputStream fin = new FileInputStream(String.format("%s/%s", name));
        ZipEntry entry = new ZipEntry(name);
        zip.putNextEntry(entry);
        while ( (bytes = fin.read(buff)) != -1) {
            zip.write(buff, 0, bytes);
        }
        zip.closeEntry();
    }


    /** Get a properties object representing the account information */
    private Properties getAccountProperties(Account account) {
        Properties props = new Properties();
        props.setProperty("account_name", account.getName());
        props.setProperty("password_hash", new String(account.getPasswordHash()));
        props.setProperty("balance", Integer.toString(account.getBalance()));
        props.setProperty("fullname", account.getFullName());
        props.setProperty("phone", account.getPhone());
        props.setProperty("email", account.getEmail());

        return props;
    }


    /** Get a properties object representing the Address information */
    private Properties getAddressProperties(@Nullable Address addr) {
        Properties props = new Properties();
        if (addr != null) {
            props.setProperty("street_address", addr.getStreetAddress());
            props.setProperty("city", addr.getCity());
            props.setProperty("state", addr.getState());
            props.setProperty("zip", addr.getZipCode());
        }

        return props;
    }


    /** Get a properties object representing the CreditCard information */
    private Properties getCreditCardProperties(@Nullable CreditCard cc) {
        Properties props = new Properties();
        if (cc != null) {
            props.setProperty("card_number", cc.getAccountNumber());
            props.setProperty("issuer", cc.getIssuer());
            props.setProperty("cardtype", cc.getType());
            props.setProperty("holder", cc.getHolder());
            props.setProperty("expires", cc.getExpirationDate());
        }

        return props;
    }
}
