package main

import db.DbAdmin
import scalikejdbc.config.DBs
import java.util.Timer

object DataProcessor {
  def main(args: Array[String]) {
    DBs.setup()
    recreateDB()

    // Run the DataTasker task after 0ms, repeating every 4 weeks
    // TODO new Timer().schedule(DataTasker, 0, 4 * 7 * 24 * 3600 * 1000)
    DataTasker.run()
  }

  private def recreateDB() {
    DbAdmin.dropTables()
    DbAdmin.createTables()
    DbAdmin.initData()
  }
}
