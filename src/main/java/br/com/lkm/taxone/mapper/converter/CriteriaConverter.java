package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.CriteriaDTO;
import br.com.lkm.taxone.mapper.entity.Criteria;

public class CriteriaConverter {

	public static CriteriaDTO convert(Criteria c) {
		CriteriaDTO cDTO = new CriteriaDTO();
		cDTO.setId(c.getId());
		cDTO.setSafxColumn(SAFXColumnConverter.convertCriteria(c.getSafxColumn()));
		cDTO.setOperator(c.getOperator());
		cDTO.setValue(c.getValue());
		cDTO.setAdditionalValue(c.getAdditionalValue());
		return cDTO;
	}
	
	public static Criteria convert(CriteriaDTO cDTO) {
		Criteria c = new Criteria();
		c.setId(cDTO.getId());
		c.setSafxColumn(SAFXColumnConverter.convert(cDTO.getSafxColumn()));
		c.setOperator(cDTO.getOperator());
		c.setValue(cDTO.getValue());
		c.setAdditionalValue(cDTO.getAdditionalValue());
		return c;
	}
}
