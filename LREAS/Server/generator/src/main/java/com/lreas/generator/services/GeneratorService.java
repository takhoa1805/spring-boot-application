package com.lreas.generator.services;

import com.lreas.generator.dtos.GenerateFromFileDto;
import com.lreas.generator.dtos.GenerateResourceResponse;

public interface GeneratorService {
    GenerateResourceResponse generateFromResource(GenerateFromFileDto generateFromFileDto) throws Exception;
    Boolean stopGenerating(String resourceId, String userId);
}
