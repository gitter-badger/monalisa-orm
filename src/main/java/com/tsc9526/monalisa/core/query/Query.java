/*******************************************************************************************
 *	Copyright (c) 2016, zzg.zhou(11039850@qq.com)
 * 
 *  Monalisa is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.

 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Lesser General Public License for more details.

 *	You should have received a copy of the GNU Lesser General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************************/
package com.tsc9526.monalisa.core.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.tsc9526.monalisa.core.agent.AgentClass;
import com.tsc9526.monalisa.core.datasource.DBConfig;
import com.tsc9526.monalisa.core.datasource.DataSourceManager;
import com.tsc9526.monalisa.core.datasource.DbProp;
import com.tsc9526.monalisa.core.generator.DBExchange;
import com.tsc9526.monalisa.core.logger.Logger;
import com.tsc9526.monalisa.core.query.cache.Cache;
import com.tsc9526.monalisa.core.query.cache.CacheKey;
import com.tsc9526.monalisa.core.query.datatable.DataMap;
import com.tsc9526.monalisa.core.query.datatable.DataTable;
import com.tsc9526.monalisa.core.query.dialect.Dialect;
import com.tsc9526.monalisa.core.query.executor.CacheExecutor;
import com.tsc9526.monalisa.core.query.executor.Execute;
import com.tsc9526.monalisa.core.query.executor.ResultExecutor;
import com.tsc9526.monalisa.core.query.executor.ResultLoadExecutor;
import com.tsc9526.monalisa.core.query.executor.ResultSetExecutor;
import com.tsc9526.monalisa.core.query.executor.ResultSetsExecutor;
import com.tsc9526.monalisa.core.query.executor.UpdateExecutor;
import com.tsc9526.monalisa.core.tools.CloseQuietly;
import com.tsc9526.monalisa.core.tools.SQLHelper;
 

/**
 * 数据库查询对象, 基本用法: <br>
 * <code>
 * Query q=new Query(); <br>
 * q.use(db); <br>
 * q.add("select * from xxx where id=?",1); <br>
 * List&lt;Result&gt; r=q.getList(Result.class);   <br>
 * Page&lt;Result&gt; p=q.getPage(Result.class,10,0);<br>
 * Result       x=q.getResult(Result.class);<br>
 * </code>
 * 
 * @author zzg.zhou(11039850@qq.com)
 */

@SuppressWarnings({"unchecked"})
public class Query {	
	static Logger logger=Logger.getLogger(Query.class.getName());
	  
	/**
	 * 创建自定义SQL查询的类
	 * 
	 * @param theQueryClass 自定义的SQL查询类
	 * 		
	 * @return 创建一个新的实例。
	 */
	public static <T> T create(Class<T> theQueryClass){
		 return AgentClass.createAgent(theQueryClass);
	}
 	
	/**
	 * 是否显示执行的SQL语句, 默认为: false
	 */
	private Boolean debugSql=null;
	 
	protected DataSourceManager dsm=DataSourceManager.getInstance();
	
	protected StringBuffer  sql=new StringBuffer();
	protected List<Object>  parameters=new ArrayList<Object>();
 	 
	protected DBConfig      db;
 	
	protected boolean enableCache=false;
	
	protected Boolean readonly;
 	
	protected List<List<Object>> batchParameters=new ArrayList<List<Object>>();
	
	protected Object tag;
	
	protected PrintWriter writer=null;
	
	
	protected Cache cache;
	
	public Query(){		 
	}
	
	public Query(DBConfig db){
		 this.db=db;
	}	 
	  
	public Query setDebugSql(boolean debugSql){
		this.debugSql=debugSql;
		return this;
	}
	
	public boolean isDebugSql(){
		return debugSql==null?false:debugSql;
	}
	
	public <T> T getTag(){
		return (T)tag;
	}
	
	public void setTag(Object tag){
		this.tag=tag;
	}
	 
	public Query notin(Object... values){
		return getDialect().notin(this, values);
	}
	 
	public Query in(Object... values){
		 return getDialect().in(this, values);
	}
	  
	public Query add(Query q){	
		return add(q.getSql(),q.getParameters());
	}
	
	public Query add(String segment,Object ... args){		
		if(segment!=null){			 
			sql.append(segment);
		}
		
		if(args!=null){
			for(Object arg:args){
				if(arg instanceof Collection){
					for(Object x:((Collection<?>)arg)){
						parameters.add(x);
					}
				}else{
					parameters.add(arg);
				}
			}
		}
		return this;
	}	
  
	/**
	 * 如果参数非空，则添加该SQL片段，否则忽略
	 * 
	 * @param segment  SQL片段
	 * @param args  参数
	 * @return 查询本身
	 */
	public Query addIfNotEmpty(String segment,Object ... args){
		if(args!=null && args.length==1){
			if(args[0]==null){
				return this;
			}else if(args[0] instanceof String){
				String s=(String)args[0];
				if(s.trim().length()<1){
					return this;
				}
			}
		}
		
		return add(segment, args);
	}
	
