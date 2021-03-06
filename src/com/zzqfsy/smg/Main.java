package com.zzqfsy.smg;

import java.util.List;
import java.util.Map;

import com.zzqfsy.smg.generator.ControllerGenerator;
import com.zzqfsy.smg.generator.DaoGenerator;
import com.zzqfsy.smg.generator.JspGenerator;
import com.zzqfsy.smg.generator.JunitGenerator;
import com.zzqfsy.smg.generator.MapperGenerator;
import com.zzqfsy.smg.generator.ModelGenerator;
import com.zzqfsy.smg.generator.ProjectGenerator;
import com.zzqfsy.smg.generator.ServiceGenerator;
import com.zzqfsy.smg.util.DBUtils;
import com.zzqfsy.smg.util.FileUtils;
import com.zzqfsy.smg.util.PropertiesUtils;

public class Main {

	public static void main(String[] args) {
		FileUtils.createPackageDirectory();
		String primaryKey = null;
		List<String> tableList = null;
		try {
			tableList = PropertiesUtils.getTableList();
			if (tableList.size() == 0) {
				tableList = DBUtils.getAllTables();
			}
		} catch (Exception e) {
			System.err.println("connection exception, please check it.");
			return;
		}
		
		String project = PropertiesUtils.getProject();
		if (project != null && !"".equals(project)) {
			ProjectGenerator.generateProject(project,tableList);
			System.out.println(project + " framework has been generated.");
		}
		for (String tableName : tableList) {
			try {
				Map<String, String> pkMap = DBUtils.getPrimaryKey(tableName);
				primaryKey = pkMap.get("primaryKey");
			} catch (Exception e) {
				System.err.println(tableName + " doesn't exist or connection exception, please check it.");
				return;
			}
			if (primaryKey != null) {
				String layers = PropertiesUtils.getLayers();
				if(layers.contains("controller")){
					ControllerGenerator.generateController(tableName);
				}if(layers.contains("dao")){
					DaoGenerator.generateDao(tableName);
				}if(layers.contains("mapper")){
					MapperGenerator.generateMapper(tableName);
				}if(layers.contains("service")){
					ServiceGenerator.generateServiceAndImpl(tableName);
				}if(layers.contains("model")){
					ModelGenerator.generateModel(tableName);
				}if(layers.contains("jsp")){
					JspGenerator.generateJsp(tableName);
				}if(layers.contains("test")){
					JunitGenerator.generateJunit(tableName);
				}
				System.out.println(tableName + " has been generated.");
			} else {
				String layers = PropertiesUtils.getLayers();
				if(layers.contains("model")){
					ModelGenerator.generateModel(tableName);
				}
				System.err.println(tableName + " has no pk, ignored.");
			}
		}
		System.out.println("All finished.");
	}
}
