package org.notificationengine.probes.probe;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.notificationengine.probes.constants.Constants;
import org.notificationengine.probes.spring.SpringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class DatabaseProbe extends Probe{

    public static Logger LOGGER = Logger.getLogger(DatabaseProbe.class);

    private String user;

    private String password;

    private String url;

    private String driverClassName;

    private Collection<String> queries;

    private JdbcTemplate jdbcTemplate;

    public DatabaseProbe() {

        super();
        this.queries = new HashSet<>();

        this.setLastTryTime(new Timestamp(new Date().getTime()));

        DataSource dataSource = (DataSource) SpringUtils.getBean("dataSource");

        this.setDataSource(dataSource);

    }

    public DatabaseProbe(String user, String password, String url, String driverClassName, Collection<String> queries) {

        super();
        this.user = user;
        this.password = password;
        this.url = url;
        this.driverClassName = driverClassName;
        this.queries = queries;

        this.setLastTryTime(new Timestamp(new Date().getTime()));

        DataSource dataSource = (DataSource) SpringUtils.getBean("dataSource");

        this.setDataSource(dataSource);

    }

    public DatabaseProbe(String topicName, Map<String, Object> options) {

        super();

        this.setTopicName(topicName);

        this.user = (String) options.get(Constants.USER);
        this.password = (String) options.get(Constants.PASSWORD);
        this.url = (String) options.get(Constants.DATABASE_URL);
        this.driverClassName = (String) options.get(Constants.DRIVER_CLASS_NAME);

        this.setLastTryTime(new Timestamp(new Date().getTime()));

        this.queries = new HashSet<>();

        String queriesMapJson = (String) options.get(Constants.QUERIES);

        Object queriesObj = JSONValue.parse(queriesMapJson);

        JSONArray queriesJsonArray = (JSONArray)queriesObj;

        for(int i = 0; i<queriesJsonArray.size(); i++) {

            JSONObject queryObj = (JSONObject) queriesJsonArray.get(i);

            Collection<String> queries = queryObj.values();

            for(String query : queries) {

                this.queries.add(query);
            }

        }

        DataSource dataSource = (DataSource) SpringUtils.getBean("dataSource");

        this.setDataSource(dataSource);

    }

    @Override
    public void listen() {

        LOGGER.info("DatabaseProbe is listenning");

        Timestamp time = new Timestamp(new Date().getTime());

        for (String query : this.queries) {

            LOGGER.info("Query " + query + " is executed");

            Collection<JSONObject> results = this.jdbcTemplate.query(
                query,
                new Object[]{this.getLastTryTime().toString()},
                new RowMapper<JSONObject>() {

                    public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {

                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnsNumber = rsmd.getColumnCount();

                        JSONObject toRespond = new JSONObject();

                        for (int i = 1; i <= columnsNumber; i++) {

                            toRespond.put(rsmd.getColumnName(i), rs.getObject(i).toString());

                        }

                        LOGGER.debug("There is a response: " + toRespond);

                        return toRespond;

                    }
                }
            );

            for(JSONObject result : results) {

                this.setNotificationContext(result);

                this.sendNotification();

            }

        }

        this.setLastTryTime(time);

    }

    public void setDataSource(DataSource dataSource) {

        BasicDataSource configuredDataSource = (BasicDataSource)dataSource;
        configuredDataSource.setDriverClassName(this.driverClassName);
        configuredDataSource.setUsername(this.user);
        configuredDataSource.setPassword(this.password);
        configuredDataSource.setUrl(this.url);

        this.jdbcTemplate = new JdbcTemplate(configuredDataSource);

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Collection<String> getQueries() {
        return queries;
    }

    public void setQueries(Collection<String> queries) {
        this.queries = queries;
    }

    public void addQuery(String query) {
        this.queries.add(query);
    }

    public void removeQuery(String query) {
        this.queries.remove(query);
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
