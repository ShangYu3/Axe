/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.helper.persistence;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.axe.annotation.persistence.ColumnDefine;
import org.axe.annotation.persistence.Comment;
import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Transient;
import org.axe.annotation.persistence.Unique;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.constant.IdGenerateWay;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.base.Helper;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;

/**
 * @author CaiDongyu
 * 数据库Schema 助手类
 */
public class SchemaHelper implements Helper{

	//#所有列出的java到mysql的类型转换
	private static Map<String,String> JAVA2MYSQL_MAP = new HashMap<>();	//#所有列出的java到mysql的类型转换
	
	@Override
	public void init() throws Exception {
		JAVA2MYSQL_MAP.put("byte", "tinyint(4)");
		JAVA2MYSQL_MAP.put("java.lang.Byte", "tinyint(4)");
		JAVA2MYSQL_MAP.put("short", "smallint(6)");
		JAVA2MYSQL_MAP.put("java.lang.Short", "smallint(6)");
		JAVA2MYSQL_MAP.put("int", "int(11)");
		JAVA2MYSQL_MAP.put("java.lang.Integer", "int(11)");
		JAVA2MYSQL_MAP.put("long", "bigint(20)");
		JAVA2MYSQL_MAP.put("java.lang.Long", "bigint(20)");
		JAVA2MYSQL_MAP.put("float", "float");
		JAVA2MYSQL_MAP.put("java.lang.Float", "float");
		JAVA2MYSQL_MAP.put("double", "double");
		JAVA2MYSQL_MAP.put("java.lang.Double", "double");
		JAVA2MYSQL_MAP.put("char", "char(1)");
		JAVA2MYSQL_MAP.put("java.lang.Character", "char(1)");
		JAVA2MYSQL_MAP.put("boolean", "bit(1)");
		JAVA2MYSQL_MAP.put("java.lang.Boolean", "bit(1)");
		JAVA2MYSQL_MAP.put("java.lang.String", "varchar(255)");
		JAVA2MYSQL_MAP.put("java.math.BigDecimal", "decimal(19,2)");
		JAVA2MYSQL_MAP.put("java.sql.Date", "datetime");
		JAVA2MYSQL_MAP.put("java.util.Date", "datetime");
		//byte[]
		JAVA2MYSQL_MAP.put("[B", "tinyblob");
	}

	@Override
	public void onStartUp() throws Exception {
		//在框架的Helper都初始化后，同步表结构，（现阶段不会开发此功能，为了支持多数据源，借鉴了Rose框架）
		Map<String, Class<?>> ENTITY_CLASS_MAP = TableHelper.getEntityClassMap();
		if(ConfigHelper.getJdbcAutoCreateTable() == null){
			//按@Table定义
			DataBaseHelper.beginTransaction();
			for(Class<?> entityClass:ENTITY_CLASS_MAP.values()){
				if(TableHelper.isTableAutoCreate(entityClass)){
					SchemaHelper.createTable(entityClass);
				}
			}
			DataBaseHelper.commitTransaction();
		} else if(ConfigHelper.getJdbcAutoCreateTable()){
			//全局开启，优先级最高，不管@Table如何定义，全部创建
			DataBaseHelper.beginTransaction();
			for(Class<?> entityClass:ENTITY_CLASS_MAP.values()){
				SchemaHelper.createTable(entityClass);
			}
			DataBaseHelper.commitTransaction();
		} else {
			//全局关闭了，优先级也最高，直接不创建
		}
		
	}
	
