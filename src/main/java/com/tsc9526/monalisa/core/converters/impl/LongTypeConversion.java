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
package com.tsc9526.monalisa.core.converters.impl;

import java.util.Date;

import com.tsc9526.monalisa.core.converters.Conversion;

/**
 * 
 * @author zzg.zhou(11039850@qq.com)
 */
public class LongTypeConversion implements Conversion<Long> {

	public Object[] getTypeKeys() {
		return new Object[] {
			Long.class,
			Long.TYPE,
			Long.class.getName(),
			TYPE_LONG
		};
	}
 
	public Long convert(Object value) {
		if (!(value instanceof Long)) {
			if(value instanceof Date){
				return ((Date)value).getTime();
			}
			
			String v=value.toString();
			if (v.trim().length()==0) {
				value=null;
			}else {
				value=Long.parseLong(v);
			}
		}
		return (Long)value;
	}
}
