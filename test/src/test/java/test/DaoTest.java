package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import org.axe.Axe;
import org.axe.bean.persistence.Page;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.util.JsonUtil;
import org.axe.util.StringUtil;
import org.test.bean.Account;
import org.test.bean.DnTable;
import org.test.bean.Export;
import org.test.bean.TestTable;
import org.test.dao.TestDao;

public class DaoTest {
	
	public static void main(String[] args) {
		try {
			Axe.init();
			testPrimarySql();
//			testDynamicTable();
//			testDynamicSql("abc", "123", null);
//			testDaoEntity();
//			testDaoPaging();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testPrimarySql(){
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		System.out.println(testDao.count());
	}
	
	/**
	 * 测试动态表
	 */
	public static void testDynamicTable(){
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		DnTable dnTable = new DnTable();
		dnTable.setId(1l);
		dnTable.setName("adf");
		testDao.deleteEntity(dnTable);
	}
	
	public static void testInsert() throws Exception{
		TestTable test = new TestTable();
		test.setId(3);
		test.setName("test3");
		test = DataBaseHelper.insertOnDuplicateKeyEntity(test);
		System.out.println(JsonUtil.toJson(test));
		
		/*Export export = new Export();
		export.setName("abc");
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		export = testDao.saveEntity(export);
		System.out.println(export.getId());*/
		
		System.exit(0);
	}
	
	public static void testDaoPaging() throws Exception{
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		List<TestTable> all = testDao.getAll();
		Page<TestTable> pageList = testDao.page();
		for(TestTable e:all){
			System.out.print(e.getName()+"\t");
		}
		System.out.println();
		System.out.println(pageList.getCount()+"\t"+pageList.getPages());
		for(TestTable e:pageList.getRecords()){
			System.out.print(e.getName()+"\t");
		}
		System.out.println();
	}
	
	public static String getSql(String loginName,String mobile,String email){
		StringBuilder sql = new StringBuilder("select * from Account where 1=1 ");
		if(StringUtil.isNotEmpty(loginName)){
			sql.append(" and loginName like '%"+loginName+"%'");
		}

		if(StringUtil.isNotEmpty(mobile)){
			sql.append(" and mobile like '%"+mobile+"%'");
		}

		if(StringUtil.isNotEmpty(email)){
			sql.append(" and email like '%"+mobile+"%'");
		}
		return sql.toString();
	}
	
	public static void testDynamicSql(String loginName,String mobile,String email){
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		
		
		List<Account> accountList = testDao.getAccountList(getSql(loginName, mobile, email));
		System.out.println(accountList.size());
	}
	
	public static void testDaoEntity() throws Exception{
		
		
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		Export export = new Export();
		export.setAccountId(1);
		export.setCode("a");
		export.setCreateTime(new Date());
		export.setDownloadTimes(0);
		export.setLastDownloadTime(null);
		export.setName("xxx");
		export.setProcess(100);
		export.setStatus(1);
		export.setStillWaitSec(100);
		testDao.saveEntity(export);
		
		
		export = testDao.getEntity(export);
		System.out.println("getEntity:"+JsonUtil.toJson(export));
		
		export.setCreateTime(new Date());
		
		export = testDao.saveEntity(export);
		System.out.println("saveEntity:"+export);
		
		export.setName("新框架测试_SqlHelper");
		int rows = testDao.updateEntity(export);
		System.out.println("updateEntity:"+rows);
		
		export = testDao.getEntity(export);
		System.out.println("getEntity:"+JsonUtil.toJson(export));
	
		rows = testDao.deleteEntity(export);
		System.out.println("deleteEntity:"+rows);
		
		rows = testDao.updateEntity(export);
		System.out.println("updateEntity:"+rows);
		
		export = testDao.insertEntity(export);
		System.out.println("updateEntity:"+rows);
		
		System.exit(0);
	}
	
	public static void mysqlKeywords(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("data.txt"));
			String line = br.readLine();
			while(!StringUtil.isEmpty(line)){
				String[] wordAry = line.split("	");
				for(String word:wordAry){
					word = word.trim();
					if(StringUtil.isEmpty(word)) continue;
					System.out.print(word+",");
				}
				line = br.readLine();
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
