package br.com.lkm.taxone.mapper.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import br.com.lkm.taxone.mapper.converter.UploadConverter;
import br.com.lkm.taxone.mapper.dto.POCUser;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.dto.UploadDTO;
import br.com.lkm.taxone.mapper.entity.SAFXColumn;
import br.com.lkm.taxone.mapper.entity.SAFXTable;
import br.com.lkm.taxone.mapper.entity.Upload;
import br.com.lkm.taxone.mapper.entity.User;
import br.com.lkm.taxone.mapper.enums.ColumnType;
import br.com.lkm.taxone.mapper.enums.UploadStatus;
import br.com.lkm.taxone.mapper.repository.SAFXColumnRepository;
import br.com.lkm.taxone.mapper.repository.SAFXTableRepository;
import br.com.lkm.taxone.mapper.repository.UploadRepository;
import br.com.lkm.taxone.mapper.util.JExcelHelper;
import br.com.lkm.taxone.mapper.util.XLSField;
import br.com.lkm.taxone.mapper.util.XLSTable;

@Service
public class UploadService {
	
	@Autowired
	private SAFXTableRepository safxTableRepository; 
	
	@Autowired
	private SAFXColumnRepository safxColumnRepository;

	@Autowired
	private UploadRepository uploadReponsitory;

	public static void main(String[] args) throws IOException, Exception {
		String file = "C:\\jason\\work\\atividades\\mapeamento_envio_taxone\\Manual_Layout_MastersafDW.xls";
		new UploadService().parseFileAndStore("Manual_Layout_MastersafDW.xls", "256.1.0", Files.readAllBytes(Paths.get(file)));
	}
	
	@Transactional
	public void parseFileAndStore(String fileName, String layoutVersion, byte[] data) throws Exception {
		List<XLSTable> xlsTables = JExcelHelper.readSAFXINfo(data);

		xlsTables.stream().forEach(table -> {
//		XLSTable table = xlsTables.get(0);
			//Verify if table exists
			System.out.println("tName:" + table.getName());// + " - desc:" + table.getDescription() + " - fields:" + table.getFields());
			SAFXTable t = safxTableRepository.findByName(table.getName());
			boolean newTable = false;
			if (t == null) {
				newTable = true;
				t = new SAFXTable();
			}
			t.setName(table.getName());
			t.setDescription(table.getDescription());
			safxTableRepository.save(t);
				
			for (XLSField field : table.getFields()) {
				SAFXColumn c = null;
				if (newTable) {
					c = new SAFXColumn();
				}else {
					c = safxColumnRepository.findFirstBysafxTableIdAndName(t.getId(), field.getColumnName());
				}
				c.setName(field.getColumnName());
				c.setColumnType(getColumnType(field));
				c.setRequired(field.getRequired());
				try {
					c.setSize(Integer.parseInt(field.getSize()));
				}catch (Exception e) {
					c.setSize(3);
				}
				c.setPosition(field.getIndex());
				c.setSafxTable(t);
				safxColumnRepository.save(c);
			}
		});
		
		uploadReponsitory.updateStatus(UploadStatus.CANCELED);
		
		//POCUser user = (POCUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		POCUser user = new POCUser(3, "WE", "WE", null);
		
		Upload u = new Upload();
		u.setFileName(fileName);
		u.setLayoutVersion(layoutVersion);
		u.setCreationDate(LocalDateTime.now());
		u.setStatus(UploadStatus.ACTIVE);
		u.setUser(new User(user.getId()));
		uploadReponsitory.save(u);
	}

	private ColumnType getColumnType(XLSField field) {
		if (field.getType().equals("A")){
			return ColumnType.VARCHAR;
		}else if(field.getColumnName().startsWith("DT_") || field.getColumnName().startsWith("DAT_") 
				|| field.getColumnName().startsWith("DATA_") || field.getDescription().contains("Data")) {
			return ColumnType.DATETIME;
		}else {
			return ColumnType.NUMERIC;
		}
	}

	public PageResponse<UploadDTO> findAll(PageRequest page) {
		Page<Upload> uPage = uploadReponsitory.findAll(page);
		System.out.println("uPage:" + uPage.getTotalElements());
		PageResponse<UploadDTO> upResponse = new PageResponse<>();
		upResponse.setContent(uPage.getContent().stream().map(UploadConverter::convert).collect(Collectors.toList()));
		upResponse.setTotalPages(uPage.getTotalPages());
		return upResponse;
	}

	
}
