package metierDAO;

import java.sql.Connection;

public abstract class DAO<T> {
  protected Connection connect = null;
   
  DAO(Connection conn){
    this.connect = conn;
  }
 
}