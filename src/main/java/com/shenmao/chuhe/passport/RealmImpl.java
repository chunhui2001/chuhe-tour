package com.shenmao.chuhe.passport;

import com.shenmao.chuhe.Application;
import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.database.chuhe.sqlqueries.ChuheSqlQuery;
import com.shenmao.chuhe.database.chuhe.sqlqueries.DbQueryHelper;
import com.shenmao.chuhe.serialization.ChainSerialization;
import com.shenmao.chuhe.serialization.SerializeType;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.auth.shiro.impl.PropertiesAuthProvider;
import io.vertx.ext.auth.shiro.impl.ShiroUser;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.auth.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import rx.Single;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.shenmao.chuhe.verticle.ChuheDbVerticle.CONFIG_CHUHEDB_QUEUE;

public class RealmImpl extends AuthorizingRealm {

  private Vertx vertx;
  SecurityManager securityManager;
  ChuheDbService chuheDbService;

  public RealmImpl(Vertx vertx) {
    this.vertx =vertx;
    this.chuheDbService = ChuheDbService.createProxy(vertx.getDelegate(), CONFIG_CHUHEDB_QUEUE);
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

    String userName = (String) principalCollection.getPrimaryPrincipal();
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

    Set<String> roles = new HashSet<>();
    Set<String> perms = new HashSet<>();

    //perms.add("create");

    // role_user,role_dealer,role_admin
    String userRoles = null;

    try {

      userRoles = getUserRoles(userName);

      System.out.println(userRoles + ", userRoles");

      Arrays.stream(userRoles.split(","))
              .filter(s -> !s.isEmpty())
              .map(s -> s.split("_")[1])
              .forEach(s -> {
                roles.add(s);
              });

      authorizationInfo.setRoles(roles);

      authorizationInfo.setStringPermissions(roles);

    } catch (SQLException e) {

    }

    return authorizationInfo;

  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

    String username = (String) authenticationToken.getPrincipal();
    Object cred = authenticationToken.getCredentials();

    if (cred == null) {
      return null;
    }

    String password = new String((char[]) cred);

    // SubjectContext subjectContext = new DefaultSubjectContext();

    // Subject subject = securityManager.createSubject(subjectContext);
    // UsernamePasswordToken token = new UsernamePasswordToken(username, password);
    /* try {
        subject.login(token);
      } catch (AuthenticationException var9) {
        throw new VertxException(var9);
      } */

    try {
      if (null != username && null != password
          && !username.trim().isEmpty()
          && !password.isEmpty() && validateUser(username, password)) {
        return new SimpleAuthenticationInfo(username, password, getName());
      } else {
        return null;
      }
    } catch (SQLException e) {
      return null;
    }

  }

  private static Connection getJdbcConnection() {


    String dbHost = Application.getConfig().getString("mysql_host");
    Integer dbPort = Application.getConfig().getInteger("mysql_port");
    String dbName = Application.getConfig().getString("mysql_database_name");
    String dbUname = Application.getConfig().getString("mysql_uname");
    String dbPasswd = Application.getConfig().getString("mysql_password");

    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    String username = dbUname;
    String password = dbPasswd;
    Connection conn = null;

    try {

      Class.forName(driver);
      conn = DriverManager.getConnection(url, username, password);


    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return conn;

  }

  private static Boolean validateUser(String username, String passwd) throws SQLException {

    Connection conn = getJdbcConnection();
    String sql = DbQueryHelper.getSqlQueries().get(ChuheSqlQuery.VALIDATE_USER);
    PreparedStatement pstmt;

    try {

      pstmt = conn.prepareStatement(sql);

      pstmt.setString(1, username);
      pstmt.setString(2, passwd);

      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) return true;

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      conn.close();
    }

    return false;

  }

  private static String getUserRoles(String username) throws SQLException {

    Connection conn = getJdbcConnection();
    String sql = DbQueryHelper.getSqlQueries().get(ChuheSqlQuery.GET_USER_ROLES);
    PreparedStatement pstmt;

    try {

      pstmt = conn.prepareStatement(sql);

      pstmt.setString(1, username);

      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) return rs.getString(1);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      conn.close();
    }

    return null;

  }


  public static Single<JsonObject> userDetail(User user) {

    Single<Boolean> userRoleSingle = user.rxIsAuthorised("user");
    Single<Boolean> dealerRoleSingle = user.rxIsAuthorised("dealer");
    Single<Boolean> adminRoleSingle = user.rxIsAuthorised("admin");
    Single<Boolean> developRoleSingle = user.rxIsAuthorised("developer");

    return Single.zip(userRoleSingle,dealerRoleSingle, adminRoleSingle, developRoleSingle,
            (isUser, isDealer, isAdmin, isDev) -> {

      JsonArray roles = new JsonArray();

      if (isUser) roles.add("user");
      if (isDealer) roles.add("dealer");
      if (isAdmin) roles.add("admin");
      if (isDev) roles.add("developer");

      JsonObject userObject = new JsonObject()
              .put("username", user.principal().getString("username"))
              .put("roles", roles);

      return userObject;

    });

  }


}
