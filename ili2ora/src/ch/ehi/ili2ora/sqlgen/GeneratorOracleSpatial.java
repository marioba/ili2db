package ch.ehi.ili2ora.sqlgen;

import java.io.IOException;

import ch.ehi.ili2db.base.DbNames;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.ehi.sqlgen.repository.DbColBoolean;
import ch.ehi.sqlgen.repository.DbColDate;
import ch.ehi.sqlgen.repository.DbColDateTime;
import ch.ehi.sqlgen.repository.DbColDecimal;
import ch.ehi.sqlgen.repository.DbColGeometry;
import ch.ehi.sqlgen.repository.DbColId;
import ch.ehi.sqlgen.repository.DbColNumber;
import ch.ehi.sqlgen.repository.DbColTime;
import ch.ehi.sqlgen.repository.DbColUuid;
import ch.ehi.sqlgen.repository.DbColVarchar;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbIndex;
import ch.ehi.sqlgen.repository.DbTable;

public class GeneratorOracleSpatial extends GeneratorJdbc {
	@Override
	public void visitColumn(DbTable dbTab,DbColumn column) throws IOException {
		String type="";
		String size="";
		String notSupported=null;
		
		if(column instanceof DbColBoolean){
			type="NUMBER(1)";
		}else if(column instanceof DbColDateTime){
			type="TIMESTAMP";
		}else if(column instanceof DbColDate){
			type="DATE";
		}else if(column instanceof DbColTime){
			type="TIME";
		}else if(column instanceof DbColDecimal){
			DbColDecimal col=(DbColDecimal)column;
			type="DECIMAL("+Integer.toString(col.getSize())+","+Integer.toString(col.getPrecision())+")";
		}else if(column instanceof DbColGeometry){
			type="MDSYS.SDO_GEOMETRY";
		}else if(column instanceof DbColId){
			type="NUMBER(9)";
		}else if(column instanceof DbColUuid){
			type="VARCHAR2(36)";
		}else if(column instanceof DbColNumber){
			DbColNumber col=(DbColNumber)column;
			type="NUMBER("+Integer.toString(col.getSize())+")";
		}else if(column instanceof DbColVarchar){
			int colsize=((DbColVarchar)column).getSize();
			
			if(colsize!=DbColVarchar.UNLIMITED) {
				type="VARCHAR2("+Integer.toString(colsize)+")";
			}else {
				type="CLOB";
			}
		}else{
			type="VARCHAR2(20)";
		}
		String isNull=column.isNotNull()?"NOT NULL":"NULL";
		if(column instanceof DbColId){
			if(((DbColId)column).isPrimaryKey()){
				isNull="PRIMARY KEY";
			}
		}
		String name=column.getName();
		
		if(name.equals(DbNames.MODELS_TAB_FILE_COL) && dbTab.getName().getName().equals(DbNames.MODELS_TAB)) {
			name = "\"" + name + "\"";
		}
		
		out.write(getIndent()+colSep+name+" "+type+" "+isNull+newline());
		colSep=",";
	}
	
	@Override
	public void visitIndex(DbIndex idx) throws IOException {
		if(!idx.isPrimary())
			super.visitIndex(idx);
	}
}
