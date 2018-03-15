package com.shenmao.chuhe.passport;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
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
    String userRoles = "role_user,role_dealer,role_admin";

    Arrays.stream(userRoles.split(","))
            .filter(s -> !s.isEmpty())
            .map(s -> s.split("_")[1])
            .forEach(s -> {
      roles.add(s);
    });

    authorizationInfo.setRoles(roles);

    authorizationInfo.setStringPermissions(roles);


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

    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3307/db_chuhe_local";
    String username = "root";
    String password = "Cc";
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
    String sql = "select * from users where user_name =? and user_passwd =?";
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

  public static Single<JsonObject> userDetail(User user) {

    Single<Boolean> createSingle = user.rxIsAuthorised("user");
    Single<Boolean> updateSingle = user.rxIsAuthorised("dealer");
    Single<Boolean> deleteSingle = user.rxIsAuthorised("admin");

    return Single.zip(createSingle,updateSingle, deleteSingle, (isUser, isDealer, isAdmin) -> {

      JsonArray roles = new JsonArray();

      if (isUser) roles.add("user");
      if (isDealer) roles.add("dealer");
      if (isAdmin) roles.add("admin");

      JsonObject userObject = new JsonObject()
              .put("username", user.principal().getString("username"))
              .put("roles", roles);

      return userObject;

    });

  }


}
