package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.DataSourceDTO;
import br.com.lkm.taxone.mapper.entity.DataSourceConfiguration;

public class DataSourceConfigConverter {
	public static DataSourceDTO convert(DataSourceConfiguration dsc) {
		DataSourceDTO dsd = new DataSourceDTO();
		dsd.setId(dsc.getId());
		dsd.setUrl(dsc.getUrl());
		dsd.setUsername(dsc.getUsername());
		dsd.setPassword(dsc.getPassword());
		dsd.setResourceNames(dsc.getResourceNames());
		dsd.setDataSourceType(dsc.getDataSourceType());
		return dsd;
	}

	public static DataSourceConfiguration convert(DataSourceDTO dsd) {
		DataSourceConfiguration dsc = new DataSourceConfiguration();
		dsc.setId(dsd.getId());
		dsc.setUrl(dsd.getUrl());
		dsc.setUsername(dsd.getUsername());
		dsc.setPassword(dsd.getPassword());
		dsc.setResourceNames(dsd.getResourceNames());
		dsc.setDataSourceType(dsd.getDataSourceType());
		return dsc;
	}
}
