package com.overcomingroom.ulpet.place.controller;

import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.service.PlaceService;
import com.overcomingroom.ulpet.response.ResResult;
import com.overcomingroom.ulpet.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("")
    @Operation(summary = "장소 목록", description = "조건에 따른 장소 검색을 하고, 장소 목록을 불러 옵니다.")
    public ResponseEntity<ResResult> search(
            @RequestParam(value = "category", required = false) Category category, // 미입력 시 모든 카테고리 조회
            @RequestParam(value = "feature", required = false) String feature, // 미입력 시 모든 특징
            @RequestParam(value = "keyword", required = false) String searchKeyword // 검색할 키워드 ( 장소 명 또는 주소 )
    ) {

        ResponseCode resultCode = ResponseCode.PLACE_SEARCH;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(placeService.searchPlaces(category, feature, searchKeyword))
                        .build()

        );
    }
}