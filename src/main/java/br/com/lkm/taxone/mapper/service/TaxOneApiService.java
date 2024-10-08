package br.com.lkm.taxone.mapper.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lkm.taxone.mapper.dto.TaxOneApiDTO;
import br.com.lkm.taxone.mapper.entity.TaxOneApi;
import br.com.lkm.taxone.mapper.repository.TaxOneApiRepository;

@Service
public class TaxOneApiService {

	@Autowired
	private TaxOneApiRepository taxOneApiRepository;
	
	@Autowired
	private ModelMapper modelMapper; 

	public TaxOneApiDTO getOne(Integer id) {
		TaxOneApi taxone = taxOneApiRepository.getOne(id);
		TaxOneApiDTO toDTO = modelMapper.map(taxone, TaxOneApiDTO.class);
		return toDTO;
	}

	public void save(TaxOneApiDTO toDTO) {
		TaxOneApi to = modelMapper.map(toDTO, TaxOneApi.class);
		taxOneApiRepository.save(to);
	}
}