	public static void createTable(Class<?> entityClass) throws SQLException{
		String tableName = TableHelper.getTableName(entityClass);
		StringBuilder createTableSqlBufer = new StringBuilder(); 
		createTableSqlBufer.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
		//#取含有get方法的字段，作为数据库表字段，没有get方法的字段，认为不是数据库表字段
		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entityClass);
		//#转类非主键字段到数据库表字段定义
		List<Field> primaryKeyFieldList = new ArrayList<>();
		List<Field> normalKeyFieldList = new ArrayList<>();;
		List<Field> uniqueKeyFieldList = new ArrayList<>();
		for(int i=0;i<entityFieldMethodList.size();i++){
			EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
			Field field = entityFieldMethod.getField();
			if(field.isAnnotationPresent(Transient.class)){
				if(!field.getAnnotation(Transient.class).save()){
					continue;
				}
			}
			
			if(field.isAnnotationPresent(Id.class)){
				//#等会儿主键处理
				primaryKeyFieldList.add(field);
			}else{
				//#普通建处理
				normalKeyFieldList.add(field);
				if(field.isAnnotationPresent(Unique.class)){
					//#唯一键
					uniqueKeyFieldList.add(field);
				}
			}
		}
		//#普通建处理
		for(int i=0;i<normalKeyFieldList.size();i++){
			Field field = normalKeyFieldList.get(i);
			String column = StringUtil.camelToUnderline(field.getName());
			createTableSqlBufer.append("`").append(column).append("`");
			String columnDefine = javaType2MysqlColumnDefine(field,true);
			if(StringUtil.isEmpty(columnDefine)){
				throw new RuntimeException(entityClass.getName()+"#["+field.getName()+"] connot convert to mysql type from "+field.getType().getName());
			}
			createTableSqlBufer.append(" ").append(columnDefine);
			
			if(i<normalKeyFieldList.size()-1){
				createTableSqlBufer.append(",");
			}
		}
		//#主键定义
		if(CollectionUtil.isNotEmpty(primaryKeyFieldList)){
			createTableSqlBufer.append(",");
			
			for(int i=0;i<primaryKeyFieldList.size();i++){
				Field primaryKeyField = primaryKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append("`").append(column).append("`");
				String columnDefine = javaType2MysqlColumnDefine(primaryKeyField,false);
				if(StringUtil.isEmpty(columnDefine)){
					throw new RuntimeException(entityClass.getName()+"#["+primaryKeyField.getName()+"] connot convert to mysql type from "+primaryKeyField.getType().getName());
				}
				createTableSqlBufer.append(" ").append(columnDefine);
				if(primaryKeyFieldList.size() == 1){
					//#若只有一个@Id主键，那么默认 AUTO_INCREMENT
					Field field = primaryKeyFieldList.get(0);
					if(!field.isAnnotationPresent(ColumnDefine.class)){
						if(field.getAnnotation(Id.class).idGenerateWay().equals(IdGenerateWay.AUTO_INCREMENT)){
							createTableSqlBufer.append(" AUTO_INCREMENT");
						}
					}
				}
				createTableSqlBufer.append(",");
			}
			
			createTableSqlBufer.append("PRIMARY KEY (");
			for(int i=0;i<primaryKeyFieldList.size();i++){
				Field primaryKeyField = primaryKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append("`").append(column).append("`");
				if(i<primaryKeyFieldList.size()-1){
					createTableSqlBufer.append(",");
				}
			}
			createTableSqlBufer.append(")");
			
			
			
		}
		
		//#唯一键约束
		if(CollectionUtil.isNotEmpty(uniqueKeyFieldList)){
			createTableSqlBufer.append(",");
			
			String keyName = tableName+"_uq";
			createTableSqlBufer.append("UNIQUE KEY "+keyName+" (");
			for(int i=0;i<uniqueKeyFieldList.size();i++){
				Field primaryKeyField = uniqueKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append("`").append(column).append("`");
				if(i<uniqueKeyFieldList.size()-1){
					createTableSqlBufer.append(",");
				}
			}
			createTableSqlBufer.append(")");
		}
		
		
		createTableSqlBufer.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		
		
		String tableDataSourceName = TableHelper.getTableDataSourceName(entityClass);
		DataBaseHelper.executeUpdate(createTableSqlBufer.toString(), new Object[]{}, new Class<?>[]{}, tableDataSourceName);
	}
	
	public static String javaType2MysqlColumnDefine(Field field,boolean nullAble){
		StringBuilder columnDefine = new StringBuilder();
		if(field.isAnnotationPresent(ColumnDefine.class)){
			columnDefine.append(field.getAnnotation(ColumnDefine.class).value());
		}else{
			String javaType = field.getType().getName();
			columnDefine.append(nullAble?JAVA2MYSQL_MAP.get(javaType)+" DEFAULT NULL":JAVA2MYSQL_MAP.get(javaType)+" NOT NULL");
		}
		if(field.isAnnotationPresent(Comment.class)){
			columnDefine.append(" COMMENT '").append(field.getAnnotation(Comment.class).value()).append("'");
		}
		return columnDefine.toString();
	}
	
}
