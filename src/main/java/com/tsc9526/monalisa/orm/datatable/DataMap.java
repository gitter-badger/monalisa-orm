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
package com.tsc9526.monalisa.orm.datatable;

import java.util.Date;
import java.util.Map;

import com.tsc9526.monalisa.orm.tools.helper.CaseInsensitiveMap;
import com.tsc9526.monalisa.orm.tools.helper.ClassHelper;
import com.tsc9526.monalisa.orm.tools.helper.ClassHelper.FGS;
import com.tsc9526.monalisa.orm.tools.helper.ClassHelper.MetaClass;
import com.tsc9526.monalisa.orm.tools.helper.Helper;

/**
 * 
 * @author zzg.zhou(11039850@qq.com)
 */
public class DataMap extends CaseInsensitiveMap<Object>{ 
	private static final long serialVersionUID = -8132926422921115814L;	
	 
	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> toClass){
		try {
			if(toClass.isAssignableFrom(DataMap.class)){
				return (T)this;
			}
			
			T r = toClass.newInstance();
			
			MetaClass mc=ClassHelper.getMetaClass(toClass);
			for(FGS fgs:mc.getFields()){
				String name=fgs.getFieldName();
				if(containsKey(name)){
					fgs.setObject(r, get(name));
				}
			} 
			
			return r;
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				throw (RuntimeException)e;
			}else{
				throw new RuntimeException(e);
			}
		}
		
	}
	 
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @return Object value
	 */
	@SuppressWarnings("unchecked")
	public Object get(int index){
		Map.Entry<String,Object> entry=(Map.Entry<String,Object>)this.entrySet().toArray()[index];
		
		Object v=entry.getValue();
		return v;
	}
	
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @param defaultValue if null, return the defaultValue
	 * @return string value
	 */
	public String getString(int index,String defaultValue){
		String v=getString(index);
		if(v==null){
			v= defaultValue;
		}
		return v;
	}
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @return string value
	 */
	public String getString(int index){		 
		Object v=get(index);
		if(v==null){
			return null;
		}else {
			return v.toString();
		}
	}
	
	public String getString(String key,String defaultValue){
		String v=getString(key);
		if(v==null){
			v= defaultValue;
		}
		return v;
	}
	
	public String getString(String key){
		Object v=get(key);
		if(v==null){
			return null;
		}else {
			return v.toString();
		}
	}
	
	public boolean getBool(String key,boolean defaultValue){
		Boolean v=getBoolean(key);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @param defaultValue  if null , return the defaultValue
	 * @return boolean value
	 */
	public boolean getBool(int index,boolean defaultValue){
		Boolean v=getBoolean(index);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	public Boolean getBoolean(String key){
		Object v=(Object)get(key);
		if(v==null){
			return null;
		}else{
			if(v instanceof Boolean){
				return (Boolean)v;
			}else{
				return Boolean.valueOf(""+v);
			}
		}
	}
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @return boolean result
	 */
	public Boolean getBoolean(int index){
		Object v=(Object)get(index);
		if(v==null){
			return null;
		}else{
			if(v instanceof Boolean){
				return (Boolean)v;
			}else{
				return Boolean.valueOf(""+v);
			}
		}
	}
	
	public int getInt(String key, int defaultValue){
		Integer v=getInteger(key);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @param defaultValue if null, return defaultValue
	 * @return int result
	 */
	public int getInt(int index, int defaultValue){
		Integer v=getInteger(index);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	public Integer getInteger(String key){
		Object v=(Object)get(key);
		if(v==null){
			return null;
		}else{
			if(v instanceof Integer){
				return (Integer)v;
			}else if(v instanceof Double){
				return ((Double)v).intValue();
			}else{
				return Integer.parseInt(""+v);
			}
		}
	}
	
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @return integer result
	 */
	public Integer getInteger(int index){
		Object v=(Object)get(index);
		if(v==null){
			return null;
		}else{
			if(v instanceof Integer){
				return (Integer)v;
			}else if(v instanceof Double){
				return ((Double)v).intValue();
			}else{
				return Integer.parseInt(""+v);
			}
		}
	}
	

	public long getLong(String key, long defaultValue){
		Long v=getLong(key);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
		/**
	 * 
	 * @param index  The first key is 0.
	 * @param defaultValue if null, return defaultValue
	 * @return long result
	 */
	public long getLong(int index, long defaultValue){
		Long v=getLong(index);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	public Long getLong(String key){
		Object v=(Object)get(key);
		if(v==null){
			return null;
		}else{
			if(v instanceof Long){
				return (Long)v;
			}else if(v instanceof Double){
				return ((Double)v).longValue();
			}else{
				return Long.parseLong(""+v);
			}
		}
	}
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @return long result
	 */
	public Long getLong(int index){
		Object v=(Object)get(index);
		if(v==null){
			return null;
		}else{
			if(v instanceof Long){
				return (Long)v;
			}else if(v instanceof Double){
				return ((Double)v).longValue();
			}else{
				return Long.parseLong(""+v);
			}
		}
	}
	
	
	public float getFloat(String key, float defaultValue){
		Float v=getFloat(key);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @param defaultValue if null, return defaultValue
	 * @return float result
	 */
	public float getFloat(int index, float defaultValue){
		Float v=getFloat(index);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	public Float getFloat(String key){
		Object v=(Object)get(key);
		if(v==null){
			return null;
		}else{
			if(v instanceof Float){
				return (Float)v;
			}else{
				return Float.parseFloat(""+v);
			}
		}
	}
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @return float result
	 */
	public Float getFloat(int index){
		Object v=(Object)get(index);
		if(v==null){
			return null;
		}else{
			if(v instanceof Float){
				return (Float)v;
			}else{
				return Float.parseFloat(""+v);
			}
		}
	}
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @param defaultValue if null, return defaultValue
	 * @return double result
	 */
	public double getDouble(int index, double defaultValue){
		Double v=getDouble(index);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	public double getDouble(String key, double defaultValue){
		Double v=getDouble(key);
		if(v==null){
			v=defaultValue;
		}
		return v;
	}
	
	public Double getDouble(String key){
		Object v=(Object)get(key);
		if(v==null){
			return null;
		}else{
			if(v instanceof Double){
				return (Double)v;
			}else{
				return Double.parseDouble(""+v);
			}
		}
	}
	
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @return double result
	 */
	public Double getDouble(int index){
		Object v=(Object)get(index);
		if(v==null){
			return null;
		}else{
			if(v instanceof Double){
				return (Double)v;
			}else{
				return Double.parseDouble(""+v);
			}
		}
	}

	/**
	 * 
	 * @param index  The first key is 0.
	 * @return date value
	 */	
	public Date getDate(int index){
		return Helper.toDate(get(index), null, null);
	}
	
	/**
	 * 
	 * @param index  The first key is 0.
	 * @param format  new SimpleDateFormat(format): auto detect date format if null or ''
	 * @param defaultValue return this value if null
	 * @return date value
	 */
	public Date getDate(int index,String format,Date defaultValue){		 
		return Helper.toDate(get(index), format, defaultValue);
	}
	
	/**
	 * Auto detect the date format:<br>
	 * yyyy-MM-dd<br>
	 * yyyy-MM-dd HH<br>
	 * yyyy-MM-dd HH:mm<br>
	 * yyyy-MM-dd HH:mm:ss<br>
	 *   
	 * @param key label of the column
	 * 
	 * @return date value
	 */
	public Date getDate(String key){
		return getDate(key,null,null);
	}
	
	/**
	 * @param key label of the column
	 * @param defaultValue return this value if null 
	 * @return date value
	 * 
	 * @see #getDate(String)
	 */
	public Date getDate(String key, Date defaultValue){
		return getDate(key,null,defaultValue);		  
	}
	
	/**
	 * 
	 * @param key label of the column
	 * @param format  new SimpleDateFormat(format): auto detect date format if null or ''
	 * @param defaultValue return this value if null
	 * @return date value
	 * 
	 * @see #getDate(String)
	 */
	public Date getDate(String key,String format,Date defaultValue){
		return Helper.toDate(get(key), format, defaultValue); 
	}
}