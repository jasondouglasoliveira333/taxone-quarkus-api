package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.UploadDTO;
import br.com.lkm.taxone.mapper.entity.Upload;

public class UploadConverter {

	public static UploadDTO convert(Upload upload) {
		UploadDTO uDTO = new UploadDTO();
		uDTO.setId(upload.getId());
		uDTO.setFileName(upload.getFileName());
		uDTO.setLayoutVersion(upload.getLayoutVersion());
		uDTO.setCreationDate(upload.getCreationDate());
		uDTO.setStatus(upload.getStatus());
		uDTO.setUserName(upload.getUser().getName());
		return uDTO;
	}
}
