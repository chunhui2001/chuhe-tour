package com.shenmao.chuhe.passport;

import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.auth.shiro.impl.LDAPAuthProvider;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.auth.shiro.ShiroAuth;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;

import java.util.Objects;

public class ShiroAuthProviderImpl {

  private Vertx vertx;
  private SecurityManager securityManager;
  private String rolePrefix = "role:";
  private String realmName;

  private ShiroAuthProviderImpl() {

  }

  public static ShiroAuth create(Vertx vertx, ShiroAuthOptions options) {
    RealmImpl realm;
    switch(options.getType()) {
      case PROPERTIES:
        // realm = PropertiesAuthProvider.createRealm(options.getConfig());
        realm = new RealmImpl(vertx);
        break;
      case LDAP:
        realm = (RealmImpl) LDAPAuthProvider.createRealm(options.getConfig());
        break;
      /* case "JDBC":
        String dbName = "db_chuhe_local";//json.getString("db-name");
        Objects.requireNonNull(dbName);
        JDBCClient client = JDBCClient.createShared(vertx, authProperties, dbName);
        this.authProvider = JDBCAuth.create(client);
        break; */
      default:
        throw new IllegalArgumentException("Invalid shiro auth realm type: " + options.getType());
    }

    return new ShiroAuthProviderImpl().newInstance(vertx, realm, options);

  }


  private ShiroAuth newInstance(Vertx vertx, RealmImpl realm, ShiroAuthOptions options) {

    this.vertx = vertx;
    this.securityManager = new DefaultSecurityManager(realm);
    this.realmName = realm.getName();

    DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
    SecurityUtils.setSecurityManager(securityManager);
    realm.setSecurityManager(this.securityManager);

    return ShiroAuth.create(vertx, options);

  }

  Vertx getVertx() {
    return this.vertx;
  }

  SecurityManager getSecurityManager() {
    return this.securityManager;
  }

  String getRealmName() {
    return this.realmName;
  }
}

