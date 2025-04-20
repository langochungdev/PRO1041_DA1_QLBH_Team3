package Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcHelper {
    public static final String HOSTNAME = "localhost";
//    public static final String HOSTNAME = "127.0.0.1";
    public static final String PORT = "1433";
    public static final String DBNAME = "QLBH";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "songlong";

    public static Connection getConnection() {
        String Url = "jdbc:sqlserver://"+ HOSTNAME+ ":"+ PORT+ ";databaseName="+ DBNAME+";encrypt=true;trustServerCertificate=true;";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(Url, USERNAME, PASSWORD);
        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e);
        }
        return null;
    }
    
    public static PreparedStatement getPpsm(String sql, Object...args){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try{
            if (sql.trim().startsWith("{")){
                ps = conn.prepareCall(sql); //proc 
            }else{
                ps = conn.prepareStatement(sql);
            }
            for (int i = 0; i<args.length; i++){
                ps.setObject(i + 1, args[i]);
            }
            return ps;
        }catch(Exception e){
            return null;
        }
    }
    
    public static int execUpdate(String sql, Object... args) {
        PreparedStatement ps = getPpsm(sql, args);
        try {
            return ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    public static ResultSet execQuery(String sql, Object...args) {
        PreparedStatement ps = getPpsm(sql, args);
        try {
            return ps.executeQuery();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    
    public static Object value(String sql, Object...args){
        ResultSet rs = execQuery(sql, args);
        try{
            if(rs.next()){
                return rs.getObject(0);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }
}
