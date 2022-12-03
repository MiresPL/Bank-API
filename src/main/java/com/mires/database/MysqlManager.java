package com.mires.database;

import com.mires.objects.User;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlManager {

    private ConnectionPoolManager pool;
    private PreparedStatement ps;
    private ResultSet rs;
    private Connection con;

    public MysqlManager() throws SQLException {
        this.pool = new ConnectionPoolManager();
    }

    public boolean checkUsername(final String usernameToCheck) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM user;");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("login").equals(usernameToCheck)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return false;
        }
    }

    public boolean createNewUser(final User user) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("INSERT INTO user (uuid, name, surname, email, login, password, role) VALUES (?, ?, ?, ?, ?, ?, ?);");
            ps.setString(1, user.getUuid().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getLogin());
            ps.setString(6, user.getPassword());
            ps.setInt(7, user.getRole());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return false;
        }
    }

    public boolean login(final String login, final String password) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM user;");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("login").equals(login) && rs.getString("password").equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return false;
        }
    }

    public String getUser(final String login) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM user;");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("login").equals(login)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("uuid", rs.getString("uuid"));
                    jsonObject.put("name", rs.getString("name"));
                    jsonObject.put("surname", rs.getString("surname"));
                    jsonObject.put("email", rs.getString("email"));
                    jsonObject.put("login", rs.getString("login"));
                    jsonObject.put("role", rs.getInt("role"));
                    jsonObject.put("balance", String.format("%.2f", rs.getDouble("balance")));
                    return jsonObject.toString();
                }
            }
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return "";
        }
    }

    public JSONObject getUserByBLIKCode(final String code) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("SELECT * FROM blikCode;");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("code").equals(code)) {
                    JSONObject jsonObject = new JSONObject();
                    ps = con.prepareStatement("SELECT * FROM `user` WHERE uuid = ?;");
                    ps.setString(1, rs.getString("userId"));
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        jsonObject.put("uuid", rs.getString("uuid"));
                        jsonObject.put("name", rs.getString("name"));
                        jsonObject.put("surname", rs.getString("surname"));
                        jsonObject.put("email", rs.getString("email"));
                        jsonObject.put("login", rs.getString("login"));
                        jsonObject.put("role", rs.getInt("role"));
                        jsonObject.put("balance", String.format("%.2f", rs.getDouble("balance")));
                        return jsonObject;
                    }
                }
            }
            return new JSONObject();
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return new JSONObject();
        }
    }

    public boolean payByBLIK(final String uuid, final double amout) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("UPDATE user SET balance = balance - ? WHERE uuid = ?;");
            ps.setDouble(1, amout);
            ps.setString(2, uuid);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return false;
        }
    }

    public boolean deposit(final String uuid, final double amout) {
        try {
            con = pool.getConnection();
            ps = con.prepareStatement("UPDATE user SET balance = balance + ? WHERE uuid = ?;");
            ps.setDouble(1, amout);
            ps.setString(2, uuid);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return false;
        }
    }

    public boolean insertBlikCode(final String uuid, final String code) {
        try {
            con = pool.getConnection();

            ps = con.prepareStatement("SELECT * FROM blikcode;");
            rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString("userId").equals(uuid)) {
                    ps = con.prepareStatement("UPDATE blikcode SET code = ? WHERE userId = ?;");
                    ps.setString(1, code);
                    ps.setString(2, uuid);
                    ps.executeUpdate();
                    return true;
                }
            }

            ps = con.prepareStatement("INSERT INTO blikcode (userId, code) VALUES (?, ?);");
            ps.setString(1, uuid);
            ps.setString(2, code);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            pool.close(con, ps, rs);
            return false;
        }
    }

}
