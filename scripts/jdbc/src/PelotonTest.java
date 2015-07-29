import java.sql.*;

/**
 * Author:  Ming Fang
 * Date:    7/19/15.
 * Email:   mingf@cs.cmu.edu
 */
public class PelotonTest {
  private final String url = "jdbc:postgresql://localhost:57721/";
  private final String username = "postgres";
  private final String pass = "postgres";

  private final String DROP = "DROP TABLE IF EXISTS A;" +
          "DROP TABLE IF EXISTS B;";
  private final String DDL = "CREATE TABLE A (id INT PRIMARY KEY, data TEXT);" +
          "CREATE TABLE B (id INT PRIMARY KEY, data TEXT);";
  private final String INSERT = "INSERT INTO A VALUES (?,?);" +
          "INSERT INTO B VALUES (?,?);";
  private final String SEQSCAN = "SELECT * FROM A";
  private final String INDEXSCAN = "SELECT * FROM A WHERE id = ?";
  private final String BITMAPSCAN = "SELECT * FROM A WHERE id > ? and id < ?";
  private final String UPDATE_BY_INDEXSCAN = "UPDATE A SET data=? WHERE id=?";
  private final String UPDATE_BY_SCANSCAN = "UPDATE A SET data=?";
  private final String DELETE_BY_INDEXSCAN = "DELETE FROM A WHERE id = ?";
  private final String SELECT_FOR_UPDATE = "SELECT * FROM A WHERE id = ? FOR UPDATE";

  private final Connection conn;

  public PelotonTest() throws SQLException {
    try {
      Class.forName("org.postgresql.Driver");
    } catch(ClassNotFoundException e) {
      e.printStackTrace();
    }
    conn = this.makeConnection();
    return;
  }

  private Connection makeConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(url, username, pass);
    return conn;
  }

  public void Close() throws SQLException {
    conn.close();
  }

  /**
   * Drop if exists and create testing database
   * @throws SQLException
   */
  public void Init() throws SQLException {
    conn.setAutoCommit(true);
    Statement stmt = conn.createStatement();
    stmt.execute(DROP);
    stmt.execute(DDL);
    System.out.println("Test db created.");
  }

  /**
   * Insert a record
   * @param n the id of the record
   * @throws SQLException
   */
  public void Insert(int n) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(INSERT);
    org.postgresql.PGStatement pgstmt = (org.postgresql.PGStatement)stmt;
    for (int i = 1; i < n; i++) {
      String data1 = "Ming says hello world and id = " + i;
      String data2 = "Joy says hello world and id = " + i;
      stmt.setInt(1, i);
      stmt.setInt(3, i);
      stmt.setString(2, data1);
      stmt.setString(4, data2);
      boolean usingServerPrepare = pgstmt.isUseServerPrepare();
      stmt.executeUpdate();
      System.out.println("Used server side prepare " + usingServerPrepare + ", Inserted: id: " + i + ", data: " + data1);
    }
    return;
  }

  public void SeqScan() throws SQLException {
    System.out.println("\nSeqScan Test:");
    System.out.println("Query: " + SEQSCAN);
    PreparedStatement stmt = conn.prepareStatement(SEQSCAN);
    ResultSet r = stmt.executeQuery();
    while (r.next()) {
      System.out.println("SeqScan: id = " + r.getString(1) + ", " + r.getString(2));
    }
    r.close();
  }

  /**
   * Perform Index Scan with a simple equal qualifier
   * @param i the param for the equal qualifier
   * @throws SQLException
   */
  public void IndexScan(int i) throws SQLException {
    System.out.println("\nIndexScan Test: ? = " + i);
    System.out.println("Query: " + INDEXSCAN);
    PreparedStatement stmt = conn.prepareStatement(INDEXSCAN);
    stmt.setInt(1, i);
    ResultSet r = stmt.executeQuery();
    while (r.next()) {
      System.out.println("IndexScanTest got tuple: id: " + r.getString(1) + ", data: " + r.getString(2));
    }
    r.close();
  }

  /**
   * Perform Index Scan with a simple equal qualifier
   * @param i the param for the equal qualifier
   * @throws SQLException
   */
  public void BitmapScan(int i, int j) throws SQLException {
    System.out.println("\nBitmapScan Test: ? = " + i + ", " + j);
    System.out.println("Query: " + BITMAPSCAN);
    PreparedStatement stmt = conn.prepareStatement(BITMAPSCAN);
    stmt.setInt(1, i);
    stmt.setInt(2, j);
    ResultSet r = stmt.executeQuery();
    while (r.next()) {
      System.out.println("BitmapScanTest got tuple: id: " + r.getString(1) + ", data: " + r.getString(2));
    }
    r.close();
  }

  public void UpdateByIndex(int i) throws SQLException {
    System.out.println("\nUpdate Test: ? = " + i);
    System.out.println("Query: " + UPDATE_BY_INDEXSCAN);
    PreparedStatement stmt = conn.prepareStatement(UPDATE_BY_INDEXSCAN);
    stmt.setInt(2, i);
    stmt.setString(1, "Updated");
    stmt.executeUpdate();
    System.out.println("Updated: id: " + i + ", data: Updated");

  }


  public void UpdateBySeqScan() throws SQLException {
    System.out.println("\nUpdate Test: ");
    System.out.println("Query: " + UPDATE_BY_SCANSCAN);
    PreparedStatement stmt = conn.prepareStatement(UPDATE_BY_SCANSCAN);
    stmt.setString(1, "Updated");
    stmt.executeUpdate();
    System.out.println("Updated: data: Updated");

  }


  public void DeleteByIndexScan(int i) throws SQLException {
    System.out.println("\nDelete Test: ");
    System.out.println("Query: " + DELETE_BY_INDEXSCAN);
    PreparedStatement stmt = conn.prepareStatement(DELETE_BY_INDEXSCAN);
    stmt.setInt(1, i);
    stmt.executeUpdate();
    System.out.println("Deleted: id = " + i);
  }

  public void ReadModifyWrite(int i) throws SQLException {
    System.out.println("\nReadModifyWrite Test: ");
    System.out.println("Query: " + SELECT_FOR_UPDATE);
    PreparedStatement stmt = conn.prepareStatement(SELECT_FOR_UPDATE);

    stmt.setInt(1, i);
    ResultSet r = stmt.executeQuery();
    while (r.next()) {
      System.out.println("ReadModifyWriteTest got tuple: id: " + r.getString(1) + ", data: " + r.getString(2));
    }

    r.close();

    stmt = conn.prepareStatement(UPDATE_BY_INDEXSCAN);
    stmt.setInt(2, i);
    stmt.setString(1, "Updated");
    stmt.executeUpdate();

    System.out.println("Updated: id: " + i + ", data: Updated");
  }


  static public void main(String[] args) throws Exception {
    PelotonTest pt = new PelotonTest();
    pt.Init();
    pt.Insert(20);

    //pt.ReadModifyWrite(3);
    //pt.BitmapScan(2, 5);
    //pt.SeqScan();
    //pt.DeleteByIndexScan(3);
    //pt.SeqScan();
    //pt.UpdateBySeqScan();
    //pt.IndexScan(3);
    pt.Close();
  }
}
