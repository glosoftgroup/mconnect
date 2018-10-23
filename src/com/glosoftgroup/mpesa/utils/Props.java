package com.glosoftgroup.mpesa.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.NoSuchPaddingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings({"FinalClass", "ClassWithoutLogger"})
public final class Props {
    /**
     * The properties file.
     */
    private static final String PROPS_FILE = "conf/props.json";
    /**
     * A list of any errors that occurred while loading the properties.
     */
    private transient List<String> loadErrors;
    /**
     * Info log level. Default = INFO.
     */
    private transient String infoLogLevel = "INFO";
    /**
     * Error log level. Default = FATAL.
     */
    private transient String errorLogLevel = "ERROR";
    /**
     * Info log file name.
     */
    private transient String infoLogFile;
    /**
     * Error log file name.
     */
    private transient String errorLogFile;
    /**
     * Database connection pool name.
     */
    private String dbPoolName;
    /**
     * Database user name.
     */
    private String dbUserName;
    /**
     * Database password.
     */
    private String dbPassword;
    /**
     * Database port.
     */
    private String dbPort;
    /**
     * Database host.
     */
    private String dbHost;
    /**
     * Database name.
     */
    private String dbName;
    
    /* URLS */
    private String newPaymentTransactionUrl;
    private String transactionStatusCallbackURL;
    
    /* Logger */
    private Logging log;


    /**
     * Constructor.
     *
     * @throws NoSuchAlgorithmException on error
     * @throws NoSuchPaddingException on error
     */
    public Props() throws NoSuchAlgorithmException, NoSuchPaddingException {
        System.out.println("" + System.getProperty("user.dir"));
        this.loadErrors = new ArrayList<String>(0);
        log = new Logging();
        loadProperties(PROPS_FILE);
    }

    /**
     * Load system properties.
     *
     * @param propsFile the system properties XML file
     */
    @SuppressWarnings({
        "UseOfSystemOutOrSystemErr",
        "null",
        "ConstantConditions"
    })
    private void loadProperties(final String propsFile) {
        
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader(propsFile))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject configObject = (JSONObject) obj;
            
            String error1 = "ERROR: %s is <= 0 or may not have been set";
            String error2 = "ERROR: %s may not have been set";


            infoLogLevel = configObject.get("InfoLogLevel").toString();
            if (getInfoLogLevel().isEmpty()) {
                getLoadErrors().add(String.format(error2, "InfoLogLevel"));
            }

            errorLogLevel = configObject.get("ErrorLogLevel").toString();;
            if (getErrorLogLevel().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ErrorLogLevel"));
            }
            
            infoLogFile = configObject.get("InfoLogFile").toString();
            if (getInfoLogFile().isEmpty()) {
                getLoadErrors().add(String.format(error2, "InfoLogFile"));
            }

            errorLogFile = configObject.get("ErrorLogFile").toString();
            if (getErrorLogFile().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ErrorLogFile"));
            }
            
            JSONObject dbconfigObject = (JSONObject) configObject.get("database");
            
            dbPoolName = dbconfigObject.get("DbPoolName").toString();
            if (getDbPoolName().isEmpty()) {
                getLoadErrors().add(String.format(error2, "DbPoolName"));
            }

            dbUserName = dbconfigObject.get("DbUserName").toString();
            if (getDbUserName().isEmpty()) {
                getLoadErrors().add(String.format(error2, "DbUserName"));
            }

            dbPassword = dbconfigObject.get("DbPassword").toString();
            if (getDbPassword().isEmpty()) {
                getLoadErrors().add(String.format(error2, "DbPassword"));
            }

            dbHost = dbconfigObject.get("DbHost").toString();
            if (getDbHost().isEmpty()) {
                getLoadErrors().add(String.format(error2, "DbHost"));
            }
            
            dbPort = dbconfigObject.get("DbPort").toString();
            if (getDbPort().isEmpty()) {
                getLoadErrors().add(String.format(error2, "DbPort"));
            }


            dbName = dbconfigObject.get("DbName").toString();
            if (getDbName().isEmpty()) {
                getLoadErrors().add(String.format(error2, "DbName"));
            }
            
            JSONObject urlconfigObject = (JSONObject) configObject.get("urls");
            
            newPaymentTransactionUrl = urlconfigObject.get("newPaymentTransactionUrl").toString();
            if (getNewPaymentTransactionUrl().isEmpty()) {
                getLoadErrors().add(String.format(error2, "newPaymentTransactionUrl"));
            }


            transactionStatusCallbackURL = urlconfigObject.get("transactionStatusCallbackURL").toString();
            if (getTransactionStatusCallbackURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "transactionStatusCallbackURL"));
            }
            
             
        } catch (FileNotFoundException e) {
            log.error("File not found", e);
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IOException", e);
            e.printStackTrace();
        } catch (ParseException e) {
            log.error("ParseException", e);
            e.printStackTrace();
        }
    }

    /**
     * Info log level. Default = INFO.
     *
     * @return the infoLogLevel
     */
    public String getInfoLogLevel() {
        return infoLogLevel;
    }

    /**
     * Error log level. Default = FATAL.
     *
     * @return the errorLogLevel
     */
    public String getErrorLogLevel() {
        return errorLogLevel;
    }

    /**
     * Info log file name.
     *
     * @return the infoLogFile
     */
    public String getInfoLogFile() {
        return infoLogFile;
    }

    /**
     * Error log file name.
     *
     * @return the errorLogFile
     */
    public String getErrorLogFile() {
        return errorLogFile;
    }

    /**
     * Gets the New Payments API URL.
     *
     * @return New Payments API URL
     */
    public String getNewPaymentTransactionUrl() {
        return newPaymentTransactionUrl;
    }
    
    /**
     * Gets the Transaction CallBack API URL.
     *
     * @return Transaction CallBack API URL
     */
    public String getTransactionStatusCallbackURL() {
        return transactionStatusCallbackURL;
    }

    

    /**
     * A list of any errors that occurred while loading the properties.
     *
     * @return the loadErrors
     */
    public List<String> getLoadErrors() {
        try {
            Collections.unmodifiableList(loadErrors);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableList(loadErrors);
    }

    /**
     * Contains the name of the database pool.
     *
     * @return the name of the database pool
     */
    public String getDbPoolName() {
        return dbPoolName;
    }

    /**
     * Contains the name of the database user.
     *
     * @return the name of the database user
     */
    public String getDbUserName() {
        return dbUserName;
    }

    /**
     * Contains the name of the database password.
     *
     * @return the name of the database password
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Contains the name of the database host.
     *
     * @return the name of the database host
     */
    public String getDbHost() {
        return dbHost;
    }
    
    /**
     * Contains the name of the database port.
     *
     * @return the name of the database port
     */
    public String getDbPort() {
        return dbPort;
    }

    /**
     * Contains the name of the database.
     *
     * @return the name of the database
     */
    public String getDbName() {
        return dbName;
    }
     
}