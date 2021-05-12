package bmfw;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.DayNewsCrawler;

import java.io.IOException;
import java.sql.*;
import java.util.*;


public class RecordDataSql {



	public static void main(String[] args) throws ClassNotFoundException, SQLException {



//		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bmtest2?useSSL=false","root","1234567");
//		Statement stmt = conn.createStatement();
//		ResultSet rs = stmt.executeQuery("SELECT * from cs_facilitator_maintain;");
//		List<Map<String, Object>> list = convertList(rs);
//
//		System.out.println(list.get(0).get("attendItem"));
//
//		// 完成后关闭
//		rs.close();
//		stmt.close();
//		conn.close();

		String sqlTemplate = "INSERT INTO `cs_resident_consume_record` VALUES " +
				"('%s'" + // id randomUUID
				", '%s'" + // maintainId
				", '%s'" + // 服务时间  记录居民在微信端扫描消费时间
				", '%s'" + // 年
				", '%s'" + // 月
				", '%s'" + // residentId
				", '%d'" + // 服务项目编码   服务商服务类型：1老年餐桌（1天1次）、2便民服务3、修脚服务4、其他（1天1次），由maintainId可查
				", '%d'" + // 消费类型:1:移动端消费;2:养老卡消费
				");\n";


		int size = 53371;
		String sql2 = "insert into cs_resident_consume_record select REPLACE(uuid(),'-','')," + //
				"'%s', " + //
				"DATE_ADD('%d-%s-%s 10:00:00', " + //
				" INTERVAL  FLOOR(1 + (RAND() * 28800))   SECOND ), " + //
				"'%s', " + //
				"'%s', id, '1' ,'2' from cs_resident_info";
		String mainId = "a2b0832a32d64bcbb400d41b0e9baf19";
		int a = size / 4122;
		int b = size % 4122;
		int year = 2020;
		int month = 12;

		Set<Integer> ms = randMon(a+1);
		Integer [] msi = ms.toArray(new Integer[]{});

		for(int i = 0; i < msi.length ; i++) {
			int day = msi[i];
			String dayStr = day<10 ? "0" + day : ""+ day;
			String monStr =  month<10 ? "0" + month : ""+ month;

			if(i < msi.length - 1) {
				String _sql = String.format(sql2, mainId,year,monStr,dayStr,year,month) + ";";
				System.out.println(_sql);
			}else {
				String _sql = String.format(sql2, mainId,year,monStr,dayStr,year,month) + " limit " + b + ";";
				System.out.println(_sql);
			}

		}



	}

	private static List<Map<String, Object>> convertList(ResultSet rs) throws SQLException {
		List list = new ArrayList();
		ResultSetMetaData md = rs.getMetaData();//获取键名
		int columnCount = md.getColumnCount();//获取行的数量
		while (rs.next()) {
			Map rowData = new HashMap();//声明Map
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i)); //获取键名及值
			}
			list.add(rowData);
		}
		return list;
	}

	private static Set<Integer> randMon(int size){
		Set<Integer> set = new HashSet<>();
		while(true) {
			if(set.size()>=size) {
				return set;
			}else {
				int i = new Random().nextInt(size) + 1;
				if(!set.contains(i)) {
					set.add(i);
				}
			}
		}
	}

}