	public boolean isEmpty(){
		return sql.length()==0;
	}
	
	/**
	 * 
	 * @return 原始的SQL语句, 中间可能会有参数
	 */
	public String getSql() {
		String r=sql.toString();
		return r;
	}
	
	/**
	 * 
	 * @return 处理过参数后的SQL语句
	 */
	public String getExecutableSQL() {
		 return SQLHelper.getExecutableSQL(getSql(), parameters);
	}

	public Query clear(){		 
		if(this.sql.length()>0){
			this.sql.delete(0,this.sql.length());
		}		 
		this.parameters.clear();
		
		return this;
	}

	public int parameterCount(){
		return parameters.size();
	}
 
	public List<Object> getParameters() {
		return parameters;
	}
	 
	public Query clearParameters(){
		this.parameters.clear();
		return this;
	}
	
	public Query setParameters(List<Object> parameters) {
		this.parameters = parameters;
		
		return this;
	}
	
	public Query addBatch(Object... parameters) {
		if(this.parameters!=null && this.parameters.size()>0){
			this.batchParameters.add(this.parameters);
		}
		
		this.batchParameters.add(Arrays.asList(parameters));
				
		return this;
	}
	
	protected Connection getConnectionFromTx(Tx tx) throws SQLException{
		return tx.getConnection(db);		 
	}
	
	protected Connection getConnectionFromDB(boolean autoCommit) throws SQLException{
		Connection conn=db.getDataSource().getConnection();
		conn.setAutoCommit(autoCommit);
		return conn;
	}
	
	public int execute(){
		return doExecute(new UpdateExecutor());
	}
	
	public <X> X execute(Execute<X> execute){
		 return doExecute(execute);
	}	
	
	public int[] executeBatch(){
		Tx tx=Tx.getTx();
		
		Connection conn=null;
		PreparedStatement pst=null;
		try{
			conn= tx==null?getConnectionFromDB(false):getConnectionFromTx(tx);
			
			pst=conn.prepareStatement(getSql());
			for(List<Object> p:batchParameters){
				SQLHelper.setPreparedParameters(pst, p);
				pst.addBatch();
			}
			
			int[] result=pst.executeBatch();
			
			if(tx==null){
				conn.commit();
			}			
			
			return result;
		}catch(Exception e){
			if(tx==null && conn!=null){
				try{ conn.rollback(); }catch(SQLException ex){}
			}
			throw new RuntimeException(e);
		}finally{
			CloseQuietly.close(pst);
			
			if(tx==null){
				CloseQuietly.close(conn);
			}
		}
	}
	 
	
	protected <X> X doExecute(Execute<X> x){
		x=new CacheExecutor<X>(this, x);
		
		Tx tx=Tx.getTx();
		
		Connection conn=null;
		PreparedStatement pst=null;
		try{
			conn= tx==null?getConnectionFromDB(true):getConnectionFromTx(tx);
			 
			pst=x.preparedStatement(conn,getSql());
			 
			SQLHelper.setPreparedParameters(pst, parameters);
			
			logSql();
			
			return x.execute(pst);			
		}catch(SQLException e){
			String executeSQL=sql.toString();
			try{
				executeSQL=getExecutableSQL();
			}catch(Exception ex){}
			
			throw new RuntimeException("SQL ERROR: \r\n========================================================================\r\n"
					                  +executeSQL+"\r\n========================================================================",e);
		}finally{
			CloseQuietly.close(pst);
			
			if(tx==null){
				CloseQuietly.close(conn);
			}
		}
	}
	 
 
	
	public DataMap getResult(){
		return getResult(DataMap.class);				
	}
	 

	/**
	 * 使用该方法获取查询返回的多个结果集
	 * 
	 * @return
	 */
	public List<DataTable<DataMap>> getAllResults(){
		queryCheck();
		 
		int deepth = DbProp.PROP_DB_MULTI_RESULTSET_DEEPTH.getIntValue(db,100);
		ResultHandler<DataMap> resultHandler=new ResultHandler<DataMap>(this,DataMap.class);
		
		return doExecute(new ResultSetsExecutor<DataMap>(resultHandler,deepth));  
	}
	 
	/**
	 * 将查询结果转换为指定的类
	 *  
	 * @param resultClass translate DataMap to the result class
	 * @param <T> result type
	 * @return the result object
	 */
	public <T> T getResult(final Class<T> resultClass){
		return getResult(new ResultHandler<T>(this,resultClass));
	}
	
	/**
	 * 将查询结果转换为指定的类
	 *  
	 * @param resultHandler handle result set
	 * @param <T> result type
	 * @return the result object
	 */
	public <T> T getResult(final ResultHandler<T> resultHandler){
		if(!doExchange()){			
			queryCheck();
			
			return doExecute(new ResultExecutor<T>(resultHandler));	 
		}else{
			return null;
		}
	}
	
