package com.zeeyeh.jobscraft.database;

import com.zaxxer.hikari.HikariDataSource;
import com.zeeyeh.devtoolkit.config.Configuration;
import com.zeeyeh.jobscraft.JobsCraft;
import com.zeeyeh.jobscraft.entity.JobsCraftTableNames;

import javax.sql.DataSource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SimpleDatabase {
    private String datatype;
    private Configuration dataConfig;
    private URLClassLoader classLoader;

    public SimpleDatabase() {
        String saveType = JobsCraft.getConfigManager().getDefaultConfig().getString("saveType");
        if (saveType.equalsIgnoreCase("database")) {
            List<File> libraries = JobsCraft.getInstance().getLibraries();
            URL[] urls = new URL[libraries.size()];
            try {
                for (int i = 0; i < libraries.size(); i++) {
                    urls[i] = libraries.get(i).toURI().toURL();
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            classLoader = new URLClassLoader(urls);
            initialize();
        }
    }

    public void initialize() {
        this.dataConfig = JobsCraft.getConfigManager().getDefaultConfig();
        this.datatype = getDataConfig().getString("datatype");
        initTables();
    }

    public DataSource getDataSource(String datatype, String urlExtraParameters) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        if (urlExtraParameters == null) {
            urlExtraParameters = "";
        }
        try {
            //int maxActive = getDataConfig().getIntValue("database.maxActive");
            //druidDataSource.setMaxActive(maxActive);
            //long maxWait = getDataConfig().getLongValue("database.maxWait");
            //druidDataSource.setMaxWait(maxWait);
            //int minIdle = getDataConfig().getIntValue("database.minIdle");
            //druidDataSource.setMinIdle(minIdle);
            hikariDataSource.setMinimumIdle(10);
            hikariDataSource.setMaxLifetime(600000);
            hikariDataSource.setMaxLifetime(30000);
            hikariDataSource.setMaxLifetime(30000);
            hikariDataSource.setConnectionTestQuery("SELECT 1");
            int poolSize = getDataConfig().getIntValue("database.pool-size");
            hikariDataSource.setMaximumPoolSize(poolSize);
            String username = getDataConfig().getString(ofPath("username"));
            //druidDataSource.setUsername(username);
            hikariDataSource.setUsername(username);
            String password = getDataConfig().getString(ofPath("password"));
            hikariDataSource.setPassword(password);
            switch (datatype) {
                case "mysql":
                    String mysqlHost = getDataConfig().getString(ofPath("host"));
                    Configuration dataConfig1 = getDataConfig();
                    int mysqlPort = dataConfig1.getInteger(ofPath("port"));
                    String mysqlBasename = getDataConfig().getString(ofPath("basename"));
                    //MysqlDataSource mysqlDataSource = new MysqlDataSource();
                    //mysqlDataSource.setUrl();
                    //mysqlDataSource.setUser(username);
                    //mysqlDataSource.setPassword(password);
                    //mysqlDataSource.setServerName(mysqlHost);
                    //mysqlDataSource.setDatabaseName(mysqlBasename);
                    //mysqlDataSource.setPort(mysqlPort);
                    hikariDataSource.setJdbcUrl(
                            "jdbc:mysql://" +
                                    mysqlHost +
                                    ":" +
                                    mysqlPort +
                                    "/" +
                                    mysqlBasename +
                                    "?useSSL=false&serverTimezone=Asia/Shanghai" +
                                    urlExtraParameters);
                    //dataSource = mysqlDataSource;
                    Class.forName("com.mysql.cj.jdbc.Driver", true, getClassLoader());
                    hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    //com.mysql.cj.jdbc.Driver mysqlDriverInstance = (com.mysql.cj.jdbc.Driver) Class.forName("com.mysql.cj.jdbc.Driver", true, getClassLoader()).getDeclaredConstructor().newInstance();
                    //druidDataSource.setDriver(mysqlDriverInstance);
                    break;
                case "sqlite":
                    String path = getDataConfig().getString(ofPath("path"));
                    //DbUtil
                    String sqliteUrl = "jdbc:sqlite://" +
                            path +
                            "?useSSL=false&serverTimezone=Asia/Shanghai" +
                            urlExtraParameters;
                    hikariDataSource.setJdbcUrl(sqliteUrl);
                    //SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
                    //sqLiteDataSource.setUrl(sqliteUrl);
                    Class.forName("org.sqlite.JDBC", true, getClassLoader());
                    hikariDataSource.setDriverClassName("org.sqlite.JDBC");
                    //org.sqlite.JDBC sqliteDriverInstance = (org.sqlite.JDBC) Class.forName("org.sqlite.JDBC", true, getClassLoader()).getDeclaredConstructor().newInstance();
                    //druidDataSource.setDriver(sqliteDriverInstance);
                    Connection connection = DriverManager.getConnection(sqliteUrl, username, password);
                    connection.close();
                    break;
                case "postgresql":
                    String postgresqlHost = getDataConfig().getString(ofPath("host"));
                    int postgresqlPort = getDataConfig().getIntValue(ofPath("port"));
                    String postgresqlBasename = getDataConfig().getString(ofPath("basename"));
                    hikariDataSource.setJdbcUrl("jdbc:postgresql://" +
                            postgresqlHost +
                            ":" +
                            postgresqlPort +
                            "/" +
                            postgresqlBasename +
                            "?useSSL=false&serverTimezone=Asia/Shanghai" +
                            urlExtraParameters);
                    Class.forName("org.postgresql.Driver", true, getClassLoader());
                    hikariDataSource.setDriverClassName("org.postgresql.Driver");
                    //org.postgresql.Driver postgresqlDriverInstance = (org.postgresql.Driver) Class.forName("org.postgresql.Driver", true, getClassLoader()).getDeclaredConstructor().newInstance();
                    //druidDataSource.setDriver(postgresqlDriverInstance);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hikariDataSource;
    }

    public void reload() {
        this.dataConfig = JobsCraft.getConfigManager().getDefaultConfig();
        this.datatype = getDataConfig().getString("datatype");
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public Configuration getDataConfig() {
        return dataConfig;
    }

    public String getDatatype() {
        return datatype;
    }

    public String ofPath(String key) {
        return "database." + getDatatype() + "." + key;
    }

    public void initTables() {
        String job = "CREATE TABLE IF NOT EXISTS `" + JobsCraftTableNames.JOB.getName() + "`  (\n" +
                "  `id` bigint NOT NULL,\n" +
                "  `name` varchar(20) NOT NULL,\n" +
                "  `title` varchar(50) NOT NULL,\n" +
                "  `players` mediumtext NOT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");";
        String jobLevel = "CREATE TABLE IF NOT EXISTS `" + JobsCraftTableNames.JOB_LEVEL.getName() + "`  (\n" +
                "  `id` bigint NOT NULL,\n" +
                "  `name` varchar(20) NULL,\n" +
                "  `title` varchar(50) NULL,\n" +
                "  `players` mediumtext NULL,\n" +
                "  `maxExp` bigint NULL,\n" +
                "  `nextId` bigint NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");";
        String jobCurtail = "CREATE TABLE IF NOT EXISTS `" + JobsCraftTableNames.JOB_CURTAIL.getName() + "`  (\n" +
                "  `id` bigint NOT NULL,\n" +
                "  `name` varchar(20) NULL,\n" +
                "  `jobId` bigint NULL,\n" +
                "  `levelId` bigint NULL,\n" +
                "  `tools` mediumtext NULL,\n" +
                "  `foods` mediumtext NULL,\n" +
                "  `places` mediumtext NULL,\n" +
                "  `destructs` mediumtext NULL,\n" +
                "  `recipes` mediumtext NULL,\n" +
                "  `interacts` mediumtext NULL,\n" +
                "  `attacks` mediumtext NULL,\n" +
                "  `buffs` mediumtext NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");";
        try {
            DataSource dataSource = getDataSource(getDatatype(), null);
            Connection connection = dataSource.getConnection();
            PreparedStatement jobStatement = connection.prepareStatement(job);
            jobStatement.executeUpdate();
            PreparedStatement jobLevelStatement = connection.prepareStatement(jobLevel);
            jobLevelStatement.executeUpdate();
            PreparedStatement jobCurtailStatement = connection.prepareStatement(jobCurtail);
            jobCurtailStatement.executeUpdate();
            //List<Entity> jobTables = Db.use(dataSource).query("SHOW TABLES LIKE '" + JobsCraftTableNames.JOB.getName() + "'");
            //if (jobTables.size() == 0) {
            //    Db.use(dataSource).execute(job);
            //}
            //List<Entity> jobLevelTables = Db.use(dataSource).query("SHOW TABLES LIKE '" + JobsCraftTableNames.JOB_LEVEL.getName() + "'");
            //if (jobLevelTables.size() == 0) {
            //    Db.use(dataSource).execute(jobLevel);
            //}
            //List<Entity> jobCurtailTables = Db.use(dataSource).query("SHOW TABLES LIKE '" + JobsCraftTableNames.JOB_CURTAIL.getName() + "'");
            //if (jobCurtailTables.size() == 0) {
            //    Db.use(dataSource).execute(jobCurtail);
            //}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
