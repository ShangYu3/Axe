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
package org.axe.helper.base;

import java.util.Properties;

import org.axe.constant.ConfigConstant;
import org.axe.interface_.base.Helper;
import org.axe.util.PropsUtil;

/**
 * 配置文件助手类
 * <p>
 * @author CaiDongyu on 2016/4/8.
 */
public final class ConfigHelper implements Helper{

    private static Properties CONFIG_PROPS;

    @Override
    public void init() {
    	synchronized (this) {
    		CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);
		}
    }
    
    public static Properties getCONFIG_PROPS() {
		return CONFIG_PROPS;
	}
    
    /**
     * 是否开启/axe的访问
     */
    public static boolean getAxeHome(){
    	return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.AXE_HOME, true);
    }
    
    /**
     * 系统联系人邮箱地址
     * 系统错误、密码找回等邮件的通知地址
     * 多个地址用“,”逗号分割
     */
    public static String getAxeEmail(){
    	return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.AXE_EMAIL, null);
    }
    
    /**
     * 是否开启/axe的登录访问
     * 默认不需要登录
     */
    public static boolean getAxeSignIn(){
    	return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.AXE_SIGN_IN, false);
    }
    
    /**
     * /axe的登录访问的口令
     * 实际上这只是公钥口令，保存在axe.properties配置文件中
     * 私钥口令只存在内存与登录页面
     * 私钥每次登录会重新产生，与ip相关
     */
    public static String getAxeSignInToken(){
    	return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.AXE_SIGN_IN_TOKEN,null);
    }
    
    /**
     * 是否释放框架初始化完成后的，ClassHelper内的classSet集合
     * 这个集合如果在实际项目中，可能会很大，因为不知道有多少资源会全部被扫描进来
     * 如果需要完全使用axe_home，可以不关闭，否则默认关闭
     */
    public static boolean getAxeClassHelperKeep(){
    	return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.AXE_CLASSHELPER_KEEP, false);
    }
    
    /**
     * 获取 JDBC 驱动
     */
    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER, null);
    }

    /**
     * 获取 JDBC URL
     */
    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_URL, null);
    }

    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME, null);
    }
    
    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD, null);
    }
    
    /**
     * 获取 JDBC 数据源
     * 多个值用“,”逗号分隔
     */
    public static String getJdbcDatasource() {
    	//默认使用axe提供的dbcp数据源
    	return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DATASOURCE, "axe-datasource-dbcp");
    }

    /**
     * 是否自动建表
     */
    public static Boolean getJdbcAutoCreateTable() {
    	return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.JDBC_AUTO_CREATE_TABLE, null);
    }
    
    /**
     * 是否打印SQL
     */
    public static boolean getJdbcShowSql() {
    	return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.JDBC_SHOW_SQL, false);
    }
    
    /**
     * 指定框架扫描的包路径，多个路径使用“,”号分割
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE, null);
    }

    /**
     * 指定jsp存放路径（建议 /WEB-INF/view/ ）
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, null);
    }

    /**
     * 指定静态文件(html、js、css、图片等)存放路径（建议 /asset/）
     */
    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSET_PATH, null);
    }

    /**
     * 文件上传限制单次文件大小，单位M
     */
    public static int getAppUploadLimit() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.APP_UPLOAD_LIMIT, 0);
    }

	@Override
	public void onStartUp() throws Exception {}
}