	public DataTable<DataMap> getList() {
		return getList(DataMap.class);
	}
	

	public <T> DataTable<T> getList(final Class<T> resultClass) {
		return getList(new ResultHandler<T>(this, resultClass));
	}
	
	
	/**
	 * @param limit 
	 *   The max number of records for this query
	 *    
	 * @param offset  
	 *   Base 0, the first record is 0
	 * @return List对象
	 */
	public DataTable<DataMap> getList(int limit,int offset) {		 
		return getList(DataMap.class,limit,offset);		 
	}
	
	public <T> DataTable<T> getList(Class<T> resultClass,int limit,int offset) {		 
		return getList(new ResultHandler<T>(this,resultClass),limit,offset);
	}	
	
	public <T> DataTable<T> getList(ResultHandler<T> resultHandler,int limit,int offset) {
		Query listQuery=getDialect().getLimitQuery(this, limit, offset);
		DataTable<T>  list=listQuery.getList(resultHandler);
			
		return list;
	}	 

	public <T> DataTable<T> getList(final ResultHandler<T> resultHandler) {
		if(!doExchange()){		 
			queryCheck();
			
			return doExecute(new ResultSetExecutor<T>(resultHandler));
		}else{
			return new DataTable<T>();
		}
	}
	
	public Page<DataMap> getPage(int limit,int offset) {
		return getPage(DataMap.class,limit, offset);
	}
	
	public <T> Page<T> getPage(Class<T> resultClass,int limit,int offset) {
		return getPage(new ResultHandler<T>(this,resultClass), limit, offset);
	}
	
	/**
	 * @param resultHandler handle result set
	 * @param limit The max number of records for this query
	 * @param offset   Base 0, the first record is 0
	 * @param <T> result type
	 * @return Page对象
	 */
	public <T> Page<T> getPage(ResultHandler<T> resultHandler,int limit,int offset) {
		if(!doExchange()){			
			queryCheck();
			
			Query countQuery=getDialect().getCountQuery(this);			
			long total=countQuery.getResult(Long.class);			
			 
			Query listQuery=getDialect().getLimitQuery(this, limit, offset);
			DataTable<T>  list=listQuery.getList(resultHandler);
			 
			Page<T> page=new Page<T>(list,total,limit,offset);
		
			return page;
		}else{
			return new Page<T>();		 
		}
	}
	
	public <T> T load(final T result){
		if(!doExchange()){			 
			queryCheck();
			
			ResultHandler<T> resultHandler=new ResultHandler<T>(this,(Class<T>)result.getClass());
			
			return doExecute(new ResultLoadExecutor<T>(resultHandler, result)); 
		}else{
			return result;
		}
	}
	 
	protected boolean doExchange(){
		DBExchange exchange=DBExchange.getExchange(false);
		if(exchange!=null){
			ResultHandler.processExchange(this,exchange);
			return true;
		}else{
			return false;
		}
	}
 	
	protected void queryCheck(){
		if(db==null){
			throw new RuntimeException("Query must use db!");
		}
	}
	
	public PrintWriter getPrintWriter(){
		if(writer==null){
			writer= new PrintWriter(new Writer(){
				public void write(char[] cbuf, int off, int len) throws IOException {
					 add(new String(cbuf,off,len));
				}

				public void flush() throws IOException {}
	
				public void close() throws IOException {}
			});
		}
		return writer;
	}
	
	protected void logSql(){
		boolean debug=false;
		if(debugSql==null){
			debug=  "true".equalsIgnoreCase( DbProp.PROP_DB_SQL_DEBUG.getValue(db));
		}else{
			debug=debugSql.booleanValue();
		}
		
		if(debug){
			logger.info(getExecutableSQL());
		}
	}
	  
	public CacheKey getCacheKey(){
		 CacheKey cacheKey = new CacheKey();
		    
		 cacheKey.update(db.getKey());
		 cacheKey.update(""+tag);
		 cacheKey.update(getSql());
		 
		 for(Object p:this.getParameters()){
			 cacheKey.update(p);
		 } 
		    
		return cacheKey;    
	}
	
	public Cache getCache(){
		if(this.cache!=null){
			return this.cache;
		}else if(enableCache){
			return getDb().getCfg().getCache();
		}else{
			return null;
		}
	}
	
	public void setCache(Cache cache){
		this.cache=cache;
	}
	 
	public DBConfig getDb() {
		return db;
	}

	public Query use(DBConfig db) {
		this.db = db;
		return this;
	}
	
	public Dialect getDialect(){
		if(db==null){
			throw new RuntimeException("Query must use db!");
		}
		
		return dsm.getDialect(db);
	}
      
	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isEnableCache() {
		return enableCache;
	}

	public Query setEnableCache(boolean enableCache) {
		this.enableCache = enableCache;
		return this;
	}
}