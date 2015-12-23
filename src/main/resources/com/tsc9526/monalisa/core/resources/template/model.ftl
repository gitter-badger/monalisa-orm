package ${table.javaPackage};

<#include "functions.ftl">
 		
<#list imports as i>
import ${i};
</#list>

/**
 * Created by monalisa at ${.now}
 */ 
@Table(
	name="${table.name}",
	primaryKeys={<#list table.keyColumns as k>"${k.name}"<#if k_has_next=true>, </#if></#list>},
	remarks="${table.remarks!?replace('"','\\"')?replace('\r','\\r')?replace('\n','\\n')}",
	indexes={		
		<#list table.indexes as index>		
		@Index(name="${index.name}", type=${index.type}, unique=${index.unique?c}, fields={<#list index.columns as c>"${c.name}"<#if c_has_next=true>,</#if></#list>})<#if index_has_next=true>,
		</#if>
		</#list>
	}
)
public class ${table.javaName} extends ${modelClass}<${table.javaName}> implements ${dbi}{
	private static final long serialVersionUID = ${table.serialID?c}L;
		 
	public static final Insert INSERT(){
	 	return new Insert(new ${table.javaName}());
	}
	
	public static final Delete DELETE(){
	 	return new Delete(new ${table.javaName}());
	}
	
	public static final Update UPDATE(${table.javaName} model){
		return new Update(model);
	}		
	
	public static final Select SELECT(){
	 	return new Select(new ${table.javaName}());
	}	 	 
	
	/**
	* Simple query with example <br>
	* 
	* Replaced with WHERE()
	*/
	@Deprecated
	public static Criteria criteria(){
		return new Example().createCriteria();
	} 
	
	
	/**
	* Simple query with example <br>
	* 
	*/
	public static Criteria WHERE(){
		return new Example().createCriteria();
	} 
	 
	public ${table.javaName}(){
		super("${table.name}"<#list table.keyColumns as k>, "${k.name}"</#list>);		
	}		 
	
	<#if table.keyColumns?size gt 0 >
	/**
	 * Constructor use primary keys.
	 *
	 <#list table.keyColumns as k>
	 * @param ${k.javaName} ${k.remarks?html?replace('*/','**')}
	 </#list>	 
	 */
	public ${table.javaName}(<#list table.keyColumns as k>${k.javaType} ${k.javaName}<#if k_has_next=true>, </#if></#list>){
		super("${table.name}"<#list table.keyColumns as k>, "${k.name}"</#list>);
	
		<#list table.keyColumns as k>
		this.${k.javaName} = ${k.javaName};
		fieldChanged("${k.javaName}");
		</#list>
	}	 
	</#if>
	
	<#list table.columns as f>
	<@comments table=table c=f align="	"/> 
	<#if f.code.annotation??>
	<#list c.code.annotation?split("\n") as a>
	${a}
	</#list>
	</#if>
	private ${f.javaType} ${f.javaName};	
	
	</#list>
	
	
	<#list table.columns as f>   
	<@comments table=table c=f align="	"/> 
	public ${table.javaName} ${f.javaNameSet}(${f.javaType} ${f.javaName}){
		<#if f.code.set??>
		${f.code.set}
		<#else>
		this.${f.javaName} = ${f.javaName};		
		</#if>
		
		fieldChanged("${f.javaName}");
		
		return this;
	}
	
	<#if f.code.list??>
	<@comments table=table c=f align="	"/> 
	<#assign listType=f.javaType?substring(5, f.javaType?length - 1 )>
	public ${table.javaName} ${f.javaNameSet?replace('set','add','f')}(${listType}... ${f.javaName}){
		if(this.${f.javaName} == null){
			this.${f.javaName}=new Array${f.javaType}();
		}
		
		for(${listType} x:${f.javaName}){
			this.${f.javaName}.add(x);
		}
		
		fieldChanged("${f.javaName}");
		
		return this;
	}
	</#if>
	
	</#list>
	
	
	<#list table.columns as f>   
	<@comments table=table c=f align="	"/> 
	public ${f.javaType} ${f.javaNameGet}(){
		<#if f.code.get??>
		${f.code.get}
		<#elseif f.code.value??>
		if(this.${f.javaName}==null){
			return ${f.code.value};		
		}
		return this.${f.javaName};
		<#else>
		return this.${f.javaName};
		</#if>	
	}
		
	</#list>
	
	public static class Insert extends com.tsc9526.monalisa.core.query.dao.Insert<${table.javaName}>{
		Insert(${table.javaName} model){
			super(model);
		}	 
	}	
	
	public static class Delete extends com.tsc9526.monalisa.core.query.dao.Delete<${table.javaName}>{
		Delete(${table.javaName} model){
			super(model);
		}
		 
		<#if table.keyColumns?size gt 0 >
		public int deleteByPrimaryKey(<#list table.keyColumns as k>${k.javaType} ${k.javaName}<#if k_has_next=true>, </#if></#list>){
			<#list table.keyColumns as k>
			if(${k.javaName} ==null ) return 0;			
			</#list>
						 			 
			<#list table.keyColumns as k>
			this.model.${k.javaName} = ${k.javaName};
			</#list>
				 			 
			return this.model.delete();				
		}				 
		</#if>
		
		<#list table.indexes as index>
		<#assign m='' />			 
		<#list index.columns as c>
			<#assign m= m + c.javaName?cap_first />				 
		</#list>
		<#if m='PrimaryKey'><#assign m= 'UKPrimaryKey'/></#if>
		<#if index.unique> 
		/**
		* Delete by unique key: ${index.name}
		<#list index.columns as c>
		* @param ${c.javaName} ${c.remarks?html?replace('*/','**')}
		</#list>	
		*/
		public int deleteBy${m}(<#list index.columns as k>${k.javaType} ${k.javaName}<#if k_has_next=true>, </#if></#list>){			 
			<#list index.columns as k>
			this.model.${k.javaName}=${k.javaName};
			</#list>			 
			 
			return this.model.delete();
		}			 
		</#if>		
		
		</#list>
		
	}
	
	public static class Update extends com.tsc9526.monalisa.core.query.dao.Update<${table.javaName}>{
		Update(${table.javaName} model){
			super(model);
		}		 			 			 		
	}
	
	public static class Select extends com.tsc9526.monalisa.core.query.dao.Select<${table.javaName},Select>{		
		Select(${table.javaName} x){
			super(x);
		}					 
		
		/**
		* find model by primary keys
		*
		* @return the model associated with the primary keys,  null if not found.
		*/
		<#if table.keyColumns?size gt 0 >
		public ${table.javaName} selectByPrimaryKey(<#list table.keyColumns as k>${k.javaType} ${k.javaName}<#if k_has_next=true>, </#if></#list>){
			<#list table.keyColumns as k>
			if(${k.javaName} ==null ) return null;			
			</#list>
						
			<#list table.keyColumns as k>
			this.model.${k.javaName} = ${k.javaName};
			</#list>
			this.model.load();
				 			 	 
			if(this.model.entity()){
				return this.model;
			}else{
				return null;
			}
		}				 
		</#if>
		
		<#list table.indexes as index>
		<#assign m='' />			 
		<#list index.columns as c>
			<#assign m= m + c.javaName?cap_first />				 
		</#list>
		<#if m='PrimaryKey'><#assign m= 'UKPrimaryKey'/></#if>
		<#if index.unique> 
		/**
		* Find by unique key: ${index.name}
		<#list index.columns as c>
		* @param ${c.javaName} ${c.remarks?html?replace('*/','**')}
		</#list>	
		*/
		public ${table.javaName} selectBy${m}(<#list index.columns as k>${k.javaType} ${k.javaName}<#if k_has_next=true>, </#if></#list>){
			Criteria c=criteria();
			<#list index.columns as k>
			c.${k.javaName}.eq(${k.javaName});
			</#list>			 
			 
			return super.selectOneByExample(c.example);
		}			 
		
		<#if index.columns?size = 1>
		<#assign k=index.columns[0]>
		/**
		* List result to Map, The map key is unique-key: ${k.name} 
		*/
		public Map<${k.javaType},${table.javaName}> selectToMapWith${k.javaName?cap_first}(String whereStatement,Object ... args){
			List<${table.javaName}> list=super.select(whereStatement,args);
			
			Map<${k.javaType},${table.javaName}> m=new LinkedHashMap<${k.javaType},${table.javaName}>();
			for(${table.javaName} x:list){
				m.put(x.${k.javaNameGet}(),x);
			}
			return m;
		}
		
		/**
		* List result to Map, The map key is unique-key: ${k.name} 
		*/
		public Map<${k.javaType},${table.javaName}> selectByExampleToMapWith${k.javaName?cap_first}(Example example){
			List<${table.javaName}> list=super.selectByExample(example);
			
			Map<${k.javaType},${table.javaName}> m=new LinkedHashMap<${k.javaType},${table.javaName}>();
			for(${table.javaName} x:list){
				m.put(x.${k.javaNameGet}(),x);
			}
			return m;
		}
		</#if>
		</#if>
		</#list>
				
		<#if table.keyColumns?size = 1 >
		<#assign k=table.keyColumns[0]>
		/**
		* List result to Map, The map key is primary-key: ${k.name} 
		*/
		public Map<${k.javaType},${table.javaName}> selectToMap(String whereStatement,Object ... args){
			List<${table.javaName}> list=super.select(whereStatement,args);
			
			Map<${k.javaType},${table.javaName}> m=new LinkedHashMap<${k.javaType},${table.javaName}>();
			for(${table.javaName} x:list){
				m.put(x.${k.javaNameGet}(),x);
			}
			return m;
		}
	
		/**
		* List result to Map, The map key is primary-key: ${k.name} 
		*/
		public Map<${k.javaType},${table.javaName}> selectByExampleToMap(Example example){
			List<${table.javaName}> list=super.selectByExample(example);
			
			Map<${k.javaType},${table.javaName}> m=new LinkedHashMap<${k.javaType},${table.javaName}>();
			for(${table.javaName} x:list){
				m.put(x.${k.javaNameGet}(),x);
			}
			return m;
		}
		</#if>
	}
	 
		
	public static class Example extends com.tsc9526.monalisa.core.query.criteria.Example<Criteria,${table.javaName}>{
		public Example(){}
		 
		protected Criteria createInternal(){
			Criteria x= new Criteria(this);
			
			Class<?> clazz=ClassHelper.findClassWithAnnotation(${table.javaName}.class,DB.class);			  			
			com.tsc9526.monalisa.core.query.criteria.QEH.getQuery(x).use(dsm.getDBConfig(clazz));
			
			return x;
		}
		
		<#if table.keyColumns?size = 1 >
		<#assign k=table.keyColumns[0]>
		/**
		* List result to Map, The map key is primary-key: ${k.name} 
		*/
		public Map<${k.javaType},${table.javaName}> selectToMap(){			
			List<${table.javaName}> list=SELECT().selectByExample(this);
			
			Map<${k.javaType},${table.javaName}> m=new LinkedHashMap<${k.javaType},${table.javaName}>();
			for(${table.javaName} x:list){
				m.put(x.${k.javaNameGet}(),x);
			}
			return m;
		}
		</#if>
		
	}
	
	public static class Criteria extends com.tsc9526.monalisa.core.query.criteria.Criteria<Criteria>{
		
		private Example example;
		
		private Criteria(Example example){
			this.example=example;
		}
		
		/**
		 * Create Select for example
		 */
		public Select.SelectForExample forSelect(){
			return SELECT().selectForExample(this.example);
		}
		
		/**
		 * Create Update for example, Ignore update the primary key;
		 *
		 * @see #forUpdate(${table.javaName},boolean)
		 */
		public Update.UpdateForExample forUpdate(${table.javaName} m){			 
			Update update=new Update(m);
			return update.updateForExample(this.example);
		}
		
		/**
		 * Create Delete for example
		 */
		public Delete.DeleteForExample forDelete(){
			return DELETE().deleteForExample(this.example);
		}
		
		<#list table.columns as f>
		<@comments table=table c=f align="		"/>
		<#if f.javaType ="Integer">
		public com.tsc9526.monalisa.core.query.criteria.Field.FieldInteger<Criteria> ${f.javaName} = new com.tsc9526.monalisa.core.query.criteria.Field.FieldInteger<Criteria>("${f.name}", this);
		<#elseif f.javaType= "Short">
		public com.tsc9526.monalisa.core.query.criteria.Field.FieldShort<Criteria> ${f.javaName} = new com.tsc9526.monalisa.core.query.criteria.Field.FieldShort<Criteria>("${f.name}", this);
		<#elseif f.javaType= "Long">
		public com.tsc9526.monalisa.core.query.criteria.Field.FieldLong<Criteria> ${f.javaName} = new com.tsc9526.monalisa.core.query.criteria.Field.FieldLong<Criteria>("${f.name}", this); 
		<#elseif f.javaType= "String">
		public com.tsc9526.monalisa.core.query.criteria.Field.FieldString<Criteria> ${f.javaName} = new com.tsc9526.monalisa.core.query.criteria.Field.FieldString<Criteria>("${f.name}", this);
		<#else>
		public com.tsc9526.monalisa.core.query.criteria.Field<${f.javaType},Criteria> ${f.javaName} = new com.tsc9526.monalisa.core.query.criteria.Field<${f.javaType},Criteria>("${f.name}", this, ${f.jdbcType});		 
		</#if>		
		
		</#list>
		 
	}
	 
	<#compress> 
	<#list table.columns as f>	 
	<#if f.code.enum?? && f.code.enum?index_of('{')!=-1>
	public static enum ${f.code.enum} 
	</#if>
			 
	</#list>
	</#compress>
	 
	public static class M{
		public final static String TABLE ="${table.name}" ;
		
		<#list table.columns as f>
		<@comments table=table c=f align="		"/>
		public final static String  ${f.javaName}         = "${f.name}" ;
		
		public final static String  ${f.javaName}$name    = "${f.name}" ;
		public final static boolean ${f.javaName}$key     = ${f.key?string("true","false")} ;
		public final static int     ${f.javaName}$length  = ${f.length?c} ;
		public final static String  ${f.javaName}$value   = "<#if f.value??>${f.value}<#else>NULL</#if>" ;
		public final static String  ${f.javaName}$remarks = "${f.remarks!?replace('"','\\"')?replace('\r','\\r')?replace('\n','\\n')}" ;
		public final static boolean ${f.javaName}$auto    = ${f.auto?string("true","false")} ;
		public final static boolean ${f.javaName}$notnull = ${f.notnull?string("true","false")} ;
		
		</#list>		 
	}
}
